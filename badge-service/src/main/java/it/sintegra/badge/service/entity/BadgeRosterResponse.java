package it.sintegra.badge.service.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class BadgeRosterResponse extends Response implements Serializable {

	private static final long serialVersionUID = 3173121877460356003L;

	private Map<String, Object> params;
	private List<Event> events;

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

}
