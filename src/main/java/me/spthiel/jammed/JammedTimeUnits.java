package me.spthiel.jammed;

public enum JammedTimeUnits {

    HOURS(1),
    DAYS(24),
    WEEKS(24*7);
    
    private int hours;
    
    JammedTimeUnits(int hours) {
        this.hours = hours;
    }
    
    public int getHours() {
        
        return hours;
    }
}
