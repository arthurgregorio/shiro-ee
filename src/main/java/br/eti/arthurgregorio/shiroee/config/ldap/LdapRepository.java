package br.eti.arthurgregorio.shiroee.config.ldap;

import static br.eti.arthurgregorio.shiroee.config.messages.Messages.CONFIGURATION_ERROR;
import java.util.Optional;
import javax.naming.ConfigurationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 11/01/2018
 */
public class LdapRepository {
    
    private final String baseDN;
    private final String searchFilter;

    private final LdapContext context;
    
    /**
     * 
     * @param baseDN
     * @param searchFilter
     * @param context 
     */
    public LdapRepository(String baseDN, String searchFilter, LdapContext context) {
        this.baseDN = baseDN;
        this.context = context;
        this.searchFilter = searchFilter;
    }
    
    /**
     * 
     * @param principal
     * @return
     * @throws NamingException 
     */
    public Optional<LdapUser> search(String principal) throws NamingException {

        if (!this.isValid()) {
            throw new ConfigurationException(
                    CONFIGURATION_ERROR.format(this.getClass().getName()));
        }
        
        final SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        final NamingEnumeration answer = this.context.search(this.baseDN,
                this.searchFilter, new Object[]{principal}, searchControls);

        while (answer.hasMoreElements()) {

            final SearchResult result = (SearchResult) answer.next();
            final Attributes attributes = result.getAttributes();

            return Optional.of(LdapUser.of(attributes));
        }
        return Optional.empty();
    }

    /**
     * 
     * @return 
     */
    private boolean isValid() {
        return this.context != null && isNotBlank(this.baseDN) 
                && isNotBlank(this.searchFilter);
    }
}
