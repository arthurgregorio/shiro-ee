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

/**
 * All the configurations constants
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 05/03/2018
 */
public interface Constants {

    // LDAP configs
    String LDAP_BASE_DN = "OU=Application,DC=br,DC=eti,DC=arthurgregorio";
    String LDAP_SEARCH_FILTER = "(&(sAMAccountName={0})(objectclass=user))";

    // the default parameters for configuration
    String URL_LOGIN = "/index";
    String URL_UNAUTHORIZED = "/error/401";
    String URL_LOGIN_SUCCESS = "/secured/index";
    String URL_ROOT_SECURED_PATH = "/secured/**";
    String URL_LOGOUT_PATH = "/logout";

    // the filter operators for path matching mechanism
    String AUTHENTICATED_OP = "authc";
    String LOGOUT_OP = "logout";
    String ANONYMOUS_OP = "anon";
    String REQUIRED_ROLE_OP = "roles";
    String REQUIRED_PERMISSION_OP = "perms";
}
