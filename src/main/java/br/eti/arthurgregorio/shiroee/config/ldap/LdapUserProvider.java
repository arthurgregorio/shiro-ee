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
package br.eti.arthurgregorio.shiroee.config.ldap;

import org.apache.shiro.realm.ldap.LdapContextFactory;

import java.util.Optional;

/**
 * Simple implementation of the {@link LdapUser} provider
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 07/03/2018
 */
public interface LdapUserProvider {

    /**
     * The method to call when you need to bind a user in the LDAP/AD repository
     *
     * @param principal represents the username of the desired user
     * @return an {@link Optional} of the {@link LdapUser}
     */
    Optional<LdapUser> search(String principal);

    /**
     * The {@link LdapContextFactory} to use for connection at the LDAP/AD repository
     *
     * @param ldapContextFactory to create connections
     */
    void setLdapContextFactory(LdapContextFactory ldapContextFactory);
}
