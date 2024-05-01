package com.apple.jmet.purview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apple.jmet.purview.domain.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
}
