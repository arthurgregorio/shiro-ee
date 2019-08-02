/*
 * Copyright 2018 Arthur Gregorio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.eti.arthurgregorio.shiroee.auth;

import lombok.Getter;
import org.apache.shiro.authc.credential.PasswordService;
import org.mindrot.jbcrypt.BCrypt;

import javax.enterprise.context.RequestScoped;

/**
 * The bcryp password service implementation
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
     * Constructor...
     */
    public PasswordEncoder() {
        this.logRounds = 10;
    }

    /**
     * Constructor...
     *
     * @param logRounds the log rounds for salt generation
     */
    public PasswordEncoder(int logRounds) {
        this.logRounds = logRounds;
    }

    /**
     * {@inheritDoc }
     *
     * @param plaintextPassword
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public String encryptPassword(Object plaintextPassword) throws IllegalArgumentException {
        return BCrypt.hashpw(String.valueOf(plaintextPassword), BCrypt.gensalt(this.logRounds));
    }

    /**
     * {@inheritDoc }
     *
     * @param submittedPlaintext
     * @param encrypted
     * @return
     */
    @Override
    public boolean passwordsMatch(Object submittedPlaintext, String encrypted) {
        return BCrypt.checkpw(String.valueOf((char[]) submittedPlaintext), encrypted);
    }

    /**
     * Same as {@link #passwordsMatch(java.lang.Object, java.lang.String)} but with string parameter for the
     * submitted password
     *
     * @param submittedPlaintext
     * @param encrypted
     * @return
     */
    public boolean passwordsMatch(String submittedPlaintext, String encrypted) {
        return BCrypt.checkpw(String.valueOf(submittedPlaintext), encrypted);
    }

    /**
     * Same as {@link #passwordsMatch(java.lang.Object, java.lang.String)} but with char-array parameter for the
     * submitted password
     *
     * @param submittedPlaintext
     * @param encrypted
     * @return
     */
    public boolean passwordsMatch(char[] submittedPlaintext, String encrypted) {
        return BCrypt.checkpw(String.valueOf(submittedPlaintext), encrypted);
    }
}
