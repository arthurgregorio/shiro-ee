package br.eti.arthurgregorio.shiroee.realm;

import br.eti.arthurgregorio.shiroee.auth.AuthenticationMechanism;
import br.eti.arthurgregorio.shiroee.auth.EmptyAuthenticationMechanism;
import br.eti.arthurgregorio.shiroee.config.jdbc.UserDetails;
import br.eti.arthurgregorio.shiroee.config.ldap.LdapUserProvider;
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
import java.util.Set;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 28/12/2017
 */
public class LdapSecurityRealm extends DefaultLdapRealm {

    private final LdapUserProvider ldapUserProvider;
    private final AuthenticationMechanism authenticationMechanism;

    /**
     * 
     * @param ldapUserProvider
     * @param authenticationMechanism 
     */
    public LdapSecurityRealm(LdapUserProvider ldapUserProvider, AuthenticationMechanism authenticationMechanism) {
        
        this.ldapUserProvider = ldapUserProvider;
        
        // if not authentication mechanism is provided, use a null one
        if (authenticationMechanism == null) {
            this.authenticationMechanism = new EmptyAuthenticationMechanism();
        } else {
            this.authenticationMechanism = authenticationMechanism;
        }
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

        final UserDetails userDetails = 
                this.authenticationMechanism.getUserDetails(username);
        
        if (userDetails.isLdapBindAccount() && !userDetails.isBlocked()) {
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
        return this.ldapUserProvider.search(principal)
                .orElseThrow(() -> new AuthenticationException(
                        AUTHENTICATION_ERROR.format(principal)))
                .getDistinguishedName();
    }
}
