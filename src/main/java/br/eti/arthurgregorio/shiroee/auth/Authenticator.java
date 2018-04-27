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

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * The basic authenticator, with this class you can authenticate the users and
 * logout them.
 *
 * All the verifications are made throug the current {@link Subject}
 *
 * @author Arthur Gregorio
 *
 * @since 1.0.0
 * @version 1.0.0, 31/01/2018
 */
@Named
@RequestScoped
public class Authenticator implements Serializable {

    private final Subject subject;

    /**
     * The constructor
     */
    public Authenticator() {
        this.subject = SecurityUtils.getSubject();
    }

    /**
     * Login the user by his credentials
     *
     * @param credential the user credentials
     */
    public void login(Credential credential) {
        this.subject.login(credential.asToken());
    }

    /**
     * Logout the current subject
     */
    public void logout() {
        this.subject.logout();
    }

    /**
     * @return if authentication is needed for the current subject
     */
    public boolean authenticationIsNeeded() {
        return !this.subject.isAuthenticated();
    }
}
