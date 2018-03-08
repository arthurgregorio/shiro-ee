package br.eti.arthurgregorio.shiroee.config;

import static br.eti.arthurgregorio.shiroee.config.Constants.ANONYMOUS_OP;
import static br.eti.arthurgregorio.shiroee.config.Constants.AUTHENTICATED_OP;
import static br.eti.arthurgregorio.shiroee.config.Constants.LOGOUT_OP;
import static br.eti.arthurgregorio.shiroee.config.Constants.REQUIRED_PERMISSION_OP;
import static br.eti.arthurgregorio.shiroee.config.Constants.REQUIRED_ROLE_OP;
import static br.eti.arthurgregorio.shiroee.config.Constants.URL_LOGIN;
import static br.eti.arthurgregorio.shiroee.config.Constants.URL_LOGIN_SUCCESS;
import static br.eti.arthurgregorio.shiroee.config.Constants.URL_LOGOUT_PATH;
import static br.eti.arthurgregorio.shiroee.config.Constants.URL_ROOT_SECURED_PATH;
import static br.eti.arthurgregorio.shiroee.config.Constants.URL_UNAUTHORIZED;
import br.eti.arthurgregorio.shiroee.config.ldap.DefaultLdapUserProvider;
import br.eti.arthurgregorio.shiroee.config.ldap.LdapUserProvider;
import static br.eti.arthurgregorio.shiroee.config.messages.Messages.INSTANCE_IS_INVALID;
import static br.eti.arthurgregorio.shiroee.config.messages.Messages.LDAP_CONFIGURATION_INCOMPLETE;
import static br.eti.arthurgregorio.shiroee.config.messages.Messages.LDAP_REPOSITORY_ERROR;
import static br.eti.arthurgregorio.shiroee.config.messages.Messages.NO_REALM_ERROR;
import br.eti.arthurgregorio.shiroee.realm.LdapSecurityRealm;
import java.util.Collection;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import org.apache.commons.configuration2.PropertiesConfiguration;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.ldap.JndiLdapContextFactory;
import org.apache.shiro.web.mgt.CookieRememberMeManager;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
@ApplicationScoped
public class DefaultSecurityConfiguration implements SecurityConfiguration {

    private final PropertiesConfiguration configuration;

    private final LdapUserProvider ldapUserProvider;
    private final JndiLdapContextFactory ldapContextFactory;

    @Inject
    private Instance<RealmConfiguration> realmConfigurationInstance;
    @Inject
    private Instance<HttpSecurityConfiguration> httpSecurityConfigurationInstance;

    /**
     *
     */
    public DefaultSecurityConfiguration() {
        this.configuration = ConfigurationFactory.get();

        this.ldapContextFactory = this.configureLdapContextFactory();
        this.ldapUserProvider = this.configureLdapUserProvider();
    }

    /**
     *
     * @return
     */
    @Override
    public FilterChainResolver configurteFilterChainResolver() {

        // validate the base configuration
        this.validateHttpConfig();

        final FilterChainManager manager = new DefaultFilterChainManager();

        manager.addFilter(this.configuration.getString(
                "operator.authenticated", AUTHENTICATED_OP),
                this.configureFormAuthentication());

        manager.addFilter(this.configuration.getString(
                "operator.logout", LOGOUT_OP),
                new LogoutFilter());
        manager.addFilter(this.configuration.getString(
                "operator.anonymous", ANONYMOUS_OP),
                new AnonymousFilter());
        manager.addFilter(this.configuration.getString(
                "operator.required_role", REQUIRED_ROLE_OP),
                new RolesAuthorizationFilter());

        final PermissionsAuthorizationFilter permsFilter
                = new PermissionsAuthorizationFilter();

        permsFilter.setUnauthorizedUrl(this.configuration
                .getString("url.unauthorized", URL_UNAUTHORIZED));

        manager.addFilter(this.configuration.getString(
                "operator.required_permission", REQUIRED_PERMISSION_OP),
                permsFilter);

        // build the http security rules for each path
        final Map<String, String> chains = this.httpSecurityConfigurationInstance
                .get()
                .configureHttpSecurity()
                .build();

        chains.keySet().stream().forEach(path -> {
            manager.createChain(path, chains.get(path));
        });

        manager.createChain(this.configuration.getString(
                "url.root_secured_path", URL_ROOT_SECURED_PATH),
                this.configuration.getString(
                        "operator.authenticated", AUTHENTICATED_OP));
        manager.createChain(this.configuration.getString(
                "url.logout_path", URL_LOGOUT_PATH),
                this.configuration.getString(
                        "operator.logout", LOGOUT_OP));

        final PathMatchingFilterChainResolver resolver
                = new PathMatchingFilterChainResolver();

        resolver.setFilterChainManager(manager);

        return resolver;
    }

    /**
     *
     * @return
     */
    @Override
    public DefaultWebSecurityManager configureSecurityManager() {

        // validate the base configuration
        this.validateRealmConfig();

        final DefaultWebSecurityManager securityManager
                = new DefaultWebSecurityManager();

        // enable the remember me function based on cookies 
        final CookieRememberMeManager rememberMeManager
                = new CookieRememberMeManager();

        rememberMeManager.setCipherKey(this.createCypherKey());

        // get the list of realms to use with this configuration
        final Collection<Realm> realms = this.realmConfigurationInstance
                .get()
                .configureRealms();

        if (realms == null || realms.isEmpty()) {
            throw new ConfigurationException(NO_REALM_ERROR.format());
        }

        // create the security manager
        securityManager.setRememberMeManager(rememberMeManager);

        // if ldap realm is set, set the context factory for this instance
        realms.stream()
                .filter(LdapSecurityRealm.class::isInstance)
                .map(LdapSecurityRealm.class::cast)
                .forEach(realm -> {
                    realm.setContextFactory(this.ldapContextFactory);
                });
        
        

        securityManager.setRealms(realms);

        return securityManager;
    }

    /**
     *
     * @return
     */
    @Produces
    @ApplicationScoped
    public LdapUserProvider produceLdapUserProvider() {
        if (this.ldapUserProvider == null) {
            this.configureLdapUserProvider();
        }
        return this.ldapUserProvider;
    }

    /**
     *
     */
    private LdapUserProvider configureLdapUserProvider() {

        final String baseDn = this.configuration.getString(
                "ldap.baseDn", Constants.LDAP_BASE_DN);
        final String searchFilter = this.configuration.getString(
                "ldap.searchFilter", Constants.LDAP_SEARCH_FILTER);

        return new DefaultLdapUserProvider(baseDn, searchFilter, 
                this.ldapContextFactory);
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

    /**
     * @return the default form authentication mechanism for this realm
     */
    private FormAuthenticationFilter configureFormAuthentication() {

        final FormAuthenticationFilter formAuthenticator
                = new FormAuthenticationFilter();

        formAuthenticator.setLoginUrl(this.configuration.getString(
                "url.login", URL_LOGIN));
        formAuthenticator.setSuccessUrl(this.configuration.getString(
                "url.login_success", URL_LOGIN_SUCCESS));

        return formAuthenticator;
    }

    /**
     * @return a custom cypher key for the cookie
     */
    private byte[] createCypherKey() {
        return String.format("0x%s", Hex.encodeToString(new AesCipherService()
                .generateNewKey().getEncoded())).getBytes();
    }

    /**
     *
     */
    private void validateHttpConfig() {
        if (this.httpSecurityConfigurationInstance.isUnsatisfied()
                || this.httpSecurityConfigurationInstance.isAmbiguous()) {
            throw new ConfigurationException(INSTANCE_IS_INVALID.format(
                    "HttpSecurityConfigurationInstance"));
        }
    }

    /**
     *
     */
    private void validateRealmConfig() {
        if (this.realmConfigurationInstance.isUnsatisfied()
                || this.realmConfigurationInstance.isAmbiguous()) {
            throw new ConfigurationException(INSTANCE_IS_INVALID.format(
                    "RealmConfiguration"));
        }
    }
}
