package com.apple.jmet.purview.web;

import java.util.List;

import com.apple.jmet.purview.domain.Person;
import com.apple.jmet.purview.domain.Role;
import com.apple.jmet.purview.domain.SimpleAuthority;

public class TestUtils {

    private TestUtils() {}

    public static Person getTestPerson(){
        return Person.builder()
            .id(11L)
            .firstName("Joseph")
            .lastName("Bagnes")
            .email("jbagnes@apple.com")
            .username("jbagnes")
            .team("SRE")
            .status("ACTIVE")
            .accountNonLocked(true)
            .roles(List.of(Role.builder().code("SRE").build()))
            .authorities(List.of(SimpleAuthority.builder().authority("ROLE_SRE").build()))
            .build();
    }

}
