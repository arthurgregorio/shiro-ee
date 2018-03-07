package br.eti.arthurgregorio.shiroee.config.jdbc;

import java.util.Optional;

/**
 * 
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
public interface UserDetailsProvider {

    /**
     * 
     * @param username
     * @return 
     */
    Optional<UserDetails> findUserDetailsByUsername(String username);
}
