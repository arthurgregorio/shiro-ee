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

import org.apache.shiro.web.env.DefaultWebEnvironment;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

/**
 * {@link EnvironmentLoaderListener} used to configure the framework
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
@WebListener
public class SecurityWebListener extends EnvironmentLoaderListener {

    @Inject
    private DefaultSecurityConfiguration configuration;

    /**
     * {@inheritDoc }
     *
     * @param event
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        event.getServletContext().setInitParameter(ENVIRONMENT_CLASS_PARAM, DefaultWebEnvironment.class.getName());
        super.contextInitialized(event);
    }

    /**
     * {@inheritDoc }
     *
     * @param servletContext
     * @return
     */
    @Override
    protected WebEnvironment createEnvironment(ServletContext servletContext) {

        final DefaultWebEnvironment environment = (DefaultWebEnvironment) super.createEnvironment(servletContext);

        environment.setSecurityManager(this.configuration.configureSecurityManager());
        environment.setFilterChainResolver(this.configuration.configurteFilterChainResolver());

        return environment;
    }
}