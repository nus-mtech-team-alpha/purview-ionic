package com.apple.jmet.purview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.ConfigType;

@Repository
public interface ConfigTypeRepository extends JpaRepository<ConfigType, Long> {

}
