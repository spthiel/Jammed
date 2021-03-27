package me.spthiel.jammed;

public class JammedTimeHandler {
    
    public static JammedTimeHandler of(JammedTimeUnits unit) {
        return new JammedTimeHandler(unit);
    }
    
    public static JammedTimeHandler of(JammedTimeUnits unit, int count) {
        return new JammedTimeHandler(unit, count);
    }
    
    private int hours;
    private boolean add;
    private JammedTimeUnits currentUnit;
    
    public JammedTimeHandler(JammedTimeUnits unit) {
        currentUnit = unit;
        add = true;
    }
    
    public JammedTimeHandler(JammedTimeUnits unit, int count) {
        this(unit);
        take(count);
    }
    
    public JammedTimeHandler take(int count) {
        if(add) {
            hours += count*currentUnit.getHours();
        } else {
            hours -= count*currentUnit.getHours();
        }
        return this;
    }
    
    public JammedTimeHandler add() {
        add = true;
        return this;
    }
    
    public JammedTimeHandler add(JammedTimeUnits unit) {
        add();
        currentUnit = unit;
        return this;
    }
    
    public JammedTimeHandler add(JammedTimeUnits unit, int count) {
        add(unit);
        take(count);
        return this;
    }
    
    public JammedTimeHandler substract() {
        add = false;
        return this;
    }
    
    public JammedTimeHandler substract(JammedTimeUnits unit) {
        substract();
        currentUnit = unit;
        return this;
    }
    
    public JammedTimeHandler substract(JammedTimeUnits unit, int count) {
        substract(unit);
        take(count);
        return this;
    }
    
    public int get() {
        return hours;
    }
    
}
