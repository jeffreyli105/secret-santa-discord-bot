package com.jeffrey;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePaths {
	public static Path REGISTRATIONS_FILE = Paths.get("data", "registrations.json");
	public static Path ASSIGNMENTS_FILE = Paths.get("data", "assignments.json");
	public static Path VOTES_FILE = Paths.get("data", "votes.json");
}
