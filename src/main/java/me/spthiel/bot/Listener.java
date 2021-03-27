package me.spthiel.bot;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.entity.channel.TextChannel;

import java.sql.SQLException;

import me.spthiel.database.DBConnection;
import me.spthiel.database.DBConnector;

public abstract class Listener {
    
    private final boolean acceptsBots;
    private final boolean acceptsDMs;
    private final long[]  channels;
    
    public Listener(boolean acceptsBots) {
        this(acceptsBots, true, (long[]) null);
    }
    
    public Listener(boolean acceptsBots, boolean acceptsDms) {
        this(acceptsBots, acceptsDms, (long[]) null);
    }
    
    public Listener(boolean acceptsBots, long... channels) {
        this(acceptsBots, true, channels);
    }
    
    public Listener(boolean acceptsBots, boolean acceptsDms, long... channels) {
        this.acceptsBots = acceptsBots;
        this.acceptsDMs = acceptsDms;
        this.channels = channels;
        if (this instanceof DBConnector) {
            try {
                ((DBConnector) this).setupTables();
            } catch (SQLException ignored) {}
            ((DBConnector)this).setup();
        }
    }
    
    public boolean acceptsGuild(Snowflake guild) {
        return guild.asLong() == 300295653907628032L;
    }
    
    public boolean acceptsBots() {
        return acceptsBots;
    }
    
    public boolean acceptsDMs() {
        return acceptsDMs;
    }
    
    public boolean acceptsChannel(Channel channel) {
    
        if(channels == null || channels.length == 0) {
            return true;
        }
        
        for (long l : channels) {
            if(l == channel.getId().asLong()) {
                return true;
            }
        }
        return false;
    }
    
    public final void onMessageCreate(MessageCreateEvent event) {
    
        Message message = event.getMessage();
        User author = message.getAuthor().orElse(null);
        if((author == null || author.isBot()) && !acceptsBots()) {
            return;
        }
        
        message.getChannel()
               .subscribe(channel -> {
                   if(channel instanceof PrivateChannel) {
                       if(acceptsDMs) {
                           onDMMessage((PrivateChannel) channel, author, message, message.getContent());
                       }
                   } else {
                       if(acceptsChannel(channel)) {
                           event.getGuild().subscribe(guild -> {
                               
                               Member member = event.getMember().orElse(null);
                               onSeverMessage(guild, (TextChannel)channel, member, message, message.getContent());
                           });
                       }
                   }
               });
    }
    
    abstract public void onSeverMessage(final Guild guild, final TextChannel channel, final Member author, final Message message, final String content);
    abstract public void onDMMessage(final PrivateChannel channel, final User author, final Message message, final String content);
    
    public void onStart() {
    
    }
    
    public void onEnd() {
    
    }
    
}
