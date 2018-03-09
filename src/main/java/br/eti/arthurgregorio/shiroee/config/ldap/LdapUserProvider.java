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

import java.util.Optional;
import org.apache.shiro.realm.ldap.LdapContextFactory;

/**
 * The interface for creating a LDAP/AD user provider
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
     * @param principal the usernem of the principal
     * @return a {@link Optional} of {@link LdapUser} to provide the details 
     * about the user that you want to authenticate
     */
    Optional<LdapUser> search(String principal);
    
    /**
     * The Shiro {@link LdapContextFactory} to use for creating connections to 
     * the LDAP/AD repository in this provider
     * 
     * @param ldapContextFactory the factory, must not be null
     */
    void setLdapContextFactory(LdapContextFactory ldapContextFactory);
}
