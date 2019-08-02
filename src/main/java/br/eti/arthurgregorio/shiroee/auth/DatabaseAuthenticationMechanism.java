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
package br.eti.arthurgregorio.shiroee.auth;

import br.eti.arthurgregorio.shiroee.config.jdbc.UserDetails;
import br.eti.arthurgregorio.shiroee.config.jdbc.UserDetailsProvider;
import br.eti.arthurgregorio.shiroee.config.messages.Messages;
import org.apache.shiro.authc.AuthenticationException;

import java.util.Set;

import static br.eti.arthurgregorio.shiroee.config.messages.Messages.ACCOUNT_NOT_FOUND;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Basic implementation for authenticate users with local accounts on the database.
 *
 * This implementation uses the {@link UserDetailsProvider} to find and provide details about the account that you are
 * trying to authenticate.
 *
 * This approach is similar to the one used by Spring Security.
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 06/03/2018
 */
public class DatabaseAuthenticationMechanism implements AuthenticationMechanism<UserDetails> {

    private final UserDetailsProvider userDetailsProvider;

    /**
     * Constructor, initialize the mechanism of authentication with the {@link UserDetailsProvider} implementation
     *
     * @param userDetailsProvider the implementation of the user details provider
     */
    public DatabaseAuthenticationMechanism(UserDetailsProvider userDetailsProvider) {
        this.userDetailsProvider = checkNotNull(userDetailsProvider);
    }

    /**
     * {@inheritDoc }
     *
     * @param username
     * @return
     */
    @Override
    public UserDetails getAccount(String username) {
        return this.userDetailsProvider.findUserDetailsByUsername(username)
                .orElseThrow(() -> new AuthenticationException(
                        ACCOUNT_NOT_FOUND.format(username)));
    }

    /**
     * {@inheritDoc }
     *
     * @param username
     * @return
     */
    @Override
    public Set<String> getPermissions(String username) {

        final UserDetails userDetails = this.userDetailsProvider
                .findUserDetailsByUsername(username)
                .orElseThrow(() -> new AuthenticationException(
                        Messages.ACCOUNT_NOT_FOUND.format(username)));

        return userDetails.getPermissions();
    }
}