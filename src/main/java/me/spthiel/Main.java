package me.spthiel;

import me.spthiel.bot.Bot;
import me.spthiel.jammed.Calendar;

public class Main {
	
	public static void main(String[] args) {
		
		Bot bot = new Bot(args[0]);
		System.out.println(Calendar.getInstance());
		
	}
}
