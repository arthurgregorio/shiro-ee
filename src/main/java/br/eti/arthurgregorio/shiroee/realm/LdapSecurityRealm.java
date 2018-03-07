package br.eti.arthurgregorio.shiroee.realm;

import br.eti.arthurgregorio.shiroee.auth.AuthenticationMechanism;
import br.eti.arthurgregorio.shiroee.auth.NullAuthenticationMechanism;
import br.eti.arthurgregorio.shiroee.config.ConfigurationFactory;
import br.eti.arthurgregorio.shiroee.config.Constants;
import br.eti.arthurgregorio.shiroee.config.ldap.LdapRepository;
import static br.eti.arthurgregorio.shiroee.config.messages.Messages.ACCOUNT_NOT_FOUND;
import static br.eti.arthurgregorio.shiroee.config.messages.Messages.BIND_ERROR;
import javax.naming.NamingException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.ldap.DefaultLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.subject.PrincipalCollection;
import static br.eti.arthurgregorio.shiroee.config.messages.Messages.AUTHENTICATION_ERROR;
import static br.eti.arthurgregorio.shiroee.config.messages.Messages.LDAP_CONFIGURATION_INCOMPLETE;
import static br.eti.arthurgregorio.shiroee.config.messages.Messages.LDAP_REPOSITORY_ERROR;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.naming.ldap.LdapContext;
import org.apache.commons.configuration2.PropertiesConfiguration;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.realm.ldap.JndiLdapContextFactory;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 28/12/2017
 */
public class LdapSecurityRealm extends DefaultLdapRealm {

    private LdapRepository ldapRepository;
    
    private final PropertiesConfiguration configuration;
    private final AuthenticationMechanism authenticationMechanism;

    /**
     * 
     * @param authenticationMechanism 
     */
    public LdapSecurityRealm(AuthenticationMechanism authenticationMechanism) {
        this.configuration = ConfigurationFactory.get();
        
        // if not authentication mechanism is provided, use a null one
        if (authenticationMechanism == null) {
            this.authenticationMechanism = new NullAuthenticationMechanism();
        } else {
            this.authenticationMechanism = authenticationMechanism;
        }
        
        super.setContextFactory(this.configureLdapContextFactory());
    }

    /**
     *
     * @param token
     * @param factory
     * @return
     * @throws NamingException
     */
    @Override
    protected AuthenticationInfo queryForAuthenticationInfo(AuthenticationToken token,
            LdapContextFactory factory) throws NamingException {

        final String username = String.valueOf(token.getPrincipal());

        if (this.authenticationMechanism.isAuthorized(username)) {
            return super.queryForAuthenticationInfo(token, factory);
        }

        throw new AuthenticationException(AUTHENTICATION_ERROR.format(username));
    }

    /**
     *
     * @param principalCollection
     * @param factory
     * @return
     * @throws NamingException
     */
    @Override
    protected AuthorizationInfo queryForAuthorizationInfo(PrincipalCollection principalCollection,
            LdapContextFactory factory) throws NamingException {

        final String username = (String) this.getAvailablePrincipal(principalCollection);

        final Set<String> permissions = this.authenticationMechanism
                .getPermissionsFor(username);

        final SimpleAuthorizationInfo authorizationInfo
                = new SimpleAuthorizationInfo();

        authorizationInfo.setStringPermissions(permissions);

        return authorizationInfo;
    }

    /**
     *
     * @param principal
     * @return
     */
    @Override
    protected String getUserDn(String principal) {
        try {
            return this.ldapRepository.search(principal)
                    .orElseThrow(() -> new AuthenticationException(
                            ACCOUNT_NOT_FOUND.format(principal)))
                    .getDistinguishedName();
        } catch (NamingException ex) {
            throw new AuthenticationException(BIND_ERROR.format(principal), ex);
        }
    }

    /**
     * 
     * @return 
     */
    @Produces
    @ApplicationScoped
    public LdapRepository produceLdapRepository() {
        
        if (this.ldapRepository == null) {

            final String baseDn = this.configuration.getString(
                    "ldap.baseDn", Constants.LDAP_BASE_DN);
            final String searchFilter = this.configuration.getString(
                    "ldap.searchFilter", Constants.LDAP_SEARCH_FILTER);

            try {
                final LdapContext context = 
                        this.getContextFactory().getSystemLdapContext();

                this.ldapRepository = new LdapRepository(baseDn, searchFilter, context);
            } catch (NamingException ex) {
                throw new ConfigurationException(LDAP_REPOSITORY_ERROR.format(), ex);
            }
        }

        return this.ldapRepository;
    }
    
    /**
     *
     * @return
     */
    private JndiLdapContextFactory configureLdapContextFactory() {

        final JndiLdapContextFactory factory = new JndiLdapContextFactory();

        final String ldapUrl = this.configuration.getString("ldap.url");
        final String ldapUser = this.configuration.getString("ldap.user");
        final String ldapPassword = this.configuration.getString("ldap.password");

        // if one of the base configurations was not provided, throw error
        if (isBlank(ldapUser) || isBlank(ldapPassword) || isBlank(ldapUrl)) {
            throw new ConfigurationException(LDAP_CONFIGURATION_INCOMPLETE.format());
        }

        factory.setUrl(ldapUrl);
        factory.setSystemUsername(ldapUser);
        factory.setSystemPassword(ldapPassword);

        factory.setPoolingEnabled(true);

        return factory;
    }
}
