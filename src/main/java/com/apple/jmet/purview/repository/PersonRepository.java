package com.apple.jmet.purview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    
    Optional<Person> findFirstByTeam(String team);
    List<Person> findByTeam(String team);
    Optional<Person> findByUsername(String username);
    Optional<Person> findByEmail(String email);
}
