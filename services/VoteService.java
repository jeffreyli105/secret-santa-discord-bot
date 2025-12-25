package com.jeffrey.services;

import com.google.gson.*;
import com.jeffrey.models.User;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VoteService {
    private final Path file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // runtime state
    private List<String> recipientsOrder = new ArrayList<>();
    private int currentIndex = 0;
    private final Map<String, Map<String, String>> votes = new ConcurrentHashMap<>();
    private final Map<String, Integer> points = new ConcurrentHashMap<>();
    private boolean finished = false;

    public VoteService(Path file) {
        this.file = file;
        load();
    }

    public synchronized void startSession(Map<String, String> assignments) {
        recipientsOrder = new ArrayList<>(assignments.keySet());
        // optional: sort by display name externally; keep deterministic order by sorting by id if desired
        currentIndex = 0;
        votes.clear();
        points.clear();
        finished = false;
        save();
    }
    
    public synchronized int getCurrentIndex() {
    	return currentIndex;
    }

    public synchronized Optional<String> getCurrentRecipient() {
        if (recipientsOrder.isEmpty() || currentIndex >= recipientsOrder.size()) return Optional.empty();
        return Optional.of(recipientsOrder.get(currentIndex));
    }

    public synchronized int getParticipantsCount() {
        return recipientsOrder.size(); // participants = recipients in Secret Santa
    }

    public synchronized boolean recordVote(String recipientId, String voterId, String guessedGiverId) {
        Map<String, String> r = votes.computeIfAbsent(recipientId, k -> new ConcurrentHashMap<>());
        if (r.containsKey(voterId)) return false; // already voted => final
        r.put(voterId, guessedGiverId);
        save();
        return true;
    }

    public synchronized boolean hasEveryoneVotedFor(String recipientId) {
        Map<String, String> r = votes.getOrDefault(recipientId, Collections.emptyMap());
        return r.size() >= getParticipantsCount();
    }

    /** Tally points for current recipient, advance index, persist. Returns actual giver id or empty if none. */
    public synchronized Optional<String> revealCurrentAndAdvance(Map<String, String> assignments, AssignmentService assignmentService) {
        Optional<String> cur = getCurrentRecipient();
        if (cur.isEmpty()) return Optional.empty();
        String recipient = cur.get();
        String actualGiver = assignmentService.getGiverFor(recipient);
        Map<String, String> r = votes.getOrDefault(recipient, Collections.emptyMap());
        for (Map.Entry<String, String> e : r.entrySet()) {
            String voter = e.getKey();
            String guess = e.getValue();
            if (voter.equals(actualGiver)) continue; // giver's own vote gets recorded but receives no points
            if (guess != null && guess.equals(actualGiver)) {
                points.put(voter, points.getOrDefault(voter, 0) + 1);
            } else {
            	points.put(voter, points.getOrDefault(voter, 0));
            }
        }
        currentIndex++;
        if (currentIndex >= recipientsOrder.size()) finished = true;
        save();
        return Optional.ofNullable(actualGiver);
    }

    public synchronized Map<String,Integer> getPointsSnapshot() {
        return new HashMap<>(points);
    }

    public synchronized boolean isFinished() { return finished; }

    private synchronized void save() {
        try {
            if (file.getParent() != null) Files.createDirectories(file.getParent());
            JsonObject root = new JsonObject();
            root.add("recipientsOrder", gson.toJsonTree(recipientsOrder));
            root.addProperty("currentIndex", currentIndex);
            root.add("votes", gson.toJsonTree(votes));
            root.add("points", gson.toJsonTree(points));
            root.addProperty("finished", finished);
            try (Writer w = Files.newBufferedWriter(file)) { gson.toJson(root, w); }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private synchronized void load() {
        try {
            if (!Files.exists(file)) return;
            String json = new String(Files.readAllBytes(file));
            if (json.isBlank()) return;
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            recipientsOrder = gson.fromJson(root.get("recipientsOrder"), List.class);
            currentIndex = root.has("currentIndex") ? root.get("currentIndex").getAsInt() : 0;
            Type votesType = new com.google.gson.reflect.TypeToken<Map<String, Map<String, String>>>() {}.getType();
            Map<String, Map<String, String>> loadedVotes = gson.fromJson(root.get("votes"), votesType);
            votes.clear();
            if (loadedVotes != null) votes.putAll(loadedVotes);
            Type pointsType = new com.google.gson.reflect.TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> loadedPoints = gson.fromJson(root.get("points"), pointsType);
            points.clear();
            if (loadedPoints != null) points.putAll(loadedPoints);
            finished = root.has("finished") && root.get("finished").getAsBoolean();
        } catch (IOException | JsonParseException ex) { ex.printStackTrace(); }
    }
}
