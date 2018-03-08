package br.eti.arthurgregorio.shiroee.config.messages;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 01/03/2018
 */
public enum Messages implements MessageFormatter {
    
    LDAP_REPOSITORY_ERROR() {
        @Override
        public String format(Object... values) {
            return "Can't create a connection to the LDAP/AD repository";
        }
    },
    LDAP_CONFIGURATION_INCOMPLETE() {
        @Override
        public String format(Object... values) {
            return "Ldap url, bind user/password was not provided, check your configurations";
        }
    },
    NO_REALM_ERROR() {
        @Override
        public String format(Object... values) {
            return "No realm provided for configuration";
        }
    },
    CANT_LOAD_CONFIGURATION() {
        @Override
        public String format(Object... values) {
            return String.format("Can't load %s configuration file from classpath", values);
        }
    },
    CONFIGURATION_ERROR() {
        @Override
        public String format(Object... values) {
            return String.format("Can't configure class %s, review your configuration", values);
        }
    },
    INSTANCE_IS_INVALID() {
        @Override
        public String format(Object... values) {
            return String.format("Tha configuration class %s has a unsatisfied or ambiguous dependency", values);
        }
    },
    ACCOUNT_NOT_FOUND() {
        @Override
        public String format(Object... values) {
            return String.format("Can't find local user with username", values);
        }
    },
    ACCOUNT_BLOCKED() {
        @Override
        public String format(Object... values) {
            return String.format("User %s is blocked on local accounts", values);
        }
    },
    AUTHENTICATION_ERROR() {
        @Override
        public String format(Object... values) {
            return String.format("Can't authenticate user %s, try again", values);
        }
    },
    BIND_ERROR() {
        @Override
        public String format(Object... values) {
            return String.format("Can't bind user %s on LDAP directory ", values);
        }
    };
}
