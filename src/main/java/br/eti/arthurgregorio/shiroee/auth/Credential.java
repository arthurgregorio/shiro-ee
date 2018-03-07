package br.eti.arthurgregorio.shiroee.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
@NoArgsConstructor
public final class Credential {

    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private boolean rememberMe;    
    
    /**
     * @return 
     */
    public UsernamePasswordToken asToken() {
        return new UsernamePasswordToken(this.username, this.password, this.rememberMe);
    }
}
