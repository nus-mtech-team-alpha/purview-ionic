package com.apple.jmet.purview.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;

@Data
@Builder
@ToString
@Entity
public class Person { //implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String team;
    private String status;
    @Column(nullable = false, unique = true)
    private String username;
    @JsonIgnore
    @JsonProperty(value = "password")
    private String password;
    @Column(name = "account_non_locked")
    private boolean accountNonLocked;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;
    @Transient
    private List<SimpleAuthority> authorities;

    @Tolerate
    public Person() {
        /* no-arg constructor */ }

    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // @Override
    // public Collection<SimpleGrantedAuthority> getAuthorities() {
    //     List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    //     this.roles.forEach(role -> {
    //         authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
    //     });
    //     return authorities;
    // }

    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // @Override
    // public boolean isAccountNonExpired() {
    //     return true;
    // }

    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // @Override
    // public boolean isAccountNonLocked() {
    //     return accountNonLocked;
    // }

    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // @Override
    // public boolean isCredentialsNonExpired() {
    //     return true;
    // }

    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // @Override
    // public boolean isEnabled() {
    //     return true;
    // }
}
