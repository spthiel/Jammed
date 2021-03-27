package me.spthiel.database.repository;

import discord4j.common.util.Snowflake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class SuggestionRepository {

	private static SuggestionRepository instance = new SuggestionRepository();

	private HashMap<Snowflake, ArrayList<String>> suggestions;
	
	public SuggestionRepository() {
		suggestions = new HashMap<>();
	}
	
	private void prepareFor(Snowflake userid) {
		suggestions.putIfAbsent(userid, new ArrayList<>());
	}
	
	public static void storeSuggestion(Snowflake userid, String suggestion) {
		instance.prepareFor(userid);
		instance.suggestions.get(userid).add(suggestion);
	}
	
	public static ArrayList<String> getSuggestions(Snowflake userid) {
		
		return Optional.ofNullable(instance.suggestions.get(userid)).orElse(new ArrayList<>());
	}
	
	public static void reset() {
		instance.suggestions = new HashMap<>();
	}
	
}
