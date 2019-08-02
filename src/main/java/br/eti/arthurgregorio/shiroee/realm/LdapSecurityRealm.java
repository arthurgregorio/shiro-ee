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
package br.eti.arthurgregorio.shiroee.realm;

import br.eti.arthurgregorio.shiroee.auth.AuthenticationMechanism;
import br.eti.arthurgregorio.shiroee.auth.EmptyAuthenticationMechanism;
import br.eti.arthurgregorio.shiroee.auth.EmptyAuthenticationMechanism.EmptyUser;
import br.eti.arthurgregorio.shiroee.config.jdbc.UserDetails;
import br.eti.arthurgregorio.shiroee.config.ldap.LdapUserProvider;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.ldap.DefaultLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.subject.PrincipalCollection;

import javax.naming.NamingException;
import java.util.Set;

import static br.eti.arthurgregorio.shiroee.config.messages.Messages.AUTHENTICATION_ERROR;

/**
 * Base implementation for LDAP/AD authentication
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 28/12/2017
 */
public class LdapSecurityRealm extends DefaultLdapRealm {

    private final LdapUserProvider ldapUserProvider;
    private final AuthenticationMechanism<? extends UserDetails> mechanism;

    /**
     * Constructor...
     *
     * @param ldapUserProvider used to bind users on LDAP/AD
     */
    public LdapSecurityRealm(LdapUserProvider ldapUserProvider) {
        this.ldapUserProvider = ldapUserProvider;
        this.mechanism = new EmptyAuthenticationMechanism();
    }

    /**
     * Constructor...
     *
     * @param ldapUserProvider used to bind users on LDAP/AD
     * @param mechanism used to verify the accounts before send them to LDAP/AD validation if no mechanism is provided,
     *                  an {@link EmptyAuthenticationMechanism}
     */
    public LdapSecurityRealm(LdapUserProvider ldapUserProvider, AuthenticationMechanism<? extends UserDetails> mechanism) {
        this.mechanism = mechanism;
        this.ldapUserProvider = ldapUserProvider;
    }

    /**
     * {@inheritDoc}
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

        final UserDetails userDetails = this.mechanism.getAccount(username);

        if (userDetails.isLdapBindAccount() && !userDetails.isBlocked()) {
            return super.queryForAuthenticationInfo(token, factory);
        }

        throw new IncorrectCredentialsException(AUTHENTICATION_ERROR.format(username));
    }

    /**
     * {@inheritDoc}
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

        final Set<String> permissions = this.mechanism.getPermissions(username);

        final SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        authorizationInfo.setStringPermissions(permissions);

        return authorizationInfo;
    }

    /**
     * {@inheritDoc}
     *
     * @param principal
     * @return
     */
    @Override
    protected String getUserDn(String principal) {
        return this.ldapUserProvider.search(principal)
                .orElseThrow(() -> new UnknownAccountException(AUTHENTICATION_ERROR.format(principal)))
                .getDistinguishedName();
    }
}
