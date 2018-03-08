package br.eti.arthurgregorio.shiroee.auth;

import br.eti.arthurgregorio.shiroee.config.jdbc.UserDetails;
import br.eti.arthurgregorio.shiroee.config.jdbc.UserDetailsProvider;
import br.eti.arthurgregorio.shiroee.config.messages.Messages;
import static br.eti.arthurgregorio.shiroee.config.messages.Messages.ACCOUNT_NOT_FOUND;
import java.util.Set;
import org.apache.shiro.authc.AuthenticationException;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 06/03/2018
 */
public class DatabaseAuthenticationMechanism implements AuthenticationMechanism {

    private final UserDetailsProvider userDetailsProvider;

    /**
     *
     * @param userDetailsProvider
     */
    public DatabaseAuthenticationMechanism(UserDetailsProvider userDetailsProvider) {
        this.userDetailsProvider = userDetailsProvider;
    }

    /**
     * 
     * @param username
     * @return 
     */
    @Override
    public UserDetails getUserDetails(String username) {
        return this.userDetailsProvider.findUserDetailsByUsername(username)
                .orElseThrow(() -> new AuthenticationException(
                        ACCOUNT_NOT_FOUND.format(username)));
    }

    /**
     *
     * @param username
     * @return
     */
    @Override
    public Set<String> getPermissionsFor(String username) {

        final UserDetails userDetails = this.userDetailsProvider
                .findUserDetailsByUsername(username)
                .orElseThrow(() -> new AuthenticationException(
                        Messages.ACCOUNT_NOT_FOUND.format(username)));
        
        return userDetails.getPermissions();
    }
}
