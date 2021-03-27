package me.spthiel.jammed;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import me.spthiel.bot.Listener;
import me.spthiel.utils.ResetUtils;
import me.spthiel.utils.TimeUtils;

public class Calendar implements Iterable<Calendar.CalendarNode> {
	
	private static final long     msPerHour = 60 * 60 * 1000;
	private static       Calendar instance;
	public static Calendar getInstance() {
		if (instance == null) {
			return generate();
		}
		return instance;
	}
	
	public static Calendar generate() {
//		return new Calendar(
//			ZonedDateTime.of(2020, 6, 15, 0, 0, 0, 0, ZoneId.of("UTC"))
//						 .toInstant()
//						 .toEpochMilli()
//		);
		return new Calendar(System.currentTimeMillis());
	}
	
	private CalendarNode head;
	private CalendarNode tail;
	
	public Calendar(long timestamp) {
		instance = this;
		long diff = System.currentTimeMillis() - timestamp;
		long totalTime = JammedEvents.TOTAL.getHours() * msPerHour;
		if (diff > totalTime) {
			long count = diff/totalTime;
			timestamp += totalTime * count;
		}
		CalendarNode current  = null;
		CalendarNode previous = null;
		for (JammedEvents event : JammedEvents.values()) {
			if (event.equals(JammedEvents.TOTAL)) {
				continue;
			}
			current = new CalendarNode(event, timestamp + event.getHours() * msPerHour);
			current.previous = previous;
			if (previous != null) {
				previous.next = current;
			}
			if (head == null) {
				head = current;
			}
			
			tail = current;
			previous = current;
			timestamp += event.getHours() * msPerHour;
		}
		tail.next = head;
		if (!update()) {
			head.getEvent().getListener().ifPresent(Listener::onStart);
		}
		Flux.interval(Duration.ofMinutes(5))
			.subscribe(ignored -> this.update());
	}
	
	public boolean update() {
		boolean ret = false;
		while (this.head.next.timestamp < System.currentTimeMillis()) {
			advance();
			ret = true;
		}
		return ret;
	}
	
	public CalendarNode getNext() {
		
		return head;
	}
	
	public void advance() {
		this.head = this.head.next;
		this.tail = this.tail.next;
		this.tail.advance();
		if (this.tail.getEvent().equals(JammedEvents.THEMESUGGESTIONS)) {
			ResetUtils.reset();
		}
		this.head.getEvent().getListener().ifPresent(Listener :: onStart);
		this.tail.getEvent().getListener().ifPresent(Listener :: onEnd);
	}
	
	public void forceNext() {
		long difference = this.head.next.timestamp - System.currentTimeMillis() - 5000;
		this.forEach(node -> node.timestamp -= difference);
		Mono.delay(Duration.ofSeconds(7)).subscribe(ignore -> this.update());
	}
	
	@Override
	public Iterator<CalendarNode> iterator() {
		
		return new CalendarIterator(this.head);
	}
	
	@Override
	public void forEach(Consumer<? super CalendarNode> action) {
		CalendarNode current = this.head;
		do {
			action.accept(current);
			current = current.next;
		} while (current != this.head);
	}
	
	@Override
	public Spliterator<CalendarNode> spliterator() {
		
		return null;
	}
	
	public long secondsUntilNext() {
		long currentSeconds = System.currentTimeMillis()/1000;
		long nextSeconds = this.head.next.timestamp;
		return nextSeconds-currentSeconds;
	}
	
	@Override
	public String toString() {
		
		Iterator<CalendarNode> it = this.iterator();
		
		StringBuilder out = new StringBuilder();
		
		CalendarNode last = null;
		for (CalendarNode node : this) {
			if (last == null) {
				out.append("> ");
			} else {
				out.append("- ");
			}
			JammedEvents event = node.getEvent();
			long timestamp = node.getTimestamp();
			out.append(event.name()).append(" - ").append(TimeUtils.toDate(timestamp)).append("\n");
			
			last = node;
		}
		
		return out.toString();
	}
	
	public static class CalendarNode {
		
		private       long         timestamp;
		private final JammedEvents event;
		
		private CalendarNode previous;
		private CalendarNode next;
		
		public CalendarNode(JammedEvents event, long timestamp) {
			this.timestamp = timestamp;
			this.event = event;
		}
		
		public CalendarNode getNext() {
			
			return next;
		}
		
		public void advance() {
			
			this.timestamp += JammedEvents.TOTAL.getHours() * msPerHour;
		}
		
		public long getTimestamp() {
			
			return timestamp;
		}
		
		public JammedEvents getEvent() {
			
			return event;
		}
		
		public long secondsUntil() {
			return (this.timestamp - System.currentTimeMillis())/1000;
		}
		
		@Override
		public String toString() {
			
			return event.toString();
		}
	}
	
	protected static class CalendarIterator implements Iterator<CalendarNode> {
		
		private final CalendarNode head;
		private       CalendarNode current;
		
		public CalendarIterator(CalendarNode head) {
			this.head = this.current = head;
		}
		
		@Override
		public boolean hasNext() {
			
			return !current.next.equals(this.head);
		}
		
		@Override
		public CalendarNode next() {
			CalendarNode out;
			out = current;
			current = current.next;
			return out;
		}
	}
}
