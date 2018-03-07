package br.eti.arthurgregorio.shiroee.auth;

import java.util.Set;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 06/03/2018
 */
public interface AuthenticationMechanism {

    /**
     * 
     * @param username
     * @return 
     */
    boolean isAuthorized(String username);
    
    /**
     * 
     * @param username
     * @return 
     */
    Set<String> getPermissionsFor(String username);
}
