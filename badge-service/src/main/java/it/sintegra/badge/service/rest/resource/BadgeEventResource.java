package it.sintegra.badge.service.rest.resource;

import it.sintegra.badge.business.dao.mybatis.mapper.CalendarEventMapper;
import it.sintegra.badge.business.entity.CalendarEvent;
import it.sintegra.badge.business.entity.CalendarEventExample;
import it.sintegra.badge.business.entity.CalendarEventExample.Criteria;
import it.sintegra.badge.business.mybatis.MyBatisConnectionFactory;
import it.sintegra.badge.service.entity.Event;
import it.sintegra.badge.service.entity.SearchBadgeEventResponse;
import it.sintegra.badge.service.helper.CalendarEventHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;


@Path("/calendarevents")
public class BadgeEventResource {

	private static final Logger logger = Logger
			.getLogger(BadgeEventResource.class);

	// @Context
	// UriInfo uriInfo;
	// @Context
	// Request request;

	@GET
	@Path("/info")
	public String getServiceDescr() {
		return "Risorsa REST per la gestione degli eventi Badge (Ferie, Permessi, Malattia ...)";
	}

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response findByQParams(@QueryParam("userId") Short userId,
			@QueryParam("category") Byte category,
			@QueryParam("categoryList") List<Byte> categoryList,
			@QueryParam("override") Boolean override,
			@QueryParam("start") Long start, @QueryParam("end") Long end,
			@QueryParam("offset") Integer offset,
			@QueryParam("size") Integer size) {
		final String methodName = "findByQParams";
		logger.debug(methodName + ": INIZIO");
		logger.info(methodName + ": Ricerca per userId:" + userId
				+ ", category: " + category + ", categoryList: " + categoryList
				+ ", override: " + override + ", start: " + start + ", end: "
				+ end);

		// Map<String, Object> params = new HashMap<String, Object>();
		// params.put("userId", userId);
		// params.put("category", category);
		// params.put("categoryList", categoryList);
		// params.put("override", override);
		// params.put("start", start);
		// params.put("end", end);
		// params.put("offset", offset);
		// params.put("size", size);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
		Date startDate = null;
		if (start != null) {
			startDate = new Date(start);
			logger.info(methodName + ": startDate=" + sdf.format(startDate));
		}
		Date endDate = null;
		if (end != null) {
			endDate = new Date(end);
			logger.info(methodName + ": endDate=" + sdf.format(endDate));
		}

		Response.Status status = Response.Status.OK;
		SearchBadgeEventResponse response = new SearchBadgeEventResponse();
		// response.setParams(params);
		if (userId != null && userId > 0) {
			response.setUserId(userId);
		}

		SqlSession sqlSession = null;
		try {
			sqlSession = MyBatisConnectionFactory.getSqlSessionFactory()
					.openSession(true);
			CalendarEventMapper calendarEventMapper = sqlSession
					.getMapper(CalendarEventMapper.class);
			CalendarEventExample example = new CalendarEventExample();
			example.setOrderByClause("START ASC");
			Criteria criteria = example.createCriteria();
			if (userId != null) {
				criteria = criteria.andUseridEqualTo(userId);
			}
			if (category != null) {
				criteria = criteria.andCategoryEqualTo(category);
			} else if (categoryList != null && categoryList.size() > 0) {
				criteria = criteria.andCategoryIn(categoryList);
			}

			if (override != null && override) {
				if (start == null) {
					status = Response.Status.BAD_REQUEST;
					response.setIsError(true);
					response.setMessage("Parametro start obbligatorio se si indica override=true");
				} else if (end == null) {
					status = Response.Status.BAD_REQUEST;
					response.setIsError(true);
					response.setMessage("Parametro end obbligatorio se si indica override=true");
				} else {
					// $scope.newEvent.start < $rootScope.holidays[i].end &&
					// $rootScope.holidays[i].start < $scope.newEvent.end
					criteria = criteria.andEndGreaterThan(startDate)
							.andStartLessThan(endDate);
				}
			} else {
				if (start != null) {
					criteria = criteria.andStartGreaterThanOrEqualTo(startDate);
				}
				if (end != null) {
					criteria = criteria.andEndLessThan(endDate);
				}
			}

			List<CalendarEvent> calendarEvents = calendarEventMapper
					.selectByExample(example);
			if (calendarEvents != null && calendarEvents.size() > 0) {
				List<Event> events = new ArrayList<Event>(calendarEvents.size());
				for (CalendarEvent currEvent : calendarEvents) {
					events.add(CalendarEventHelper.convertToEvent(currEvent));
				}

				response.setEvents(events);
				response.setNumResults(events.size());
				response.setMessage("Risultati trovati: "
						+ response.getNumResults());
				logger.info(methodName + ": Trovati " + events.size()
						+ " eventi");
			} else {
				response.setNumResults(0);
				response.setMessage("Nessun evento trovato");
				logger.info(methodName + ": Nessun evento trovato");
			}

		} catch (Throwable th) {
			logger.error("Errore durante la ricerca eventi", th);
			response.setIsError(true);
			response.setMessage(th.getMessage());
			status = Response.Status.INTERNAL_SERVER_ERROR;
		} finally {
			sqlSession.close();
			logger.debug(methodName + ": FINE");
		}
		return Response.status(status).entity(response).build();
	}

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response findById(@PathParam("id") Integer id) {
		final String methodName = "findById";
		logger.debug(methodName + ": INIZIO");
		SqlSession sqlSession = null;
		try {
			sqlSession = MyBatisConnectionFactory.getSqlSessionFactory()
					.openSession(true);
			CalendarEventMapper calendarEventMapper = sqlSession
					.getMapper(CalendarEventMapper.class);
			CalendarEvent event = calendarEventMapper.selectByPrimaryKey(id);
			if (event != null) {
				return Response.status(Response.Status.OK)
						.entity(CalendarEventHelper.convertToEvent(event))
						.build();
			}
		} catch (Throwable th) {
			logger.error("Errore durante la ricerca", th);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.build();
		} finally {
			sqlSession.close();
			logger.debug(methodName + ": FINE");
		}
		return Response.status(Response.Status.NOT_FOUND)
				.entity("Event con id:" + id + " non trovato").build();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.TEXT_HTML })
	public Response create(Event newEvent) {
		final String methodName = "create";
		logger.debug(methodName + ": INIZIO");
		SqlSession sqlSession = null;
		try {
			sqlSession = MyBatisConnectionFactory.getSqlSessionFactory()
					.openSession(true);
			CalendarEventMapper calendarEventMapper = sqlSession
					.getMapper(CalendarEventMapper.class);

			CalendarEvent calendarEvent = CalendarEventHelper
					.convertToCalendarEvent(newEvent);

			if (calendarEventMapper.insert(calendarEvent) < 1) {
				logger.warn("Evento non creato");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.build();
			}
			sqlSession.commit();
		} catch (Throwable th) {
			logger.error("Errore durante la creazione", th);
			sqlSession.rollback();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.build();
		} finally {
			sqlSession.close();
			logger.debug(methodName + ": FINE");
		}
		return Response.status(Response.Status.CREATED).entity("Evento creato")
				.build();
	}

	@DELETE
	@Path("{id}")
	@Produces({ MediaType.TEXT_HTML })
	public Response deleteById(@PathParam("id") Integer id) {
		final String methodName = "deleteById";
		logger.debug(methodName + ": INIZIO");
		SqlSession sqlSession = null;
		try {
			sqlSession = MyBatisConnectionFactory.getSqlSessionFactory()
					.openSession(true);
			CalendarEventMapper calendarEventMapper = sqlSession
					.getMapper(CalendarEventMapper.class);

			if (calendarEventMapper.deleteByPrimaryKey(id) != 1) {
				logger.warn(methodName + ": Cancellazione non effettuata");
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Evento con id: " + id + " non trovato")
						.build();
			}
			sqlSession.commit();
		} catch (Throwable th) {
			logger.error("Errore durante la cancellazione", th);
			sqlSession.rollback();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.build();
		} finally {
			sqlSession.close();
			logger.debug(methodName + ": FINE");
		}
		return Response.status(Response.Status.OK)
				.entity("Evento con id: " + id + " eliminato").build();
	}
}
