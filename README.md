# commons-auth

![logo](assets/commons-logo.svg)

[![Build Status](https://travis-ci.com/rocketbase-io/commons-auth.svg?branch=master)](https://travis-ci.com/rocketbase-io/commons-auth)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.rocketbase.commons/commons-auth/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.rocketbase.commons/commons-auth)
[![Maintainability](https://api.codeclimate.com/v1/badges/341f43fec3b1d060c58c/maintainability)](https://codeclimate.com/github/rocketbase-io/commons-auth/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/341f43fec3b1d060c58c/test_coverage)](https://codeclimate.com/github/rocketbase-io/commons-auth/test_coverage)

Adds auth services to your spring-boot applications. We at [rocketbase.io](https://www.rocketbase.io) develop many microservices and tried different tools and projects. All of them didn't matched our needs or were too bloated for smaller projects.

The implementation bases on spring-boot: mainly on **spring-mvc**, **spring-data**, **javax.mail** and **jjwt**

**Features:**
* authentication with jwt-tokens
* refresh token flow
* oauth2 endpoints
* registration + verification flow
* optional gravatar integration
* optional key values pairs to hold custom properties to user
* password forgot/reset flow
* admin endpoints to crud users + invites
* invite user flow to allow others to join your application
* forms to register/fogot-password/invite etc.

I've added an swagger api-documentation. You can find it within [src](./commons-auth-api/src/doc/swagger) of [swaggerHub](https://app.swaggerhub.com/apis-docs/melistik/commons-auth/)

## commons-auth-api

This module provides the DTOs and a client to communicate with the authentication endpoints.

## commons-auth-core

Containing an implementation for Token-Generators, Interfaces, Filters and many more...

## commons-auth-adapter

Containing spring security beans for an easy use within other spring application that are connected to a commons-auth backend...

### configuration properties

You can configure the behaviour of the service by following properties

| property                       | default         | explanation                                                  |
| ------------------------------ | --------------- | ------------------------------------------------------------ |
| auth.role-admin                | ADMIN           |                                                              |
| auth.role-user                 | USER            |                                                              |
| auth.token-secret              | *fixed 32chars* | **should get changed for production**<br>used to generate reset + verification tokens |
| auth.use-cache-time            | 30              | time in minutes - 0 means disabled                           |
| auth.verification-url          | null            | full qualified url to a custom UI that proceed the verification<br />?verification=VALUE will get append |
| auth.password-reset-url        | null            | full qualified url to a custom UI that proceed the password reset<br />?verification=VALUE will get append |
| auth.password-reset-expiration | 60              | time in minutes - after this period the token is invalid     |
| auth.invite-expiration         | 10080           | time in minutes (default 7-days) - after this period the token is invalid     |
| auth.base-url                  | http://localhost:8080 | required for authFormsController etc. need to specify correctly when you use it |
| auth.invite.enabled            | true            | activate invite endpoints (for "admins" and "invited")  |
For handling the JWT-Tokens and it's expirations you can use these properties:

| property                       | default         | explanation                                                  |
| ------------------------------ | --------------- | ------------------------------------------------------------ |
| auth.jwt.header                | Authorization   | standard approach                                            |
| auth.jwt.token-prefix          | Bearer          | standard approach with " " at the end                        |
| auth.jwt.uri-param          | token          | token could also get provided via url-param in case of downloads etc.                        |
| auth.jwt.secet          | **required**          | a base64 encoded jwt secret |
| auth.jwt.access-token-expiration          | 60          | time in minutes |
| auth.jwt.refresh-token-expiration          | 43200          | time in minutes - default means 30 days |

The required security for password could be configured by the following properties:

| property                 | default | explanation                                                  |
| ------------------------ | ------- | ------------------------------------------------------------ |
| auth.password.min-length | 8       | minimum length of password                                   |
| auth.password.max-length | 100     | maximum length of password                                   |
| auth.password.lowercase  | 1       | at least X chars of lowercase                                |
| auth.password.uppercase  | 1       | at least X chars of uppercase                                |
| auth.password.digit      | 1       | at least X chars of digit                                    |
| auth.password.special    | 1       | at least X chars of special<br />!"#$%&'()*+,-./:;<=>?@[\]^_`{\|}~ |

You can also configure the setup ot the username:

| property                         | default | explanation                                                  |
| -------------------------------- | ------- | ------------------------------------------------------------ |
| auth.username.min-length         | 3       | minimum length of username                                   |
| auth.username.max-length         | 20      | maximum length of username                                   |
| auth.username.special-characters | .-_     | allowed char apart from a-z and digits<br />username need to be lowercase in total |

The service contains also an registration flow that is by default enabled


| property                       | default         | explanation                                                  |
| ------------------------------ | --------------- | ------------------------------------------------------------ |
| auth.registration.enabled          | true          | allow users to register |
| auth.registration.verification          | true          | registered used needs to verify their email |
| auth.registration.verification-expiration          | 1440          | time in minutes - default means 1 day |
| auth.registration.role          | USER          | role of user after registration |

Gravatar is been used by default to fetch an avatar if nothing is provided.

| property                       | default         | explanation                                                  |
| ------------------------------ | --------------- | ------------------------------------------------------------ |
| auth.gravatar.enabled          | true          | should the avatar been initially loaded via gravatar service |
| auth.gravatar.size          | 256          | size of image |
| auth.gravatar.image          | RETRO          | type of placeholder style<br />https://gravatar.com/site/implement/images/ |
| auth.gravatar.rating          | null          | filter for spefic rating<br />https://gravatar.com/site/implement/images/ |

The Content of the emails (forgot-password + registration-verification) is been highly configurable. Furthermore a simple EmailTempalte engine is implemented that could also be used for other purposes.


| property                       | default         | explanation                                                  |
| ------------------------------ | --------------- | ------------------------------------------------------------ |
| auth.email.subject-prefix          | [Auth]          | prefix of email subject |
| auth.email.service-name          | commons-auth          | will get displayed in email-text |
| auth.email.support-email          | support@localhost          | will get displayed in email-text |
| auth.email.from-email          | no-reply@localhost          | sender of emails |
| auth.email.copyright-name          | commons-auth          | name of sender |
| auth.email.copyright-url          | link to github repro          | will get displayed in email-text |

To send emails the is used. This needs also some configurations:

| property                                         | default    |
| ------------------------------------------------ | ---------- |
| spring.mail.host                                 | *required* |
| spring.mail.port                                 | *required* |
| spring.mail.username                             | *required* |
| spring.mail.password                             | *required* |
| spring.mail.properties.mail.smtp.auth            | *required* |
| spring.mail.properties.mail.smtp.starttls.enable | *required* |

Forms configuration:

| property                                            | default    |
| --------------------------------------------------- | ---------- |
| auth.forms.title                                    | commons-auth |
| auth.forms.logo-src                                 | ./assets/rocketbase.svg |


## commons-auth-service

Containing nearly all services and functions to perform any task within the application - services for working with entities (without db-layer), performing invites, forgot/password-reset/registration flows,  sending emails etc.


## commons-auth-server

Only spring rest-controller + exception handler - logic is used from service-module

### configure spring-security

Apart from the configuration properties to get it running you need to configure and activate the security filter etc. Here you can find an example:

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties({AuthProperties.class, FormsProperties.class})
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthProperties authProperties;

    private final FormsProperties formsProperties;

    @Resource
    private UserDetailsService userDetailsService;

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // @formatter:off
        httpSecurity
            // activate CorsConfigurationSource
            .cors().and()
            // we don't need CSRF because our token is invulnerable
            .csrf().disable()

            // don't create session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()

            .authorizeRequests()
            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

            // allow anonymous resource requests
            .antMatchers(HttpMethod.GET,
                    "/",
                    "/assets/**",
                    "/favicon.ico"
            ).permitAll()
            // configure auth endpoint
            .antMatchers(authProperties.getAllPublicRestEndpointPaths()).permitAll()
            // allow logged in users get profile details etc.
            .antMatchers(authProperties.getAllAuthenticatedRestEndpointPaths()).authenticated()
            // login/logout, forgot, reset-password forms etc
            .antMatchers(formsProperties.getFormEndpointPaths()).permitAll()
            // registration form
            .antMatchers(formsProperties.getRegistrationEndpointPaths()).permitAll()
            // invite form
            .antMatchers(formsProperties.getInviteEndpointPaths()).permitAll()
            // user-management is only allowed by ADMINS
            .antMatchers(authProperties.getApiRestEndpointPaths()).hasRole(authProperties.getRoleAdmin())
            .antMatchers(authProperties.getApiInviteRestEndpointPaths()).hasRole(authProperties.getRoleAdmin())
            .antMatchers(authProperties.getUserSearchRestEndpointPaths()).authenticated()
            // secure all other api-endpoints
            .antMatchers(authProperties.getPrefix()+"/api/**").authenticated()
            .anyRequest().authenticated();

        // Custom JWT based security filter
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

        // allow also basic auth (optional)
        httpSecurity.httpBasic();

        // disable page caching templates
        httpSecurity.headers().cacheControl().disable();
        // @formatter:on
    }

    @Override
    public void configure(WebSecurity web) {
        // needed when basic auth is also set and oauth (with header auth is used)
        web.ignoring().antMatchers(authProperties.getOauthRestEndpointPaths());
    }

    // cors allow all
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(CorsConfiguration.ALL);
        configuration.addAllowedMethod(CorsConfiguration.ALL);
        configuration.addAllowedHeader(CorsConfiguration.ALL);
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(1800L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
```


## commons-auth-forms

Containing all forms for login, logout, forgot-password, register, email-verifaction etc.

It's based on Thymeleaf and uses [Bulma](http://bulma.io) from CDN.

## commons-auth-mongo

Containing the persistence layer for user via spring-data-mongo

Will create a collection with name of **user**

## commons-auth-jpa

Containing the persistence layer for user via jpa

Will create 3 tables: **USER**, **USER_ROLES**, **USER_KEYVALUE_PAIRS**

In order to get it running you need to add the following annotations to your project, so that jpa detects also the provided entites and repositories...

```java
@EnableJpaRepositories(basePackages = {"io.rocketbase.commons", "YOUR_PACKAGE"})
@EntityScan({"io.rocketbase.commons", "YOUR_PACKAGE"})
```

## commons-auth-test

Some base test classes and configurations.

## dependencies

Here you can find a simplified dependency-tree of commons-auth created 2020-01

```
---------------< io.rocketbase.commons:commons-auth-api >---------------

io.rocketbase.commons:commons-auth-api:jar:LATEST-SNAPSHOT
+- io.rocketbase.commons:commons-rest-api:jar:2.0.0:compile
+- com.google.guava:guava:jar:28.2-jre:compile

--------------< io.rocketbase.commons:commons-auth-core >---------------

io.rocketbase.commons:commons-auth-core:jar:LATEST-SNAPSHOT
+- io.rocketbase.commons:commons-auth-api:jar:LATEST-SNAPSHOT:compile
+- org.springframework.boot:spring-boot:jar:2.2.4.RELEASE:compile
+- org.springframework.security:spring-security-core:jar:5.2.1.RELEASE:compile
+- io.jsonwebtoken:jjwt-api:jar:0.10.7:compile

-------------< io.rocketbase.commons:commons-auth-adapter >-------------

io.rocketbase.commons:commons-auth-adapter:jar:LATEST-SNAPSHOT
+- io.rocketbase.commons:commons-auth-core:jar:LATEST-SNAPSHOT:compile
+- org.springframework.boot:spring-boot-autoconfigure:jar:2.2.4.RELEASE:compile
+- org.springframework.security:spring-security-config:jar:5.2.1.RELEASE:compile
+- org.springframework.security:spring-security-web:jar:5.2.1.RELEASE:compile

-------------< io.rocketbase.commons:commons-auth-service >-------------

io.rocketbase.commons:commons-auth-service:jar:LATEST-SNAPSHOT
+- io.rocketbase.commons:email-template-builder:jar:1.4.0:compile
|  +- io.pebbletemplates:pebble:jar:3.1.2:compile
|  \- org.jsoup:jsoup:jar:1.12.1:compile
+- org.springframework.data:spring-data-commons:jar:2.2.4.RELEASE:compile
+- org.springframework.boot:spring-boot-autoconfigure:jar:2.2.4.RELEASE:compile
+- org.springframework:spring-webmvc:jar:5.2.3.RELEASE:compile
+- org.passay:passay:jar:1.0:compile
+- org.springframework.boot:spring-boot-starter-mail:jar:2.2.4.RELEASE:compile
+- org.springframework.security:spring-security-config:jar:5.2.1.RELEASE:compile
+- org.springframework.security:spring-security-web:jar:5.2.1.RELEASE:compile
+- org.springframework.boot:spring-boot-configuration-processor:jar:2.2.4.RELEASE:compile

-------------< io.rocketbase.commons:commons-auth-server >--------------

io.rocketbase.commons:commons-auth-server:jar:LATEST-SNAPSHOT
+- io.rocketbase.commons:commons-auth-adapter:jar:LATEST-SNAPSHOT:compile
+- io.rocketbase.commons:commons-auth-service:jar:LATEST-SNAPSHOT:compile
+- io.rocketbase.commons:commons-rest-server:jar:2.0.0:compile
+- io.rocketbase.commons:commons-rest-errorpage:jar:2.0.0:compile
+- io.rocketbase.commons:email-template-builder:jar:1.4.0:compile
|  +- io.pebbletemplates:pebble:jar:3.1.2:compile
|  \- org.jsoup:jsoup:jar:1.12.1:compile
+- org.springframework.data:spring-data-commons:jar:2.2.4.RELEASE:compile
+- org.springframework.boot:spring-boot-autoconfigure:jar:2.2.4.RELEASE:compile
+- org.springframework:spring-webmvc:jar:5.2.3.RELEASE:compile
+- org.passay:passay:jar:1.0:compile
+- org.springframework.boot:spring-boot-starter-mail:jar:2.2.4.RELEASE:compile
+- org.springframework.security:spring-security-config:jar:5.2.1.RELEASE:compile
+- org.springframework.security:spring-security-web:jar:5.2.1.RELEASE:compile
+- org.springframework.boot:spring-boot-configuration-processor:jar:2.2.4.RELEASE:compile

--------------< io.rocketbase.commons:commons-auth-mongo >--------------

io.rocketbase.commons:commons-auth-mongo:jar:LATEST-SNAPSHOT
+- io.rocketbase.commons:commons-auth-service:jar:LATEST-SNAPSHOT:compile
+- org.springframework.data:spring-data-mongodb:jar:2.2.4.RELEASE:compile

---------------< io.rocketbase.commons:commons-auth-jpa >---------------

io.rocketbase.commons:commons-auth-jpa:jar:LATEST-SNAPSHOT
+- io.rocketbase.commons:commons-auth-service:jar:LATEST-SNAPSHOT:compile
+- org.springframework.data:spring-data-jpa:jar:2.2.4.RELEASE:compile

--------------< io.rocketbase.commons:commons-auth-forms >--------------

io.rocketbase.commons:commons-auth-forms:jar:LATEST-SNAPSHOT
+- io.rocketbase.commons:commons-auth-adapter:jar:LATEST-SNAPSHOT:compile
+- io.rocketbase.commons:commons-rest-errorpage:jar:2.0.0:compile
+- org.thymeleaf:thymeleaf:jar:3.0.11.RELEASE:compile
+- org.springframework:spring-webmvc:jar:5.2.3.RELEASE:compile
+- org.hibernate.validator:hibernate-validator:jar:6.0.18.Final:compile
+- org.springframework.boot:spring-boot-autoconfigure:jar:2.2.4.RELEASE:compile
+- org.springframework.security:spring-security-config:jar:5.2.1.RELEASE:compile
+- org.springframework.security:spring-security-web:jar:5.2.1.RELEASE:compile
```


### The MIT License (MIT)
Copyright (c) 2019 rocketbase.io

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
