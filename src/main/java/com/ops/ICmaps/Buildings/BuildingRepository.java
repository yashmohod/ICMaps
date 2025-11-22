package com.ops.ICmaps.Buildings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends JpaRepository<Building, String> {

    boolean existsBuildingByName(String name);
}
