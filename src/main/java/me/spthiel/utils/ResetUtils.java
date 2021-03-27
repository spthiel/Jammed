package me.spthiel.utils;

import me.spthiel.database.repository.SuggestionRepository;

public class ResetUtils {

	public static void reset() {
		
		SuggestionRepository.reset();
		
	}
	
}
