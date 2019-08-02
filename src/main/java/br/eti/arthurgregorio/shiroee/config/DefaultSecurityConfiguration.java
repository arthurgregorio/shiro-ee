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

import br.eti.arthurgregorio.shiroee.auth.events.AuthenticationEventSupport;
import br.eti.arthurgregorio.shiroee.config.ldap.DefaultLdapUserProvider;
import br.eti.arthurgregorio.shiroee.config.ldap.LdapUserProvider;
import br.eti.arthurgregorio.shiroee.realm.LdapSecurityRealm;
import com.google.common.collect.Lists;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.ldap.JndiLdapContextFactory;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

import static br.eti.arthurgregorio.shiroee.config.Constants.*;
import static br.eti.arthurgregorio.shiroee.config.messages.Messages.INSTANCE_IS_INVALID;
import static br.eti.arthurgregorio.shiroee.config.messages.Messages.NO_REALM_ERROR;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The main class of this library. All configurations should be happening here.
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
@ApplicationScoped
public class DefaultSecurityConfiguration implements SecurityConfiguration {

    private PropertiesConfiguration configuration;

    private LdapUserProvider ldapUserProvider;
    private LdapContextFactory ldapContextFactory;

    @Inject
    private AuthenticationEventSupport authenticationEventSupport;

    @Inject
    private Instance<RealmConfiguration> realmConfigurationInstance;
    @Inject
    private Instance<HttpSecurityConfiguration> httpSecurityConfigurationInstance;

    /**
     * Constructor...
     */
    public DefaultSecurityConfiguration() {
        this.configuration = ConfigurationFactory.get();

        // check if the LDAP/AD is enabled
        final boolean ldapEnabled = this.configuration.getBoolean("ldap.enabled", false);

        if (ldapEnabled) {
            this.ldapContextFactory = this.configureLdapContextFactory();
            this.ldapUserProvider = this.configureLdapUserProvider();
        }
    }

    /**
     * {@inheritDoc }
     *
     * @return
     */
    @Override
    public FilterChainResolver configureFilterChainResolver() {

        final FilterChainManager manager = new DefaultFilterChainManager();

        manager.addFilter(this.configuration.getString("operator.authenticated", AUTHENTICATED_OP), this.configureFormAuthentication());
        manager.addFilter(this.configuration.getString("operator.logout", LOGOUT_OP), new LogoutFilter());
        manager.addFilter(this.configuration.getString("operator.anonymous", ANONYMOUS_OP), new AnonymousFilter());
        manager.addFilter(this.configuration.getString("operator.required_role", REQUIRED_ROLE_OP), new RolesAuthorizationFilter());

        final PermissionsAuthorizationFilter permsFilter = new PermissionsAuthorizationFilter();

        // configure the default url for http 401 redirect
        final String Url401 = this.configuration.getString("url.unauthorized");

        if (StringUtils.isNotBlank(Url401)) {
            permsFilter.setUnauthorizedUrl(Url401);
        }

        manager.addFilter(this.configuration.getString("operator.required_permission", REQUIRED_PERMISSION_OP),
                permsFilter);

        // validate the base configuration
        final HttpSecurityConfiguration httpSecurityConfiguration = this.validateHttpConfig();

        // build the http security rules for each path
        final Map<String, String> chains = httpSecurityConfiguration
                .configureHttpSecurity()
                .build();

        chains.keySet().forEach(path -> {
            manager.createChain(path, chains.get(path));
        });

        this.destroy(httpSecurityConfiguration);

        manager.createChain(this.configuration.getString("url.root_secured_path", URL_ROOT_SECURED_PATH),
                this.configuration.getString("operator.authenticated", AUTHENTICATED_OP));
        manager.createChain(this.configuration.getString("url.logout_path", URL_LOGOUT_PATH),
                this.configuration.getString("operator.logout", LOGOUT_OP));

        final PathMatchingFilterChainResolver resolver = new PathMatchingFilterChainResolver();

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

        final DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        // enable the remember me function based on cookies 
        final CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();

        rememberMeManager.setCipherKey(this.createCypherKey());

        final RealmConfiguration realmConfiguration = this.validateRealmConfig();

        // get the list of realms to use with this configuration
        final Collection<Realm> realms = realmConfiguration.configureRealms();

        if (realms == null || realms.isEmpty()) {
            throw new ConfigurationException(NO_REALM_ERROR.format());
        }

        this.destroy(realmConfiguration);

        // create the security manager
        securityManager.setRememberMeManager(rememberMeManager);

        // add the modular authenticator with support for CDI events 
        final ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();

        authenticator.setAuthenticationListeners(Lists.newArrayList(this.authenticationEventSupport));

        securityManager.setAuthenticator(authenticator);

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
     * Produce the {@link LdapUserProvider} for external classes outside of the library.
     *
     * This is needed in case of you want to find the data from a LDAP/AD account to create a local bind process
     *
     * @return a configured provider
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
     * @return {@link LdapUserProvider} configured
     */
    private LdapUserProvider configureLdapUserProvider() {

        final String baseDn = this.configuration.getString("ldap.baseDn", Constants.LDAP_BASE_DN);
        final String searchFilter = this.configuration.getString("ldap.searchFilter", Constants.LDAP_SEARCH_FILTER);

        return new DefaultLdapUserProvider(baseDn, searchFilter, this.ldapContextFactory);
    }

    /**
     * Configure the {@link LdapContextFactory} to enable LDAP/AD connectivity
     *
     * @return {@link JndiLdapContextFactory} configured for use
     */
    private LdapContextFactory configureLdapContextFactory() {

        final JndiLdapContextFactory factory = new JndiLdapContextFactory();

        final String ldapUrl = checkNotNull(this.configuration.getString("ldap.url"));
        final String ldapUser = checkNotNull(this.configuration.getString("ldap.user"));
        final String ldapPassword = checkNotNull(this.configuration.getString("ldap.password"));

        factory.setUrl(ldapUrl);
        factory.setSystemUsername(ldapUser);
        factory.setSystemPassword(ldapPassword);

        factory.setPoolingEnabled(true);

        return factory;
    }

    /**
     * Configure the {@link FormAuthenticationFilter} for form based  authentication
     *
     * @return the {@link FormAuthenticationFilter} configured
     */
    private FormAuthenticationFilter configureFormAuthentication() {

        final FormAuthenticationFilter formAuthenticator = new FormAuthenticationFilter();

        formAuthenticator.setLoginUrl(this.configuration.getString("url.login", URL_LOGIN));
        formAuthenticator.setSuccessUrl(this.configuration.getString("url.login_success", URL_LOGIN_SUCCESS));

        return formAuthenticator;
    }

    /**
     * Build a single key for cookies storage when "remember-me" is enable
     *
     * @return cypher key for the cookie storage
     */
    private byte[] createCypherKey() {
        return String.format("0x%s", Hex.encodeToString(new AesCipherService().generateNewKey().getEncoded())).getBytes();
    }

    /**
     * Validate the {@link HttpSecurityConfiguration}
     */
    private HttpSecurityConfiguration validateHttpConfig() {
        if (!this.httpSecurityConfigurationInstance.isUnsatisfied() && !this.httpSecurityConfigurationInstance.isAmbiguous()) {
            return this.httpSecurityConfigurationInstance.get();
        }
        throw new ConfigurationException(INSTANCE_IS_INVALID.format("HttpSecurityConfigurationInstance"));
    }

    /**
     * Validate the {@link RealmConfiguration}
     */
    private RealmConfiguration validateRealmConfig() {
        if (!this.realmConfigurationInstance.isUnsatisfied() && !this.realmConfigurationInstance.isAmbiguous()) {
            return this.realmConfigurationInstance.get();
        }
        throw new ConfigurationException(INSTANCE_IS_INVALID.format("RealmConfiguration"));
    }

    /**
     * Destroy the instance of {@link RealmConfiguration} to avoid memory leaks
     *
     * @param instance to be destroyed
     */
    private void destroy(RealmConfiguration instance) {
        this.realmConfigurationInstance.destroy(instance);
    }

    /**
     * Destroy the instance of {@link HttpSecurityConfiguration} to avoid memory leaks
     *
     * @param instance to be destroyed
     */
    private void destroy(HttpSecurityConfiguration instance) {
        this.httpSecurityConfigurationInstance.destroy(instance);
    }
}
