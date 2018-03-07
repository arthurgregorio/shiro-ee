package br.eti.arthurgregorio.shiroee.config;

import java.util.Set;
import org.apache.shiro.realm.Realm;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 05/03/2018
 */
public interface RealmConfiguration {
    
    /**
     * 
     * @return 
     */
    Set<Realm> configureRealms();
}
