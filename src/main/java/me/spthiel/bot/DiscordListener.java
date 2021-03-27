package me.spthiel.bot;

import discord4j.core.event.domain.message.MessageCreateEvent;

import me.spthiel.jammed.Calendar;

public class DiscordListener {
	
	public void onMessageCreate(MessageCreateEvent event) {
		Calendar.getInstance()
				.getNext()
				.getEvent()
				.getListener()
				.ifPresent(jevent -> {
					jevent.onMessageCreate(event);
				});
	}
	
}
