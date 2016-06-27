package it.sintegra.badge.service.rest;

import it.sintegra.badge.service.rest.resource.BadgeEventResource;
import it.sintegra.badge.service.rest.resource.BadgeSheetResource;
import it.sintegra.badge.service.rest.resource.BadgeUserResource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

//@ApplicationPath("/rest")
public class ServiceApplication extends Application {

	public Set<Class<?>> getClasses() {
		Set<Class<?>> s = new HashSet<Class<?>>();
		s.add(BadgeEventResource.class);
		s.add(BadgeUserResource.class);
		s.add(BadgeSheetResource.class);
		return s;
	}

}
