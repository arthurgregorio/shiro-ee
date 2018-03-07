package br.eti.arthurgregorio.shiroee.config.jdbc;

import java.util.Set;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
public interface UserDetails {

    /**
     * 
     * @return 
     */
    Object getUsername();
    
    /**
     * 
     * @return 
     */
    Object getPassword();
    
    /**
     * 
     * @return 
     */
    boolean isBlocked();
    
    /**
     * 
     * @return 
     */
    default boolean isNotBlocked() {
        return !this.isBlocked();
    }
    
    /**
     * 
     * @return 
     */
    Set<String> getPermissions();
}
