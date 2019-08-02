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
package br.eti.arthurgregorio.shiroee.config;

import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

/**
 * Interface for configurations definition
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 05/03/2018
 */
public interface SecurityConfiguration {

    /**
     * Method used to configure the {@link FilterChainResolver} for http security
     *
     * @return configured filter chain to resolve http security
     */
    FilterChainResolver configureFilterChainResolver();

    /**
     * Method used to configure the {@link DefaultWebSecurityManager} to manage all the other parts of the framework,
     * i.e. the authentication process through the realms
     *
     * @return default web security manager
     */
    DefaultWebSecurityManager configureSecurityManager();
}
