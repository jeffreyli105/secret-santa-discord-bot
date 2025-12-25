package com.jeffrey.services;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class AssignmentService {
	private final Path file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String, String> map = new ConcurrentHashMap<>();
    
    public AssignmentService(Path file) {
        this.file = file;
        load();
    }
    
    public synchronized void load() {
        try {
            if (!Files.exists(file)) {
                if (file.getParent() != null) Files.createDirectories(file.getParent());
                save();
                return;
            }
            String json = new String(Files.readAllBytes(file));
            if (json.trim().isEmpty()) return;
            JsonObject obj = gson.fromJson(json, JsonObject.class);
            if (obj == null) return;
            for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                String r = gson.fromJson(e.getValue(), String.class);
                map.put(e.getKey(), r);
            }
            System.out.println("Loaded " + map.size() + " assignments.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public synchronized void save() {
        try (Writer w = Files.newBufferedWriter(file)) {
            gson.toJson(map, w);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    // return assigned recipient discord id (persist assignments in /start and load here)
    public String getAssignmentFor(String discordId) {
		return map.get(discordId);
    }
    
    public String getGiverFor(String discordId) {
    	System.out.println(map);
    	for (Entry<String, String> entry : map.entrySet()) {
    		if (entry.getValue().equals(discordId)) return entry.getKey();
    	}
    	return null;
    }
    
    public Map<String, String> getAllAssignments() {
    	return new HashMap<>(this.map);
    }
    
    public void addAssignments(Map<String, String> assignments) {
    	map.clear();
    	for (Entry<String, String> entry : assignments.entrySet()) {
    		map.put(entry.getKey(), entry.getValue());
    	}
    	save();
    }
}
