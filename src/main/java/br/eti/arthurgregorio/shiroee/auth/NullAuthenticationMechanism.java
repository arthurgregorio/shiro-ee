package br.eti.arthurgregorio.shiroee.auth;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 06/03/2018
 */
public final class NullAuthenticationMechanism implements AuthenticationMechanism {

    /**
     * 
     * @param username
     * @return 
     */
    @Override
    public boolean isAuthorized(String username) {
        return true;
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
}
