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

import static br.eti.arthurgregorio.shiroee.config.messages.Messages.BIND_ERROR;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Optional;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.realm.ldap.LdapContextFactory;

/**
 * The default implementation for {@link LdapUserProvider}.
 * 
 * With this class you can search for a {@link LdapUser} on the LDAP/AD 
 * directory
 * 
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 11/01/2018
 */
public class DefaultLdapUserProvider implements LdapUserProvider {

    private final String baseDN;
    private final String searchFilter;

    private LdapContextFactory ldapContextFactory;

    /**
     * Constructor...
     * 
     * @param baseDN the base DN account search
     * @param searchFilter the search fielter to be used
     */
    public DefaultLdapUserProvider(String baseDN, String searchFilter) {
        this.baseDN = checkNotNull(baseDN);
        this.searchFilter = checkNotNull(searchFilter);
    }
    
    /**
     * Constructor...
     * 
     * @param baseDN the base DN account search
     * @param searchFilter the search fielter to be used
     * @param ldapContextFactory Shiro {@link LdapContextFactory} to be used to 
     * get LDAP/AD connections
     */
    public DefaultLdapUserProvider(String baseDN, String searchFilter, LdapContextFactory ldapContextFactory) {
        this(baseDN, searchFilter);
        this.ldapContextFactory = checkNotNull(ldapContextFactory);
    }

    /**
     * {@inheritDoc }
     *
     * @param principal
     * @return
     */
    @Override
    public Optional<LdapUser> search(String principal) {
        try {
            final SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            
            final LdapContext context = 
                    this.ldapContextFactory.getSystemLdapContext();

            final NamingEnumeration answer = context.search(this.baseDN,
                    this.searchFilter, new Object[]{principal}, searchControls);

            while (answer.hasMoreElements()) {

                final SearchResult result = (SearchResult) answer.next();
                final Attributes attributes = result.getAttributes();

                return Optional.of(LdapUser.of(attributes));
            }
        } catch (NamingException ex) {
            throw new IncorrectCredentialsException(BIND_ERROR.format(principal));
        }
        
        return Optional.empty();
    }

    /**
     * {@inheritDoc }
     * 
     * @param ldapContextFactory 
     */
    @Override
    public void setLdapContextFactory(LdapContextFactory ldapContextFactory) {
        this.ldapContextFactory = ldapContextFactory;
    }
}
