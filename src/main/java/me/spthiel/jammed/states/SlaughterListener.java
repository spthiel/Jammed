package me.spthiel.jammed.states;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.entity.channel.TextChannel;

import me.spthiel.bot.Listener;

public class SlaughterListener extends Listener {
	
	public SlaughterListener() {
		super(false, true);
	}
	
	@Override
	public void onSeverMessage(Guild guild, TextChannel channel, Member author, Message message, String content) {
	
	}
	
	@Override
	public void onDMMessage(PrivateChannel channel, User author, Message message, String content) {
	
	}
}
