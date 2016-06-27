package it.sintegra.badge.service.helper;

import it.sintegra.badge.business.entity.CalendarEvent;
import it.sintegra.badge.service.entity.Event;

public class CalendarEventHelper {

	public static Event convertToEvent(CalendarEvent calendarEvent) {
		if (calendarEvent == null) {
			throw new IllegalArgumentException("Dati di input non specificati");
		}
		Event event = new Event();
		event.setCategory(calendarEvent.getCategory());
		event.setDescription(calendarEvent.getDescription());
		event.setEnd(calendarEvent.getEnd());
		event.setStart(calendarEvent.getStart());
		event.setTitle(calendarEvent.getTitle());
		event.setUserid(calendarEvent.getUserid());
		event.setId(calendarEvent.getId());
		return event;
	}

	public static CalendarEvent convertToCalendarEvent(Event event) {
		if (event == null) {
			throw new IllegalArgumentException("Dati di input non specificati");
		}
		CalendarEvent calendarEvent = new CalendarEvent();
		calendarEvent.setCategory(event.getCategory());
		calendarEvent.setEnd(event.getEnd());
		calendarEvent.setStart(event.getStart());
		calendarEvent.setTitle(event.getTitle());
		calendarEvent.setUserid(event.getUserid());
		calendarEvent.setDescription(event.getDescription());
		return calendarEvent;
	}
}
