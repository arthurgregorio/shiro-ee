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
 * The definition for configration of Shiro with this library
 * 
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 05/03/2018
 */
public interface SecurityConfiguration {

    /**
     * This method is used to configure the {@link FilterChainResolver} for 
     * security of the http paths 
     * 
     * @return the configured filter chain to resolve http path security
     */
    FilterChainResolver configurteFilterChainResolver();
    
    /**
     * This method is used to configure the {@link DefaultWebSecurityManager} to
     * manage all the other parts of the framework, i.e. the authentication 
     * process throug the realms
     * 
     * @return the default web security manager for use with Shiro
     */
    DefaultWebSecurityManager configureSecurityManager();
}
