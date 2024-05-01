package com.apple.jmet.purview.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

// import com.apple.ist.locksmith.error.LocksmithTokenParseException;
// import com.apple.ist.locksmith.oidc.LocksmithOidcService;
// import com.apple.ist.locksmith.util.Claims;
// import com.apple.ist.locksmith.util.ClaimsUtil;
// import com.apple.ist.locksmith.util.TokenUtil;
import com.apple.jmet.purview.domain.Role;
import com.apple.jmet.purview.repository.RoleRepository;
// import com.nimbusds.jwt.JWTClaimsSet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomLocksmithOidcService { //extends LocksmithOidcService {

    // @Autowired
    // private RoleRepository roleRepository;

    // @Override
    // public Collection<GrantedAuthority> getAuthoritiesFromClaimsSet(final String idToken) throws LocksmithTokenParseException {
    //     List<Role> roles = roleRepository.findAll();
    //     Collection<GrantedAuthority> authorities = new ArrayList<>();
    //     JWTClaimsSet claimsSet = TokenUtil.getClaimsFromIdToken(idToken);

    //     List<Long> groupIds = ClaimsUtil.getLongListValue(Claims.GROUPS_KEY, claimsSet);
    //     if (null != groupIds) {
    //         for (Long groupId : groupIds) {
    //             String roleCode = roles.stream().filter(role -> role.getGroupId() == groupId).findFirst().get().getCode();
    //             authorities.add(new SimpleGrantedAuthority("ROLE_" + roleCode));
    //         }
    //     }
    //     log.debug("GroupConverter: User has the roles: {} ", authorities);
    //     return authorities;
    // }
}
