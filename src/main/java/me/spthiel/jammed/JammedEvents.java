package me.spthiel.jammed;

import java.util.Optional;

import me.spthiel.bot.Listener;
import me.spthiel.jammed.states.SuggestionsListener;

public enum JammedEvents {
    
    TOTAL(JammedTimeHandler.of(JammedTimeUnits.WEEKS, 0)),
    THEMESUGGESTIONS(JammedTimeHandler.of(JammedTimeUnits.WEEKS, 2), "Theme suggestion ends in %s", "Theme suggestions", new SuggestionsListener()),
    THEMESLAUGHTER(JammedTimeHandler.of(JammedTimeUnits.WEEKS, 2), "Theme slaughter ends in %s", "Theme Slaughter"),
    THEMEVOTING(JammedTimeHandler.of(JammedTimeUnits.DAYS, 5), "Theme voting ends in %s", "Theme Voting"),
    CODING(JammedTimeHandler.of(JammedTimeUnits.DAYS, 2), "The coding phase ends in %s", "Coding"),
    SUBMISSION(JammedTimeHandler.of(JammedTimeUnits.HOURS,1), "Voting will begin in %s", "Submission"),
    VOTING(JammedTimeHandler.of(JammedTimeUnits.WEEKS, 2).substract(JammedTimeUnits.HOURS, 1), "Voting will end in %s", "Voting"),
    REST(JammedTimeHandler.of(JammedTimeUnits.WEEKS, 4), "The next jam will begin in %s", "Next Jam")
    ;
    
    private static int total = -1;
    
    private final int      hours;
    private final Listener listener;
    private final String   format;
    private       int    ordinal = -1;
    private final String name;
    
    JammedEvents(int hours, String format, String name, Listener listener) {
        this.hours = hours;
        this.listener = listener;
        this.format = format;
        this.name = name;
    }
    
    JammedEvents(JammedTimeHandler time) {
        this(time.get(), null, null, null);
    }
    
    JammedEvents(JammedTimeHandler time, String format, String name) {
        this(time.get(), format, name, null);
    }
    
    JammedEvents(JammedTimeHandler time, String format, String name, Listener listener) {
        this(time.get(), format, name, listener);
    }
    
    public int getHours() {
        
        if (this.equals(JammedEvents.TOTAL)) {
            if (total == -1) {
                total = 0;
                for (JammedEvents event : values()) {
                    total += event.hours;
                }
            }
            return total;
        }
        
        return hours;
    }
    
    public boolean listening() {
        return listener != null;
    }
    
    public Optional<Listener> getListener() {
    
        return Optional.ofNullable(listener);
    }
    
    public String getFormat() {
        
        return format;
    }
    
    public int getOrdinal() {
        if(ordinal < 0) {
            JammedEvents[] values = values();
            for (int i = 0, valuesLength = values.length ; i < valuesLength ; i++) {
                JammedEvents value = values[i];
                if(value.equals(this)) {
                    ordinal = i;
                    break;
                }
            }
        }
        return ordinal;
    }
    
    public String getName() {
        
        return name == null ? name() : name;
    }
}
