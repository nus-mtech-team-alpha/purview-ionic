package com.apple.jmet.purview.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// import com.apple.ist.locksmith.oidc.LocksmithOidcAuthenticationFilter;
// import com.apple.ist.locksmith.oidc.config.LocksmithOidcProperties;

@Configuration
public class LocksmithConfig {

    @Value("${locksmith.oidc.env}")
    String env;
    @Value("${locksmith.oidc.client.id}")
    String clientId;
    @Value("${locksmith.oidc.client.secret}")
    String clientSecret;
    @Value("${locksmith.oidc.client.redirect.url}")
    String clientRedirectUrl;
    @Value("${locksmith.oidc.client.scopes}")
    String scopes;

    // @Bean
    // public LocksmithOidcAuthenticationFilter authenticationFilter() {
        
    //     LocksmithOidcProperties props = LocksmithOidcProperties.build(env, clientId, clientSecret, clientRedirectUrl, scopes);

    //     props.setAuthenticatedRoutes(List.of("/api/**"));
    //     props.setNoAuthenticationRoutes(List.of("/", "/actuator/"));
    //     props.setTokenAutoRefreshEnabled(true);
    //     props.setSmartRedirectEnabled(true); 

    //     return new LocksmithOidcAuthenticationFilter(props);
    // }

}
