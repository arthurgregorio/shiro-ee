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

import java.util.Collections;
import java.util.Set;

/**
 * A empty authentication mechanism used when you don't want to bind the LDAP/AD account to a local user
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 06/03/2018
 */
public final class EmptyAuthenticationMechanism implements AuthenticationMechanism<EmptyAuthenticationMechanism.EmptyUser> {

    /**
     * {@inheritDoc }
     *
     * @param username
     * @return
     */
    @Override
    public EmptyUser getAccount(String username) {
        return new EmptyUser();
    }

    /**
     * {@inheritDoc }
     *
     * @param username
     * @return
     */
    @Override
    public Set<String> getPermissions(String username) {
        return Collections.emptySet();
    }

    /**
     * Empty user detail implementation
     */
    public static class EmptyUser implements UserDetails {

        /**
         * {@inheritDoc }
         *
         * @return
         */
        @Override
        public Object getUsername() {
            throw new UnsupportedOperationException("Empty user details didn't have username");
        }

        /**
         * {@inheritDoc }
         *
         * @return
         */
        @Override
        public Object getPassword() {
            throw new UnsupportedOperationException("Empty user details didn't have password");
        }

        /**
         * {@inheritDoc }
         *
         * @return
         */
        @Override
        public boolean isBlocked() {
            return false;
        }

        /**
         * {@inheritDoc }
         *
         * @return
         */
        @Override
        public boolean isLdapBindAccount() {
            return true;
        }

        /**
         * {@inheritDoc }
         *
         * @return
         */
        @Override
        public Set<String> getPermissions() {
            return Collections.emptySet();
        }
    }
}
