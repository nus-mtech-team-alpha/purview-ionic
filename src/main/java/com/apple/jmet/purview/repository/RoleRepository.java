package com.apple.jmet.purview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    List<Role> findByCodeIn(List<String> codes);
    List<Role> findByGroupIdIn(List<Long> groupIds);
    Role findByCode(String code);
}
