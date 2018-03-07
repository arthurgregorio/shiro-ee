package br.eti.arthurgregorio.shiroee.config.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import lombok.Getter;

/**
 * The details for the user fond on the LDAP/AD 
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 05/01/2018
 */
public final class LdapUser {

    @Getter
    private String name;
    @Getter
    private String mail;
    @Getter
    private String sAMAccountName;
    @Getter
    private String distinguishedName;
    @Getter
    private int userAccountControl;

    /**
     * 
     */
    public LdapUser() {
        this.name = null;
        this.mail = null;
        this.sAMAccountName = null;
        this.distinguishedName = null;
        this.userAccountControl = 0;
    }
    
    /**
     * 
     * @param attributes
     * @return
     * @throws NamingException 
     */
    public static LdapUser of(Attributes attributes) throws NamingException {

        final LdapUser bindable = new LdapUser();

        bindable.setName(attributes.get("name").get());
        bindable.setMail(attributes.get("mail").get());
        bindable.setsAMAccountName(attributes.get("sAMAccountName").get());
        bindable.setDistinguishedName(attributes.get("distinguishedName").get());
        bindable.setUserAccountControl(attributes.get("userAccountControl").get());
        
        return bindable;
    }

    /**
     * 
     * @param name 
     */
    public void setName(Object name) {
        this.name = String.valueOf(name);
    }

    /**
     * 
     * @param mail 
     */
    public void setMail(Object mail) {
        this.mail = String.valueOf(mail);
    }

    /**
     * 
     * @param sAMAccountName 
     */
    public void setsAMAccountName(Object sAMAccountName) {
        this.sAMAccountName = String.valueOf(sAMAccountName);
    }

    /**
     * 
     * @param distinguishedName 
     */
    public void setDistinguishedName(Object distinguishedName) {
        this.distinguishedName = String.valueOf(distinguishedName);
    }

    /**
     * 
     * @param userAccountControl 
     */
    public void setUserAccountControl(Object userAccountControl) {
        this.userAccountControl = Integer.valueOf(String.valueOf(userAccountControl));
    }
}
