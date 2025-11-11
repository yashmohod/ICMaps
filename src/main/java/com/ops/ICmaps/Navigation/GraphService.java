package com.ops.ICmaps.Navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.ops.ICmaps.Edge.Edge;
import com.ops.ICmaps.Edge.EdgeLight;
import com.ops.ICmaps.Edge.EdgeRepository;
import com.ops.ICmaps.Node.Node;
import com.ops.ICmaps.Node.NodeRepository;

@Service
public class GraphService {

    private final EdgeRepository edges;
    private final NodeRepository nodes;

    // Volatile so reads require no locking
    private volatile Map<String, List<Adj>> adj = Map.of();

    private volatile Map<String, double[]> nodeCoords = Map.of(); // id -> [lat, lng]

    public GraphService(EdgeRepository edges, NodeRepository nodes) {
        this.edges = edges;
        this.nodes = nodes;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadGraph() {
        rebuild();
        rebuildNodes(nodes.findAll());
    }

    public void rebuildNodes(List<Node> allNodes) {
        Map<String, double[]> m = new HashMap<>();
        for (var n : allNodes)
            m.put(n.getId(), new double[] { n.getLat(), n.getLng() });
        nodeCoords = Map.copyOf(m);
    }

    public String nearestNodeId(double lat, double lng) {
        String best = null;
        double bestD2 = Double.POSITIVE_INFINITY;
        for (var e : nodeCoords.entrySet()) {
            var c = e.getValue();
            double d2 = haversineSquared(lat, lng, c[0], c[1]); // or simple euclidean on small boxes
            if (d2 < bestD2) {
                bestD2 = d2;
                best = e.getKey();
            }
        }
        return best;
    }

    private static double haversineSquared(double lat1, double lon1, double lat2, double lon2) {
        // You can return actual meters too; squared used only if you compare.
        double R = 6371000.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return (R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }

    public static record Adj(String to, double distance) {
    }

    public void rebuild() {
        Map<String, List<Adj>> map = new HashMap<>();

        for (EdgeLight e : edges.findAllLight()) {
            map.computeIfAbsent(e.getFromNode(), k -> new ArrayList<>())
                    .add(new Adj(e.getToNode(), e.getDistanceMeters()));
            map.computeIfAbsent(e.getToNode(), k -> new ArrayList<>())
                    .add(new Adj(e.getFromNode(), e.getDistanceMeters()));
        }

        // freeze lists + map for thread safety, zero lock reads
        map.replaceAll((k, v) -> List.copyOf(v));
        adj = Map.copyOf(map);
    }

    // For A* / Dijkstra expansions
    public List<Adj> neighbors(String fromId) {
        return adj.getOrDefault(fromId, List.of());
    }

    public List<Edge> BFS(double lat, double lng) {

        String startId = nearestNodeId(lat, lng);
        Node Staring;
        return null;
    }

    private List<Node> BFShelper(Node cur) {
        int V = adj.size();
        Set<String> visited = new HashSet<String>();
        ArrayList<Node> res = new ArrayList<Node>();

        Queue<Node> q = new LinkedList<>();
        visited.add(cur.getId());
        q.add(cur);

        while (!q.isEmpty()) {
            Node curr = q.poll();
            res.add(curr);

            // visit all the unvisited
            // neighbours of current node
            for (int x : adj.get(curr)) {
                if (!visited[x]) {
                    visited[x] = true;
                    q.add(x);
                }
            }
        }

        return res;
    }

    // distane in meters
    Double calDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        Double R = 6371.0; // Radius of the earth in km
        Double dLat = Math.toRadians(lat2 - lat1); // deg2rad below
        Double dLon = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double d = R * c; // Distance in km
        return d * 1000; // Distance in km
    }
}
