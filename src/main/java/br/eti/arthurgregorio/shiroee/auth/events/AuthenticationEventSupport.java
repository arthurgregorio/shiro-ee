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
package br.eti.arthurgregorio.shiroee.auth.events;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationListener;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.PrincipalCollection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * Implementation of the {@link AuthenticationListener} used to notify others about the steps of the authentication
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.3.1, 27/04/2018
 */
@ApplicationScoped
public class AuthenticationEventSupport implements AuthenticationListener {

    @Inject
    @OnLoginSuccess
    private Event<String> loginSuccessEvent;
    @Inject
    @OnLoginFailed
    private Event<String> loginFailedEvent;
    @Inject
    @OnLogout
    private Event<String> logoutEvent;

    /**
     * {@inheritDoc }
     *
     * @param token
     * @param info
     */
    @Override
    public void onSuccess(AuthenticationToken token, AuthenticationInfo info) {
        this.loginSuccessEvent.fire(String.valueOf(token.getPrincipal()));
    }

    /**
     * {@inheritDoc }
     *
     * @param token
     * @param exception *spo
     */
    @Override
    public void onFailure(AuthenticationToken token, AuthenticationException exception) {
        this.loginFailedEvent.fire(String.valueOf(token.getPrincipal()));
    }

    /**
     * {@inheritDoc }
     *
     * @param principals
     */
    @Override
    public void onLogout(PrincipalCollection principals) {
        this.logoutEvent.fire(String.valueOf(principals.getPrimaryPrincipal()));
    }
}