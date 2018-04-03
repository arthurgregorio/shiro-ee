# Welcome to ShiroEE

ShiroEE is a simple java extension to make Apache Shiro more CDI aware and simplify the configuration processes without the use of _.ini_ files.

This library also provide a ready to use implementation of authentication realm for simultaneously use of database and LDAP/AD mechanichs.

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

ShiroEE has a ready to use configuration, basically you need to tell wich type of authentication to use and wich path you want to protect with permissions or roles based authorization. Below you can see the basics to use the extension.

### ShiroEE properties:

First of all, you will need to provide the basic configuration to the framework by providing a file with the name of _shiroee.properties_ in your _resources_ folder, keep at least the minimum obligatory parameters.

```properties
url.login=/index.xhtml
url.login_success=/secured/dashboard.xhtml
url.unauthorized=/error/401.xhtml
```

A list of the predefined values already shipped with the framework can be seen [here](https://github.com/arthurgregorio/shiro-ee/blob/master/src/main/java/br/eti/arthurgregorio/shiroee/config/Constants.java).
