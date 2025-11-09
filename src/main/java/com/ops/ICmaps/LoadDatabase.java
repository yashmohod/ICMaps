package com.ops.ICmaps;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ops.ICmaps.Node.Node;
import com.ops.ICmaps.Node.NodeRepository;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    private final NodeRepository repository;
    private final ObjectMapper objectMapper;
    public LoadDatabase(NodeRepository repository,ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper =objectMapper;
    }

    @Bean
    CommandLineRunner initDatabase() throws IOException, ParseException {

        JSONParser jp =  new JSONParser();

        FileReader fr = new FileReader("/home/mohod/projects/ICMaps/src/main/java/com/ops/ICmaps/phaseOne.json");

        JSONObject onj = (JSONObject) jp.parse(fr);
        JSONArray features = (JSONArray)onj.get("features");

        for (Object elem : features) {
            JSONObject featuer = (JSONObject)elem;

            JSONObject geometry = (JSONObject)featuer.get("geometry");
            String type = (String)geometry.get("type");
            JSONArray cords = (JSONArray) geometry.get("coordinates");
            JSONObject prop = (JSONObject)featuer.get("properties");
            if(type.equals("Point")){
                
                System.out.println(prop.get("id")+","+cords.get(0)+","+cords.get(1));

                Node newNode = new Node(
                    (String)prop.get("id"),
                    (Double)cords.get(1),
                    (Double)cords.get(0));

                repository.findById(newNode.getId())
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
            }

            // if(type.equals("LineString")){
  
            //     String from = (String)prop.get("from");
            //     String to = (String)prop.get("to");
            //     System.out.println(from+" -> "+to);
            // }

            // System.out.println(type);

            // System.out.println(geometry);

        }

        

        return args -> {
            log.info("Ran - Preloading.");
        };
    }
}