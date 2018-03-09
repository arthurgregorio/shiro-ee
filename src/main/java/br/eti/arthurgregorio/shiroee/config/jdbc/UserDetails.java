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
package br.eti.arthurgregorio.shiroee.config.jdbc;

import java.util.Set;

/**
 * Definition for what details we need from you user account model
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
public interface UserDetails {

    /**
     * @return the username
     */
    Object getUsername();

    /**
     * @return the password
     */
    Object getPassword();

    /**
     * @return if this account is blocked or not
     */
    boolean isBlocked();

    /**
     * @return if this account is only a local bind account for a LDAP/AD
     * authentication process
     */
    boolean isLdapBindAccount();

    /**
     * @return the list of permissions for this account
     */
    Set<String> getPermissions();

    /**
     * @return if this account is not blocked or is blocked
     */
    default boolean isNotBlocked() {
        return !this.isBlocked();
    }
}
