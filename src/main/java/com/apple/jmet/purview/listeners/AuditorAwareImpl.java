package com.apple.jmet.purview.listeners;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContext;
// import org.springframework.security.core.context.SecurityContextHolder;

// import com.apple.ist.locksmith.oidc.LocksmithUserService;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // SecurityContext securityContext = SecurityContextHolder.getContext();
        // Optional<String> opt = Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
        // return Optional.of(opt.orElse("SYSTEM"));
        return Optional.of("SYSTEM");
    }
    
    // private static String extractPrincipal(Authentication authentication) {
    //     if (authentication == null) {
    //         return null;
    //     } 
    //     return LocksmithUserService.getUser().getEmailAddress();
    // }
    
}
