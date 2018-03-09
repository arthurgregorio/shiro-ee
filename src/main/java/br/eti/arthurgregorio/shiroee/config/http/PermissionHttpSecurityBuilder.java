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
package br.eti.arthurgregorio.shiroee.config.http;

import static br.eti.arthurgregorio.shiroee.config.Constants.REQUIRED_PERMISSION_OP;

/**
 * The builder for permission based http path security
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 03/02/2018
 */
public class PermissionHttpSecurityBuilder extends HttpSecurityBuilder {

    private final String builderOperator;

    /**
     * The constructor, from the configuration initialize the default operator
     */
    public PermissionHttpSecurityBuilder() {
        super();
        
        // from the configuration, get the operator for permissions or use the
        // default one
        final String operator = this.configuration.getString(
                "operator.required_permission", REQUIRED_PERMISSION_OP);
        
        this.builderOperator = operator + "[%s]";
    }

    /**
     * {@inheritDoc }
     * 
     * @return 
     */
    @Override
    public String getBuilderOperator() {
        return this.builderOperator;
    }
}
