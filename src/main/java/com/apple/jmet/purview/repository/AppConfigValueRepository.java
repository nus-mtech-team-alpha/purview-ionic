package com.apple.jmet.purview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.AppConfigValue;

@Repository
public interface AppConfigValueRepository extends JpaRepository<AppConfigValue, Long> {

    @Modifying
    @Query("update AppConfigValue set rolloutPhase = ?1 where val = ?2 and appConfig.id = ?3")
    void updateRolloutPhase(String rolloutPhase, String val, Long appConfigId);

    @Modifying
    @Query("update AppConfigValue set rolloutPhase = ?1 where val IN (?2) and appConfig.id = ?3")
    void updateRolloutPhaseBulk(String rolloutPhase, List<String> vals, Long appConfigId);

}
