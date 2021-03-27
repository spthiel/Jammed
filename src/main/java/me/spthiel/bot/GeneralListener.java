package me.spthiel.bot;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.entity.channel.TextChannel;

import java.util.Arrays;

import me.spthiel.jammed.Calendar;
import me.spthiel.jammed.JammedEvents;

import static me.spthiel.utils.TimeUtils.*;
import static me.spthiel.bot.Bot.PREFIX;

public class GeneralListener extends Listener {
	
	public GeneralListener() {
		
		super(false, false, 708691757230981172L, 366238019428155412L, 366277269561540618L, 366241386690904064L);
	}
	
	@Override
	public void onSeverMessage(final Guild guild, final TextChannel channel, final Member author, final Message message, final String content) {
		
		
		if (!content.startsWith(PREFIX)) {
			return;
		}
		String   command = content.substring(PREFIX.length());
		String[] args    = command.split(" ");
		switch (args[0]) {
			case "timeleft":
				onTimeleft(channel);
				break;
			case "advance":
				if (author.getId().asLong() != 261538420952662016L) {
					break;
				}
				Calendar.getInstance().forceNext();
				channel.createMessage("Next event will start in 5 seconds").subscribe();
				System.out.println("Skipping to " + Calendar.getInstance().getNext() + " event");
				break;
		}
	}
	
	@Override
	public void onDMMessage(final PrivateChannel channel, final User author, final Message message, final String content) {
	
	}
	
	private void onTimeleft(TextChannel channel) {
		
		Calendar.getInstance().update();
		
		final String format = "%1$s%2$-" + Arrays.stream(JammedEvents.values())
												 .mapToInt(event -> event.getName()
																		 .length())
												 .max()
												 .orElse(1) + "s %3$13s%1$s%n";
		
		channel.createEmbed(embed -> {
			
			Calendar.CalendarNode node = Calendar.getInstance()
												 .getNext();
			
			String timeleft = toDateTime(node.getNext()
											 .secondsUntil());
			embed.addField("Next Phase:", String.format(node.getEvent()
															.getFormat(), timeleft), false);
			
			StringBuilder eventsField = new StringBuilder();
			eventsField.append("```inform7\n");
			
			boolean highlight = true;
			for (Calendar.CalendarNode calendarNode : Calendar.getInstance()) {
				
				eventsField.append(String.format(format, highlight ? '"' : ' ', calendarNode.getEvent()
																							.getName(), toDateTime(calendarNode.getNext()
																															   .secondsUntil())));
				highlight = false;
			}
			
			eventsField.append("```");
			embed.addField("Events", eventsField.toString(), true);
		})
			   .subscribe();
	}
}
