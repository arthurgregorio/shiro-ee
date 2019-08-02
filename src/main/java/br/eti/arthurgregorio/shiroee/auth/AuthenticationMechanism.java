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

import java.util.Set;

import br.eti.arthurgregorio.shiroee.config.jdbc.UserDetails;
import org.apache.shiro.realm.Realm;

/**
 * The definition for creating authentication mechanisms to authenticate users
 * on the realms of Shiro
 *
 * @param <T> the parameter to determine wich type of detais we want to return
 * 
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 06/03/2018
 */
public interface AuthenticationMechanism<T extends UserDetails> {

    /**
     * Provide the necessary data for the account to the {@link Realm} that you 
     * want to use for authentication
     * 
     * @param username the username of the account to search for 
     * @return the data abount this account
     */
    T getAccount(String username);
    
    /**
     * Provide a list of permissions for the given username 
     * 
     * @param username the username to search for permissions
     * @return the permissions list
     */
    Set<String> getPermissions(String username);
}
