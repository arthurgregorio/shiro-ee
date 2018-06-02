# Welcome to ShiroEE

ShiroEE is a simple java extension to make Apache Shiro more CDI aware and simplify the configuration processes without the use of _.ini_ files.

This library also provide a ready to use implementation of authentication realm for simultaneously use of database and LDAP/AD mechanics.

## In your project:

Download one of the releases under the [release page](https://github.com/arthurgregorio/shiro-ee/releases) or just put it as da dependency in your maven project:

```xml
<dependency>
    <groupId>br.eti.arthurgregorio</groupId>
    <artifactId>shiro-ee</artifactId>
    <version>${shiroee.version}</version>
</dependency>
```

## Quick use guide:

ShiroEE has a ready to use configuration, basically you need to create the properties file with the basic configurations, tell wich type of authentication you want to use and wich path you want to protect with permissions or roles authorization.

These three process are described below.

### ShiroEE properties:

First of all, you need to provide the basic configuration to the framework creating a file named _shiroee.properties_ in your _resources_ folder, at least the obligatory parameters need to be set.

```properties
ldap.enabled=false # use true if you want to enable LDAP
url.login=/index.xhtml
url.login_success=/secured/dashboard.xhtml
url.unauthorized=/error/401.xhtml
```

A list of the predefined values already shipped with the framework can be seen [here](https://github.com/arthurgregorio/shiro-ee/blob/master/src/main/java/br/eti/arthurgregorio/shiroee/config/Constants.java).

### Configure authentication:

To configure the authentication process you need to create a class to tell ShiroEE wich realm they need to activate in your application. To do that, [see this sample class](https://github.com/arthurgregorio/library/blob/master/src/main/java/br/eti/arthurgregorio/library/infrastructure/shiro/SecurityRealmConfiguration.java).

### Configure URL authorizations:

To configure the URL authorization of you project you just need to implement the ```HttpSecurityConfiguration``` class and with the builder class (```HttpSecurityBuilder```) provided by the project configure wich path's you need to protect. A sample of this process [can be found here](https://github.com/arthurgregorio/library/blob/master/src/main/java/br/eti/arthurgregorio/library/infrastructure/shiro/PathSecurityConfiguration.java).

### Bonus:

The authentication control and the session control are the parts managed by the Shiro Framework but you need to implement some classes to access this features without compromissing the coupling of your project. To help with that, the [Library](https://github.com/arthurgregorio/library) project also have samples of this mechanics too, they can be found [here](https://github.com/arthurgregorio/library/blob/master/src/main/java/br/eti/arthurgregorio/library/application/controllers/AuthenticationBean.java) and [here](https://github.com/arthurgregorio/library/blob/master/src/main/java/br/eti/arthurgregorio/library/application/controllers/UserSessionBean.java).

## Project with ShiroEE:

If you want a full project where ShiroEE is configured with authentication through LDAP and Database, you can check the [Library](https://github.com/arthurgregorio/library) project made by me to provide a full base architecture for JEE applications.
