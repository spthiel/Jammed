package me.spthiel.database.repository;

import discord4j.common.util.Snowflake;

import java.util.HashMap;

public class SlaughterRepository {
	
	private HashMap<Snowflake, HashMap<VoteType, Integer>> registeredVotes;
	
	private enum VoteType {
		UPVOTE,
		DOWNVOTE,
		NONE
	}
	
}
