package com.apple.jmet.purview.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.Feature;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {

    Optional<Feature> findByName(String name);
}
