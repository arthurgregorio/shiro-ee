package br.eti.arthurgregorio.shiroee.auth;

import br.eti.arthurgregorio.shiroee.config.jdbc.UserDetails;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 06/03/2018
 */
public final class EmptyAuthenticationMechanism implements AuthenticationMechanism {

    /**
     * 
     * @param username
     * @return 
     */
    @Override
    public UserDetails getUserDetails(String username) {
        return new EmptyUser();
    }

    /**
     * 
     * @param username
     * @return 
     */
    @Override
    public Set<String> getPermissionsFor(String username) {
        return Collections.emptySet();
    }
    
    /**
     * 
     */
    public class EmptyUser implements UserDetails {

        /**
         * 
         * @return 
         */
        @Override
        public Object getUsername() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         * 
         * @return 
         */
        @Override
        public Object getPassword() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         * 
         * @return 
         */
        @Override
        public boolean isBlocked() {
            return false;
        }

        /**
         * 
         * @return 
         */
        @Override
        public boolean isLdapBindAccount() {
            return true;
        }

        /**
         * 
         * @return 
         */
        @Override
        public Set<String> getPermissions() {
            return Collections.emptySet();
        }
        
    }
}
