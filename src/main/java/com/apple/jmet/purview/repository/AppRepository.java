package com.apple.jmet.purview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.App;

@Repository
public interface AppRepository extends JpaRepository<App, Long> {
    Integer countByStatus(String status);
    Optional<App> findByInternalName(String internalName);
    List<App> findByInternalNameOrExternalName(String internalName, String externalName);
}
