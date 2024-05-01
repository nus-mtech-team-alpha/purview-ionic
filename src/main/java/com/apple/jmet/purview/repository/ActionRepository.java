package com.apple.jmet.purview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.Action;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
    
    @Query("SELECT a FROM Action a WHERE a.id != ?1 AND a.app.id = ?2 AND a.verified = false")
    List<Action> findSimilarUnapprovedActionsExceptThis(Long actionId, Long appId);

    List<Action> findAllByVerified(boolean verified);
}
