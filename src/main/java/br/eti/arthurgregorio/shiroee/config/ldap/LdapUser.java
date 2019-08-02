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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

/**
 * LDAP/AD user representation
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 05/01/2018
 */
@ToString
@EqualsAndHashCode
public final class LdapUser {

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String mail;
    @Getter
    @Setter
    private String sAMAccountName;
    @Getter
    @Setter
    private String distinguishedName;
    @Getter
    @Setter
    private String userAccountControl;

    /**
     * Convert a set of {@link Attributes} to a single instance of this class
     *
     * @param attributes to be used
     * @return the {@link LdapUser} created from the {@link Attributes}
     * @throws NamingException if any error occur at the mapping process
     */
    public static LdapUser of(Attributes attributes) throws NamingException {

        final LdapUser ldapUser = new LdapUser();

        ldapUser.setName(String.valueOf(attributes.get("name").get()));
        ldapUser.setMail(String.valueOf(attributes.get("mail").get()));
        ldapUser.setSAMAccountName(String.valueOf(attributes.get("sAMAccountName").get()));
        ldapUser.setDistinguishedName(String.valueOf(attributes.get("distinguishedName").get()));
        ldapUser.setUserAccountControl(String.valueOf(attributes.get("userAccountControl").get()));

        return ldapUser;
    }
}
