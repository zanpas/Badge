package it.sintegra.badge.service.entity;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class User extends UserEssential {

	protected String firstname, lastname;

	public User() {
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

}
