package com.jeffrey.services;

import com.google.gson.*;
import com.jeffrey.models.User;

import java.io.*;
import java.nio.file.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Simple JSON-backed registration storage */
public class RegistrationService {
    private final Path file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String, Registration> map = new ConcurrentHashMap<>();

    public RegistrationService(Path file) {
        this.file = file;
        load();
    }

    public synchronized void load() {
        try {
            if (!Files.exists(file)) {
                if (file.getParent() != null) Files.createDirectories(file.getParent());
                save(); // create empty file
                return;
            }
            String json = new String(Files.readAllBytes(file));
            if (json.trim().isEmpty()) return;
            JsonObject obj = gson.fromJson(json, JsonObject.class);
            if (obj == null) return;
            for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                Registration r = gson.fromJson(e.getValue(), Registration.class);
                map.put(e.getKey(), r);
            }
            System.out.println("Loaded " + map.size() + " registrations.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public Map<String, User> loadAllUsers() throws FileNotFoundException {
        Map<String, User> users = new HashMap<>();
        JsonObject json = JsonParser.parseReader(new FileReader(file.toFile())).getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            JsonObject u = entry.getValue().getAsJsonObject();
            User user = new User(
                    u.get("id").getAsString(),
                    u.get("name").getAsString(),
                    u.get("language").getAsString(),
                    u.get("email").getAsString(),
                    u.get("passwordHash").getAsString(),
                    u.get("wishlist").getAsString(),
                    u.get("createdAt").getAsString()
            );
            users.put(entry.getKey(), user);
        }

        return users;
    }
 // return User by discord id (reads your JSON map)
    public User loadUserByDiscordId(String discordId) throws FileNotFoundException {
        Map<String, User> all = loadAllUsers(); // your existing method
        return all.get(discordId);
    }

    public synchronized void save() {
        try (Writer w = Files.newBufferedWriter(file)) {
            gson.toJson(map, w);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isRegistered(String userId) {
        return map.containsKey(userId);
    }

    public synchronized void addRegistration(String userId, Registration r) {
        map.put(userId, r);
        save();
    }
    
    public synchronized boolean removeRegistration(String userId) {
        if (map.remove(userId) != null) {
            save();        // rewrite the JSON without that entry
            return true;
        }
        return false;
    }
    
    public Registration getDraftRegistration(String userId) {
        return map.get(userId);
    }


    public Collection<Registration> all() {
        return Collections.unmodifiableCollection(map.values());
    }
    
    public void ensureDraftRegistration(String discordId) {
        map.putIfAbsent(discordId, new Registration(discordId));
    }

    public void setLanguageFor(String discordId, String lang) {
        map.get(discordId).language = lang;
    }
    
    public String getLanguageFor(String discordId) {
    	return map.get(discordId).language;
    }

    public void setPersonalityAnswer(String discordId, String questionId, String answer) {
        Registration r = map.get(discordId);
        if (r.personalityAnswers == null) r.personalityAnswers = new HashMap<>();
        r.personalityAnswers.put(questionId, answer);
    }

    public Map<String, String> getPersonalityAnswers(String discordId) {
        return map.get(discordId).personalityAnswers;
    }
    // Data holder
    public static class Registration {
        public String id;
        public String name;
        public String language;
        public String email;
        public String passwordHash;
        public String wishlist;
        public String createdAt;
        public Map<String, String> personalityAnswers = new HashMap<>();

        // no-arg constructor needed for Gson
        public Registration() {}
        
        public Registration(String id) {
            this.id = id;
            this.createdAt = ZonedDateTime.now(ZoneId.of("America/Toronto"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-H:mm"));
        }

        public Registration(String id, String name, String language, String email, String passwordHash, String wishlist) {
            this.id = id;
            this.name = name;
            this.language = language;
            this.email = email;
            this.passwordHash = passwordHash;
            this.wishlist = wishlist;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd-H:mm");
            this.createdAt = ZonedDateTime.now(ZoneId.of("America/Toronto")).format(fmt);
        }
    }
}
