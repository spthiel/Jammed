package me.spthiel.bot;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.Channel;
import reactor.core.publisher.Mono;

public class Bot {
	
	private static Bot instance;
	
	public static Bot getInstance() {
		
		return instance;
	}
	
	private final       GatewayDiscordClient client;
	public static final String               PREFIX = ".";
	
	public Bot(String token) {
		instance = this;
		
		client = DiscordClient.create(token).login().block();
		Listener listener = new GeneralListener();
		
		client.on(GuildCreateEvent.class).subscribe(event -> {
			System.out.println("Joining guild " + event.getGuild().getName());
		});
		client.on(MessageCreateEvent.class).subscribe(listener:: onMessageCreate);
		client.on(MessageCreateEvent.class).subscribe(new DiscordListener():: onMessageCreate);
		
		client.onDisconnect().block();
	}
	
	public Mono<Channel> getChannel(long channel) {
		return getChannel(Snowflake.of(channel));
	}
	
	public Mono<Channel> getChannel(Snowflake channel) {
		return client.getChannelById(channel);
	}
	
}
