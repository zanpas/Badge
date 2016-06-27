package it.sintegra.badge.service.entity;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class SearchBadgeEventResponse extends Response implements
		Serializable {

	private static final long serialVersionUID = 3173121877460356003L;

	// @XmlElement(name = "userId")
	private Short userId;
	// private Map<String, Object> params;
	// @XmlElement(name = "events")
	private List<Event> events;

	// public Map<String, Object> getParams() {
	// return params;
	// }
	//
	// public void setParams(Map<String, Object> params) {
	// this.params = params;
	// }

	public List<Event> getEvents() {
		return events;
	}

	public Short getUserId() {
		return userId;
	}

	public void setUserId(Short userId) {
		this.userId = userId;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

}
