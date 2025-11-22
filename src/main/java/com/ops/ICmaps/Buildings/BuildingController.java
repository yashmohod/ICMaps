package com.ops.ICmaps.Buildings;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ops.ICmaps.Node.Node;
import com.ops.ICmaps.Node.NodeRepository;

@RestController
@CrossOrigin
@RequestMapping("/building")
public class BuildingController {

    private final NodeRepository nr;
    private final BuildingRepository br;
    private final ObjectMapper objectMapper;

    public BuildingController(BuildingRepository br, NodeRepository nr, ObjectMapper objectMapper) {
        this.nr = nr;
        this.br = br;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/")
    @ResponseBody
    public ObjectNode AddBuilding(@RequestBody Building newBuilding) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        try {
            br.save(newBuilding);
            objectNode.put("message", "Building added!");
        } catch (Exception e) {
            objectNode.put("message", e.toString());
        }
        return objectNode;
    }

    @PutMapping("/")
    @ResponseBody
    public ObjectNode EditBuilding(@RequestBody Building newBuilding) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        String respMessage = br.findById(newBuilding.getId()).map(curBuilding -> {
            curBuilding.setName(newBuilding.getName());
            br.save(curBuilding);
            return "Building name update!";
        }).orElseGet(() -> {
            return "Building not found!";
        });
        objectNode.put("message", respMessage);
        return objectNode;
    }

    @PatchMapping("/setpolygon")
    @ResponseBody
    public ObjectNode SetBuildingPolyGon(@RequestBody ObjectNode args) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        String buildingId = args.get("buildingId").asText();
        Double lat = args.get("lat").asDouble();
        Double lng = args.get("lng").asDouble();
        String polyGon = args.get("polygonJson").asText();

        try{
            Building curBuilding = br.findById(buildingId).get();
            curBuilding.setLat(lat);
            curBuilding.setLng(lng);
            curBuilding.setPolyGon(polyGon);
            br.save(curBuilding);
            objectNode.put("message", "PolyGon Updated!");
        }catch(Exception e){
            objectNode.put("message", e.toString());
        }

        
        return objectNode;
    }

    @DeleteMapping("/setpolygon")
    @ResponseBody
    public ObjectNode RemoveBuildingPolyGon(@RequestBody ObjectNode args) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        String buildingId = args.get("buildingId").asText();
        try{
            Building curBuilding = br.findById(buildingId).get();
            curBuilding.setPolyGon("");
            br.save(curBuilding);
            objectNode.put("message", "PolyGon Updated!");
        }catch(Exception e){
            objectNode.put("message", e.toString());
        }

        
        return objectNode;
    }

    @DeleteMapping("/")
    @ResponseBody
    public ObjectNode DeleteBuilding(@RequestBody ObjectNode args) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        String buildingId = args.get("id").asText();

        try {
            br.deleteById(buildingId);
            objectNode.put("message", "Building deleted!");
        } catch (Exception e) {
            objectNode.put("message", e.toString());
        }

        return objectNode;
    }

    public record BuildingsDTO(String id, String name, double lat, double lng,String polyGon) {

    }

    @GetMapping("/")
    public ObjectNode GetAllBuildings() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        List<Building> allBuildings = br.findAll();
        List<BuildingsDTO> NavmodeDTOs = allBuildings.stream()
                .map(e -> new BuildingsDTO(
                        e.getId(),
                        e.getName(),
                        e.getLat(),
                        e.getLng(),
                    e.getPolyGon()))
                .toList();

        objectNode.set("buildings", objectMapper.valueToTree(NavmodeDTOs));
        return objectNode;
    }

    @GetMapping("/nodesget")
    public ObjectNode GetAllBuildingNodes(@RequestParam String id) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        Building building = br.findById(id).get();
        objectNode.set("nodes", objectMapper.valueToTree(building.getNodes()));
        return objectNode;
    }

    @GetMapping("/buildingpos")
    public ObjectNode GetBuildingPos(@RequestParam String id) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        Building building = br.findById(id).get();
        // Set<Node> nodes = building.getNodes();

        // double x = 0;
        // double y = 0;
        // double z = 0;

        // for (Node curNode : nodes) {
        //     double lat_rad = Math.toRadians(curNode.getLat());
        //     double lng_rad = Math.toRadians(curNode.getLng());
        //     x += Math.cos(lat_rad) * Math.cos(lng_rad);
        //     y += Math.cos(lat_rad) * Math.sin(lng_rad);
        //     z += Math.sin(lat_rad);
        // }

        // x /= nodes.size();
        // y /= nodes.size();
        // z /= nodes.size();
        // double hyp = Math.sqrt(x * x + y * y);
        // double lat = Math.toDegrees(Math.atan2(z, hyp));
        // double lng = Math.toDegrees(Math.atan2(y, x));

        objectNode.put("lat", building.getLat());
        objectNode.put("lng", building.getLng());
        return objectNode;
    }

    @PostMapping("/nodeadd")
    public ObjectNode NodeAdd(@RequestBody ObjectNode args) {

        ObjectNode objectNode = objectMapper.createObjectNode();
        String buildingId = args.get("buildingId").asText();
        String nodeId = args.get("nodeId").asText();

        Building curBuilding = br.findById(buildingId).get();
        Node curNode = nr.findById(nodeId).get();

        curBuilding.addNode(curNode);
        nr.save(curNode);
        br.save(curBuilding);
        return objectNode;
    }

    @PostMapping("/noderemove")
    public ObjectNode RemoveNode(@RequestBody ObjectNode args) {

        ObjectNode objectNode = objectMapper.createObjectNode();
        String buildingId = args.get("buildingId").asText();
        String nodeId = args.get("nodeId").asText();

        Building curBuilding = br.findById(buildingId).get();
        Node curNode = nr.findById(nodeId).get();

        curBuilding.removeNode(curNode);
        nr.save(curNode);
        br.save(curBuilding);
        return objectNode;
    }
}
