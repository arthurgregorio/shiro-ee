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

import br.eti.arthurgregorio.shiroee.config.http.HttpSecurityBuilder;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;

/**
 * The definition for http security configuration
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 02/03/2018
 */
public interface HttpSecurityConfiguration {

    /**
     * Configure the http security builder to use in conjunction with the main
     * configuration to build the {@link FilterChainResolver}
     * 
     * @return the http access rules
     */
    HttpSecurityBuilder configureHttpSecurity();
}
