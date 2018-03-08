package br.eti.arthurgregorio.shiroee.config.ldap;

import static br.eti.arthurgregorio.shiroee.config.messages.Messages.BIND_ERROR;
import static br.eti.arthurgregorio.shiroee.config.messages.Messages.CONFIGURATION_ERROR;
import java.util.Optional;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.realm.ldap.LdapContextFactory;

/**
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
     * 
     * @param baseDN
     * @param searchFilter
     * @param ldapContextFactory 
     */
    public DefaultLdapUserProvider(String baseDN, String searchFilter, LdapContextFactory ldapContextFactory) {
        this.baseDN = baseDN;
        this.searchFilter = searchFilter;
        this.ldapContextFactory = ldapContextFactory;
    }

    /**
     *
     * @param principal
     * @return
     */
    @Override
    public Optional<LdapUser> search(String principal) {

        if (!this.isValid()) {
            throw new ConfigurationException(
                    CONFIGURATION_ERROR.format(this.getClass().getName()));
        }

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
            throw new AuthenticationException(BIND_ERROR.format(principal));
        }
        
        return Optional.empty();
    }

    /**
     * 
     * @param ldapContextFactory 
     */
    @Override
    public void setLdapContextFactory(LdapContextFactory ldapContextFactory) {
        this.ldapContextFactory = ldapContextFactory;
    }
    
    /**
     *
     * @return
     */
    private boolean isValid() {
        return this.ldapContextFactory != null && isNotBlank(this.baseDN)
                && isNotBlank(this.searchFilter);
    }
}
