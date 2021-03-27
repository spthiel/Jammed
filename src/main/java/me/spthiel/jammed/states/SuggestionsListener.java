package me.spthiel.jammed.states;

import discord4j.common.util.Snowflake;
import discord4j.core.object.ExtendedPermissionOverwrite;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

import me.spthiel.bot.Bot;
import me.spthiel.bot.Listener;
import me.spthiel.database.repository.SuggestionRepository;

import static me.spthiel.bot.Bot.PREFIX;

public class SuggestionsListener extends Listener {
	
	private final static Snowflake suggestionChannel   = Snowflake.of(709374487803592783L);
	private final static int       maxSuggestionLength = 20;
	
	public SuggestionsListener() {
		
		super(false);
	}
	
	@Override
	public void onSeverMessage(Guild guild, TextChannel channel, Member author, Message message, String content) {
		
		if (content.startsWith(PREFIX)) {
			if (content.substring(PREFIX.length())
					   .equalsIgnoreCase("suggestions")) {
				ArrayList<String> suggestions = SuggestionRepository.getSuggestions(author.getId());
				channel.createMessage(author.getDisplayName() + "'s Suggestions: " + (suggestions.size() > 0 ? "\n- " : "None") + String.join("\n- ", suggestions))
					   .delayElement(Duration.ofSeconds(10))
					   .flatMap(botMessage -> botMessage.delete("Automatic timed deletion"))
					   .subscribe();
				message.delete("Automatic deletion to keep chat clean");
			}
			return;
		}
		
		if (channel.getId()
				   .equals(suggestionChannel)) {
			if (content.length() > maxSuggestionLength) {
				channel.createMessage("Your message is too long for a suggestion. Please use at most " + maxSuggestionLength + " characters")
					   .delayElement(Duration.ofSeconds(5))
					   .flatMap(botMessage -> botMessage.delete("Automatic timed deletion"))
					   .subscribe();
			} else {
				SuggestionRepository.storeSuggestion(author.getId(), content);
			}
			message.delete("Removed for being a suggestion")
				   .subscribe();
		}
		
	}
	
	@Override
	public void onDMMessage(PrivateChannel channel, User author, Message message, String content) {
	
	
	}
	
	@Override
	public void onStart() {
		
		updatePermission(true);
		
	}
	
	@Override
	public void onEnd() {
		
		updatePermission(false);
	}
	
	private void updatePermission(boolean add) {
		
		Bot.getInstance()
		   .getChannel(suggestionChannel)
		   .filter(channel -> channel instanceof TextChannel)
		   .map(channel -> (TextChannel) channel)
		   .flatMap(channel -> {
			   Optional<ExtendedPermissionOverwrite> overwriteOptional = channel.getOverwriteForRole(channel.getGuildId());
			   PermissionSet allowed = overwriteOptional.map(PermissionOverwrite :: getAllowed)
														.orElse(PermissionSet.none());
			   PermissionSet denied = overwriteOptional.map(PermissionOverwrite :: getDenied)
													   .orElse(PermissionSet.none());
			   if (add) {
				   allowed = allowed.or(PermissionSet.of(Permission.SEND_MESSAGES));
			   } else {
				   denied = denied.or(PermissionSet.of(Permission.SEND_MESSAGES));
			   }
			
			   return channel.addRoleOverwrite(
				   channel.getGuildId(),
				   PermissionOverwrite.forRole(channel.getGuildId(), allowed, denied)
			   );
		   })
		   .subscribe();
	}
}
