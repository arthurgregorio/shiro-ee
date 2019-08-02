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
import br.eti.arthurgregorio.shiroee.auth.DatabaseAuthenticationMechanism;
import br.eti.arthurgregorio.shiroee.auth.PasswordEncoder;
import br.eti.arthurgregorio.shiroee.config.jdbc.UserDetails;
import br.eti.arthurgregorio.shiroee.config.jdbc.UserDetailsProvider;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import static br.eti.arthurgregorio.shiroee.config.messages.Messages.AUTHENTICATION_ERROR;

/**
 * A local realm used to authenticate through a database connection.
 *
 * This method requires a {@link DatabaseAuthenticationMechanism} and the {@link UserDetailsProvider} implementation
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
public class JdbcSecurityRealm extends AuthorizingRealm {

    private final AuthenticationMechanism<? extends UserDetails> mechanism;

    /**
     * Constructor...
     *
     * @param mechanism to be used
     */
    public JdbcSecurityRealm(AuthenticationMechanism<? extends UserDetails> mechanism) {
        this.mechanism = mechanism;

        // instantiate the custom password matcher based on bcrypt
        final PasswordMatcher passwordMatcher = new PasswordMatcher();

        passwordMatcher.setPasswordService(new PasswordEncoder());

        super.setCredentialsMatcher(passwordMatcher);
    }

    /**
     * {@inheritDoc}
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {

        final UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;

        final UserDetails userDetails = this.mechanism.getAccount(token.getUsername());

        if (!userDetails.isLdapBindAccount() && !userDetails.isBlocked()) {
            return new SimpleAuthenticationInfo(userDetails.getUsername(),
                    userDetails.getPassword(), this.getName());
        }

        throw new IncorrectCredentialsException(AUTHENTICATION_ERROR.format(token.getUsername()));
    }

    /**
     * {@inheritDoc}
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        final String username = (String) this.getAvailablePrincipal(principalCollection);

        final SimpleAuthorizationInfo authzInfo = new SimpleAuthorizationInfo();

        authzInfo.setStringPermissions(this.mechanism.getPermissions(username));

        return authzInfo;
    }
}
