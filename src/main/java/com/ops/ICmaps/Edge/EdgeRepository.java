package com.ops.ICmaps.Edge;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EdgeRepository extends JpaRepository<Edge, String> {

    @Query("select e.fromNode as fromNode, e.toNode as toNode, e.distanceMeters as distanceMeters from Edge e")
    List<EdgeLight> findAllLight();

    List<Edge> findByFromNode(String fromNode);

    List<Edge> findByToNode(String toNode);

}
