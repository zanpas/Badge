package it.sintegra.badge.service.entity;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class UserEssential {

	protected Short id;
	protected String username, profile;

	public UserEssential() {
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

}
