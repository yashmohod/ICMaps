package com.ops.ICmaps.Edge;

import com.ops.ICmaps.Node.Node;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "edges", indexes = {
        @Index(name = "idx_edges_from", columnList = "from_node_id"),
        @Index(name = "idx_edges_to", columnList = "to_node_id")
})
public class Edge {

    @Id
    private String key;
    private String fromNode;
    private String toNode;

    // any metadata: distance in meters, cost, speed limit, etc.
    private double distanceMeters;

    protected Edge() {

    }

    public Edge(double distanceMeters, String toNode, String fromNode, String key) {
        this.distanceMeters = distanceMeters;
        this.toNode = toNode;
        this.fromNode = fromNode;
        this.key = key;
    }

    public String getKey() {
        return key;
    }



    public double getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Double.compare(distanceMeters, edge.distanceMeters) == 0 && Objects.equals(key, edge.key) && Objects.equals(fromNode, edge.fromNode) && Objects.equals(toNode, edge.toNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, fromNode, toNode, distanceMeters);
    }
}
