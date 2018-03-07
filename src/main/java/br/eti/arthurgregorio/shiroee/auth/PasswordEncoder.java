package br.eti.arthurgregorio.shiroee.auth;

import javax.enterprise.context.RequestScoped;
import lombok.Getter;
import org.apache.shiro.authc.credential.PasswordService;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
@RequestScoped
public class PasswordEncoder implements PasswordService {

    @Getter
    private final int logRounds;

    /**
     * 
     */
    public PasswordEncoder() {
        this.logRounds = 10;
    }

    /**
     * 
     * @param logRounds 
     */
    public PasswordEncoder(int logRounds) {
        this.logRounds = logRounds;
    }

    /**
     * 
     * @param plaintextPassword
     * @return 
     */
    @Override
    public String encryptPassword(Object plaintextPassword) {
        if (plaintextPassword instanceof String) {
            return BCrypt.hashpw(
                    (String) plaintextPassword, BCrypt.gensalt(this.logRounds));
        }
        throw new IllegalArgumentException(
                "encryptPassword only support java.lang.String credential");
    }

    /**
     * 
     * @param submittedPlaintext
     * @param encrypted
     * @return 
     */
    @Override
    public boolean passwordsMatch(Object submittedPlaintext, String encrypted) {
        if (submittedPlaintext instanceof char[]) {
            return BCrypt.checkpw(String.valueOf(
                    (char[]) submittedPlaintext), encrypted);
        }
        throw new IllegalArgumentException("Only char[] is supported");
    }
}
