package com.ops.ICmaps.NavMode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NavModeRepository extends JpaRepository<NavMode, Long> {

    boolean existsNavModeByName(String name);
}
