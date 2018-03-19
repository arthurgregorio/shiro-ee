/*
 * Copyright 2018 Arthur Gregorio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import static br.eti.arthurgregorio.shiroee.config.messages.Messages.NO_REALM_ERROR;
import br.eti.arthurgregorio.shiroee.realm.LdapSecurityRealm;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import org.apache.commons.configuration2.PropertiesConfiguration;
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
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.WebSecurityManager;

/**
 * The main class of this library.
 * 
 * Its in this class that all configurations are consolidated and Shiro is 
 * configured to work with you http rules and the realms of your choice.
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
    private final LdapContextFactory ldapContextFactory;

    @Inject
    private Instance<RealmConfiguration> realmConfigurationInstance;
    @Inject
    private Instance<HttpSecurityConfiguration> httpSecurityConfigurationInstance;

    /**
     * The constructor, retrieve the configuration and build the basic 
     * configuration for the {@link WebSecurityManager} and the 
     * {@link FilterChainResolver}
     */
    public DefaultSecurityConfiguration() {
        this.configuration = ConfigurationFactory.get();

        this.ldapContextFactory = this.configureLdapContextFactory();
        this.ldapUserProvider = this.configureLdapUserProvider();
    }

    /**
     * {@inheritDoc }
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
     * {@inheritDoc }
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
     * Produce the {@link LdapUserProvider} for external classes outside of the
     * library.
     * 
     * This is needed in case of you need to find the data from a LDAP/AD account
     * to create a simple bind account on the database
     *
     * @return the provider configured
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
     * Configure the {@link LdapUserProvider}
     * 
     * @return the {@link LdapUserProvider} configured
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
     * Configure the {@link LdapContextFactory} for the classes that want to 
     * connect to the LDAP/AD repository
     *
     * @return the {@link JndiLdapContextFactory} configured
     */
    private LdapContextFactory configureLdapContextFactory() {

        final JndiLdapContextFactory factory = new JndiLdapContextFactory();

        final String ldapUrl = this.configuration.getString("ldap.url");
        final String ldapUser = this.configuration.getString("ldap.user");
        final String ldapPassword = this.configuration.getString("ldap.password");

        factory.setUrl(ldapUrl);
        factory.setSystemUsername(ldapUser);
        factory.setSystemPassword(ldapPassword);

        factory.setPoolingEnabled(true);

        return factory;
    }

    /**
     * Configure the {@link FormAuthenticationFilter} for form based 
     * authentication
     *
     * @return the {@link FormAuthenticationFilter} configured
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
     * Build a single key for cookies storage when remeber me is enable
     * 
     * @return cypher key for the cookie storage
     */
    private byte[] createCypherKey() {
        return String.format("0x%s", Hex.encodeToString(new AesCipherService()
                .generateNewKey().getEncoded())).getBytes();
    }

    /**
     * Validate the http configuration
     */
    private HttpSecurityConfiguration validateHttpConfig() {
        if (!this.httpSecurityConfigurationInstance.isUnsatisfied()
                && !this.httpSecurityConfigurationInstance.isAmbiguous()) {
            return this.httpSecurityConfigurationInstance.get();
        }
        throw new ConfigurationException(
                INSTANCE_IS_INVALID.format("HttpSecurityConfigurationInstance"));
    }

    /**
     * Validate the real configuration
     */
    private RealmConfiguration validateRealmConfig() {
        if (!this.realmConfigurationInstance.isUnsatisfied()
                && !this.realmConfigurationInstance.isAmbiguous()) {
            return this.realmConfigurationInstance.get();
        }
        throw new ConfigurationException(
                INSTANCE_IS_INVALID.format("RealmConfiguration"));
    }
    
    /**
     * Destroy the instance of {@link RealmConfiguration} to avoid memory leaks
     * 
     * @param instance the instance to destroy the injected instance
     */
    private void destroy(RealmConfiguration instance) {
        this.realmConfigurationInstance.destroy(instance);
    }
    
    /**
     * Destroy the instance of {@link HttpSecurityConfiguration} to avoid memory
     * leaks
     * 
     * @param instance the instance to destroy the injected instance
     */
    private void destroy(HttpSecurityConfiguration instance) {
        this.httpSecurityConfigurationInstance.destroy(instance);
    }
}
