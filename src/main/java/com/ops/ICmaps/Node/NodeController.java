package com.ops.ICmaps.Node;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/node")
public class NodeController {

    private final NodeRepository repository;
    private final ObjectMapper objectMapper;
    public NodeController(NodeRepository repository,ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper =objectMapper;
    }

    @PostMapping("/add_update")
    public ObjectNode AddUpdateNode(@RequestBody Node newNode){
        ObjectNode objectNode = objectMapper.createObjectNode();
        String respMessage = repository.findById(newNode.getId())
                .map(curNode -> {
                    curNode.setLat(newNode.getLat());
                    curNode.setLng(newNode.getLng());
                    repository.save(curNode);
                    return "Node Updated!";
                })
                .orElseGet(() -> {
                    repository.save(newNode);
                    return "Node Added!";
                });

        objectNode.put("message", respMessage);
        return objectNode;
    }


    @DeleteMapping("/delete/{id}")
    public ObjectNode DeleteNode(@PathVariable String id) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        System.out.println(id);
        try{
            repository.deleteById(id);
            objectNode.put("message", "Node Deleted!");
            return objectNode;
        }
        catch(Exception e){
            objectNode.put("message", e.getMessage());
            return objectNode;
        }

    }



    @GetMapping("/load")
    public void loadfeatures() throws IOException, ParseException{
        
        

    }







}
