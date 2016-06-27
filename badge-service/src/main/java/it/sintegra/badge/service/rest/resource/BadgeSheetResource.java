package it.sintegra.badge.service.rest.resource;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import it.sintegra.badge.business.dao.mybatis.mapper.CalendarEventMapper;
import it.sintegra.badge.business.dao.mybatis.mapper.UserMapper;
import it.sintegra.badge.business.entity.CalendarEvent;
import it.sintegra.badge.business.entity.CalendarEventExample;
import it.sintegra.badge.business.entity.CalendarEventExample.Criteria;
import it.sintegra.badge.business.entity.User;
import it.sintegra.badge.business.entity.UserExample;
import it.sintegra.badge.business.mybatis.MyBatisConnectionFactory;


@Path("/calendarsheets")
public class BadgeSheetResource {

	private static final Logger logger = Logger.getLogger(BadgeSheetResource.class);

	@GET
	@Path("/info")
	public String getServiceDescr() {
		return "Risorsa REST per la gestione delle statistiche Badge";
	}

	@GET
	@Path("/downloadRosterXLS")
	// @Produces("application/vnd.ms-excel")
	@Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public Response downloadRoster(@QueryParam("start") Long start, @QueryParam("end") Long end) {
		final String methodName = "downloadRoster";
		logger.debug(methodName + ": INIZIO");

		// final String tempdir = System.getProperty("java.io.tmpdir");
		// "/usr/share/BADGE_HOME/temp/";
		// logger.debug(methodName + ": tempdir='" + tempdir + "'");

		// FTPClient ftp = new FTPClient();
		// try {
		// ftp.connect("192.168.10.12");
		// if (!ftp.login("root", "next_centosjb.2011")) {
		// ftp.logout();
		// // return false;
		// }
		// int reply = ftp.getReplyCode();
		// if (!FTPReply.isPositiveCompletion(reply)) {
		// ftp.disconnect();
		// // return false;
		// }
		//
		// // enter passive mode
		// ftp.enterLocalPassiveMode();
		// // get system name
		// logger.debug(methodName + ": ftp.getSystemType="
		// + ftp.getSystemType());
		// // change current directory
		// ftp.changeWorkingDirectory(tempdir);
		// logger.debug(methodName + ": Current directory is "
		// + ftp.printWorkingDirectory());
		// } catch (Throwable th) {
		// logger.warn(methodName + ": Errore di connessione al server ftp",
		// th);
		// return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
		// .entity("Errore di connessione al server").build();
		// }
		// logger.info(methodName + ": Stabilita connessione ftp");

		Response.Status status = Response.Status.OK;
		// SearchBadgeEventResponse response = new
		// SearchBadgeEventResponse();

		// if (start == null) {
		// status = Response.Status.BAD_REQUEST;
		// // response.setIsError(true);
		// // response.setMessage("Parametro start obbligatorio");
		// } else if (end == null) {
		// status = Response.Status.BAD_REQUEST;
		// // response.setIsError(true);
		// // response.setMessage("Parametro end obbligatorio");
		// } else if (end <= start) {
		// status = Response.Status.BAD_REQUEST;
		// // response.setIsError(true);
		// //
		// response.setMessage("Parametro end deve essere maggiore di start");
		// }

		Date startRoster = new Date();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(startRoster);

		// int year = cal.get(GregorianCalendar.YEAR);
		// int month = cal.get(GregorianCalendar.MONTH);
		cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
		startRoster = cal.getTime();

		cal.set(GregorianCalendar.DAY_OF_MONTH,
				cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
		// cal.set(Calendar.HOUR, 23);
		// cal.set(Calendar.MINUTE, 59);
		// cal.set(Calendar.SECOND, 59);
		Date endRoster = cal.getTime();

		if (start != null && end != null) {
			startRoster = new Date(start);
			endRoster = new Date(end);

			cal.setTime(startRoster);
		}

		logger.info(methodName + ": Registro presenze dal " + startRoster + " endRoster " + endRoster);

		SqlSession sqlSession = null;
		String filename = "SINTEGRA_RegistroPresenze_"
				+ cal.get(GregorianCalendar.YEAR) + "-"
				+ (cal.get(GregorianCalendar.MONTH) + 1) + "_"
				+ System.currentTimeMillis();

		// File dataDir = new File(System.getProperty("jboss.server.base.dir"));
		// jboss.server.base.dir#sthash.rcJxZB84.dpuf
		// jboss.server.temp.dir
		// File rosterFile = new File(dataDir, filename + ".xls");
		Workbook wb = new XSSFWorkbook();
		// tempdir + filename + ".xls"

		byte[] byteArray = null;

		// File rosterFile = null;
		// try {
		// rosterFile = new File(tempdir + "/" + filename + ".xls");
		// // File.createTempFile(tempdir + filename, ".xls");
		// } catch (Exception e1) {
		// logger.error(methodName
		// + ": Errore nella creazione del file registro presenze", e1);
		// return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
		// .entity("Errore creazione file").build();
		// }
		// logger.info(methodName + "rosterFile: " +
		// rosterFile.getAbsolutePath());
		// new File(tempdir + filename + ".xls");
		try {
			sqlSession = MyBatisConnectionFactory.getSqlSessionFactory().openSession(true);
			UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

			UserExample userExample = new UserExample();
			userExample.createCriteria().andQualifyEqualTo("IMP");

			List<User> users = userMapper.selectByExample(userExample);
			if (users == null || users.size() == 0) {
				logger.info(methodName + ": Valutazione eventi Festa completata");
			} else {
				int rosterDWork = 0;
				int daysInMonth = 0;
				List<String> rosterGM = new ArrayList<String>();
				List<String> rosterGS = new ArrayList<String>();

				GregorianCalendar currDay = new GregorianCalendar();
				currDay.setTime(startRoster);

				while (!currDay.getTime().after(endRoster)) {
					daysInMonth++;

					if (currDay.get(GregorianCalendar.DAY_OF_MONTH) < 10) {
						rosterGM.add("0" + currDay.get(GregorianCalendar.DAY_OF_MONTH));
					} else {
						rosterGM.add("" + currDay.get(GregorianCalendar.DAY_OF_MONTH));
					}

					if (currDay.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.SUNDAY
							&& currDay.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.SATURDAY) {
						rosterDWork++;
					}

					switch (currDay.get(GregorianCalendar.DAY_OF_WEEK)) {
					case GregorianCalendar.SUNDAY:
						rosterGS.add("D");
						break;
					case GregorianCalendar.MONDAY:
						rosterGS.add("L");
						break;
					case GregorianCalendar.TUESDAY:
						rosterGS.add("M");
						break;
					case GregorianCalendar.WEDNESDAY:
						rosterGS.add("M");
						break;
					case GregorianCalendar.THURSDAY:
						rosterGS.add("G");
						break;
					case GregorianCalendar.FRIDAY:
						rosterGS.add("V");
						break;
					case GregorianCalendar.SATURDAY:
						rosterGS.add("S");
						break;
					}
					currDay.add(GregorianCalendar.DAY_OF_MONTH, 1);
				}

				CalendarEventMapper calendarEventMapper = sqlSession.getMapper(CalendarEventMapper.class);

				CalendarEventExample holidayExample = new CalendarEventExample();
				holidayExample.setOrderByClause("START ASC");

				int ordTotM = 0;
				List<String> ordGGM = new ArrayList<String>(daysInMonth);
				List<String> dosangGGM = new ArrayList<String>(daysInMonth);
				List<String> strGGM = new ArrayList<String>(daysInMonth);
				List<String> ferieGGM = new ArrayList<String>(daysInMonth);
				List<String> permGGM = new ArrayList<String>(daysInMonth);
				List<String> malGGM = new ArrayList<String>(daysInMonth);
				List<String> allatGGM = new ArrayList<String>(daysInMonth);

				for (String ggw : rosterGS) {
					if (ggw.equalsIgnoreCase("D") || ggw.equalsIgnoreCase("S")) {
						ordGGM.add(ggw);
						dosangGGM.add(ggw);
						strGGM.add(ggw);
						ferieGGM.add(ggw);
						permGGM.add(ggw);
						malGGM.add(ggw);
						allatGGM.add(ggw);
					} else {
						ordGGM.add("8");
						ordTotM += 8;
						dosangGGM.add("");
						strGGM.add("");
						ferieGGM.add("");
						permGGM.add("");
						malGGM.add("");
						allatGGM.add("");
					}

					currDay.add(GregorianCalendar.DAY_OF_MONTH, 1);
				}

				logger.debug(methodName + ": Inizio valutazione eventi Festa ...");
				List<Byte> holidayCategories = new ArrayList<Byte>(2);
				holidayCategories.add((byte) 7);
				holidayCategories.add((byte) 8);

				Criteria holidayCriteria = holidayExample.createCriteria()
						.andCategoryIn(holidayCategories)
						.andStartGreaterThanOrEqualTo(startRoster)
						.andEndLessThan(endRoster);
				List<CalendarEvent> holidayEvents = calendarEventMapper
						.selectByExample(holidayExample);
				if (holidayEvents != null && holidayEvents.size() > 0) {
					for (int i = 0; i < holidayEvents.size(); i++) {
						GregorianCalendar startRosterHoliday = new GregorianCalendar();
						startRosterHoliday.setTime(holidayEvents.get(i).getStart());
						Date endRosterHoliday = holidayEvents.get(i).getEnd();

						while (startRosterHoliday.getTime().before(endRosterHoliday)) {
							if (startRosterHoliday.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.SUNDAY
									&& startRosterHoliday.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.SATURDAY) {
								rosterDWork--;

								if (holidayEvents.get(i).getCategory() == 8) {
									ordGGM.set(startRosterHoliday.get(GregorianCalendar.DAY_OF_MONTH) - 1, "C");
									dosangGGM.set(startRosterHoliday.get(GregorianCalendar.DAY_OF_MONTH) - 1, "C");
									strGGM.set(startRosterHoliday.get(GregorianCalendar.DAY_OF_MONTH) - 1, "C");
									ferieGGM.set(startRosterHoliday.get(GregorianCalendar.DAY_OF_MONTH) - 1, "C");
									permGGM.set(startRosterHoliday.get(GregorianCalendar.DAY_OF_MONTH) - 1, "C");
									malGGM.set(startRosterHoliday.get(GregorianCalendar.DAY_OF_MONTH) - 1, "C");
									allatGGM.set(startRosterHoliday.get(GregorianCalendar.DAY_OF_MONTH) - 1, "C");
								} else if (holidayEvents.get(i).getCategory() == 7) {
									ordGGM.set(startRosterHoliday .get(GregorianCalendar.DAY_OF_MONTH) - 1, "F");
									dosangGGM.set(startRosterHoliday.get(GregorianCalendar.DAY_OF_MONTH) - 1, "F");
									strGGM.set(startRosterHoliday.get(GregorianCalendar.DAY_OF_MONTH) - 1, "F");
									ferieGGM.set(startRosterHoliday.get(GregorianCalendar.DAY_OF_MONTH) - 1, "F");
									permGGM.set(startRosterHoliday.get(GregorianCalendar.DAY_OF_MONTH) - 1, "F");
									malGGM.set(startRosterHoliday.get(GregorianCalendar.DAY_OF_MONTH) - 1, "F");
									allatGGM.set(startRosterHoliday.get(GregorianCalendar.DAY_OF_MONTH) - 1, "F");
								}
								ordTotM -= 8;
							}
							startRosterHoliday.add(GregorianCalendar.DAY_OF_MONTH, 1);
						}
					}
				}
				logger.debug(methodName + ": Valutazione eventi Festa completata");

				Map<String, CellStyle> styles = BadgeSheetResource.createRosterStyles(wb);
				int rowCounter = 0;

				Sheet sheet = wb.createSheet((cal.get(GregorianCalendar.MONTH) + 1) + "-" + cal.get(GregorianCalendar.YEAR));
				PrintSetup printSetup = sheet.getPrintSetup();
				printSetup.setLandscape(true);
				sheet.setFitToPage(true);
				sheet.setHorizontallyCenter(true);

				Row titleRow = sheet.createRow(rowCounter++);
				// titleRow.setHeightInPoints(45);
				Cell titleCell0 = titleRow.createCell(0);
				titleCell0.setCellValue("Sintegra Srl");
				titleCell0.setCellStyle(styles.get("title"));
				Cell titleCell1 = titleRow.createCell(1);
				Cell titleCell2 = titleRow.createCell(2);
				Cell titleCell3 = titleRow.createCell(3);
				Cell titleCell4 = titleRow.createCell(4);
				titleCell4.setCellValue("Registro Presenze del " + (cal.get(GregorianCalendar.MONTH) + 1) + "-" + cal.get(GregorianCalendar.YEAR));
				titleCell4.setCellStyle(styles.get("title"));

				for (int i = 0; i < rosterGS.size() - 4; i++) {
					Cell titleCelli = titleRow.createCell(5 + i);
					titleCelli.setCellValue(rosterGS.get(i));
					// header2Celli.setCellStyle(styles.get("header"));
				}
				Cell titleCelli1 = titleRow.createCell(2 + rosterGS.size());
				titleCelli1.setCellValue("gg lavorative " + rosterDWork);
				titleCelli1.setCellStyle(styles.get("title"));
				Cell titleCelli2 = titleRow.createCell(3 + rosterGS.size());
				Cell titleCelli3 = titleRow.createCell(4 + rosterGS.size());
				Cell titleCelli4 = titleRow.createCell(5 + rosterGS.size());

				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 1 + rosterGS.size()));
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 2 + rosterGS.size(), 5 + rosterGS.size()));

				Row header1Row = sheet.createRow(rowCounter++);
				// header1Row.setHeightInPoints(40);
				Cell header1Cell0 = header1Row.createCell(0);
				header1Cell0.setCellValue("Codice");
				header1Cell0.setCellStyle(styles.get("header"));
				Cell header1Cell1 = header1Row.createCell(1);
				header1Cell1.setCellValue("Cognome e Nome");
				header1Cell1.setCellStyle(styles.get("header"));
				Cell header1Cell2 = header1Row.createCell(2);
				header1Cell2.setCellValue("Qualifica");
				header1Cell2.setCellStyle(styles.get("header"));
				Cell header1Cell3 = header1Row.createCell(3);
				header1Cell3.setCellValue("g/m");
				header1Cell3.setCellStyle(styles.get("header"));
				for (int i = 0; i < rosterGM.size(); i++) {
					Cell header1Celli = header1Row.createCell(4 + i);
					header1Celli.setCellValue(rosterGM.get(i));
					header1Celli.setCellStyle(styles.get("header"));
				}
				Cell header1TotCell1 = header1Row.createCell(4 + rosterGS.size());
				header1TotCell1.setCellValue("Totali");
				header1TotCell1.setCellStyle(styles.get("header"));
				Cell header1TotCell2 = header1Row.createCell(5 + rosterGS.size());
				header1TotCell2.setCellValue("");
				header1TotCell2.setCellStyle(styles.get("header"));

				sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
				sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1));
				sheet.addMergedRegion(new CellRangeAddress(1, 2, 2, 2));
				sheet.addMergedRegion(new CellRangeAddress(1, 1, 4 + rosterGS.size(), 5 + rosterGS.size()));

				Row header2Row = sheet.createRow(rowCounter++);
				// headerRow.setHeightInPoints(40);
				Cell header2Cell0 = header2Row.createCell(0);
				header2Cell0.setCellValue("");
				header2Cell0.setCellStyle(styles.get("header"));
				Cell header2Cell1 = header2Row.createCell(1);
				header2Cell1.setCellValue("");
				header2Cell1.setCellStyle(styles.get("header"));
				Cell header2Cell2 = header2Row.createCell(2);
				header2Cell2.setCellValue("");
				header2Cell2.setCellStyle(styles.get("header"));
				Cell header2Cell3 = header2Row.createCell(3);
				header2Cell3.setCellValue("g/S");
				header2Cell3.setCellStyle(styles.get("header"));
				for (int i = 0; i < rosterGS.size(); i++) {
					Cell header2Celli = header2Row.createCell(4 + i);
					header2Celli.setCellValue(rosterGS.get(i));
					header2Celli.setCellStyle(styles.get("header"));
				}
				Cell header2TotCell1 = header2Row.createCell(4 + rosterGS.size());
				header2TotCell1.setCellValue("gg");
				header2TotCell1.setCellStyle(styles.get("header"));
				Cell header2TotCell2 = header2Row.createCell(5 + rosterGS.size());
				header2TotCell2.setCellValue("ore");
				header2TotCell2.setCellStyle(styles.get("header"));

				for (User currUser : users) {
					logger.debug(methodName + ": Estrazione dati utente '" + currUser.getFirstname() + " " + currUser.getLastname() + "' ...");

					int ordTot = new Integer(ordTotM);
					List<String> ordGG = new ArrayList<String>(ordGGM);
					int dosangTot = 0;
					List<String> dosangGG = new ArrayList<String>(dosangGGM);
					int strTot = 0;
					List<String> strGG = new ArrayList<String>(strGGM);
					int ferieTot = 0;
					List<String> ferieGG = new ArrayList<String>(ferieGGM);
					int permTot = 0;
					List<String> permGG = new ArrayList<String>(permGGM);
					int malTot = 0;
					List<String> malGG = new ArrayList<String>(malGGM);
					int allatTot = 0;
					List<String> allatGG = new ArrayList<String>(allatGGM);

					CalendarEventExample eventExample = new CalendarEventExample();
					eventExample.setOrderByClause("START ASC");

					Criteria eventCriteria = eventExample.createCriteria().andStartGreaterThanOrEqualTo(startRoster).andEndLessThan(endRoster);
					eventCriteria.andUseridEqualTo(currUser.getId());

					List<CalendarEvent> calendarEvents = calendarEventMapper.selectByExample(eventExample);
					if (calendarEvents != null && calendarEvents.size() > 0) {
						logger.debug(methodName + ": Trovati " + calendarEvents.size() + " eventi");
						for (CalendarEvent currEvent : calendarEvents) {
							cal.setTime(currEvent.getStart());
							switch (currEvent.getCategory()) {
							case 1:
								dosangTot += 8;
								dosangGG.set(cal.get(GregorianCalendar.DAY_OF_MONTH) - 1, "8");
								break;
							case 2:
								long permHours = (((currEvent.getEnd().getTime() - currEvent.getStart().getTime()) / 1000) / 60) / 60;
								permTot += permHours;
								permGG.set(cal.get(GregorianCalendar.DAY_OF_MONTH) - 1, "" + permHours);
								break;
							case 3:
								malTot += 8;
								malGG.set(cal.get(GregorianCalendar.DAY_OF_MONTH) - 1, "8");
								break;
							case 4:
								long allHours = (((currEvent.getEnd().getTime() - currEvent.getStart().getTime()) / 1000) / 60) / 60;
								allatTot += allHours;
								allatGG.set(cal.get(GregorianCalendar.DAY_OF_MONTH) - 1, "" + allHours);
								break;
							case 5:
								ferieTot += 8;
								ferieGG.set(cal.get(GregorianCalendar.DAY_OF_MONTH) - 1, "8");
								break;
							case 6:
								// straordinario
								long strHours = (((currEvent.getEnd().getTime() - currEvent.getStart().getTime()) / 1000) / 60) / 60;
								strTot += strHours;
								strGG.set(cal.get(GregorianCalendar.DAY_OF_MONTH) - 1, "" + strHours);
								break;
							}
						}

						for (int o = 0; o < rosterGS.size(); o++) {
							if (strGG.get(o) != null) {
								int strH = 0;
								try {
									strH = Integer.parseInt(strGG.get(o));
								} catch (Exception e) {
								}
								if (strH > 0) {
									if (rosterGS.get(o).equalsIgnoreCase("D")
											|| rosterGS.get(o).equalsIgnoreCase("S")) {
										ordGG.set(o, "" + strH);
									} else {
										ordGG.set(o, "" + (Integer.parseInt(ordGG.get(o)) + strH));
										ordTot += Integer.parseInt(strGG.get(o));
									}
								}
							}
							if (ferieGG.get(o) != null) {
								int ferieH = 0;
								try {
									ferieH = Integer.parseInt(ferieGG.get(o));
								} catch (Exception e) {
								}
								if (ferieH > 0) {
									ordTot -= ferieH;
									ordGG.set(o, "" + (Integer.parseInt(ordGG.get(o)) - ferieH));
								}
							}
							if (permGG.get(o) != null) {
								int permH = 0;
								try {
									permH = Integer.parseInt(permGG.get(o));
								} catch (Exception e) {
								}
								if (permH > 0) {
									ordTot -= permH;
									ordGG.set(o, "" + (Integer.parseInt(ordGG.get(o)) - permH));
								}
							}
							if (allatGG.get(o) != null) {
								int allatH = 0;
								try {
									allatH = Integer.parseInt(allatGG.get(o));
								} catch (Exception e) {
								}
								if (allatH > 0) {
									ordTot -= allatH;
									ordGG.set(
											o,
											""
													+ (Integer.parseInt(ordGG
															.get(o)) - allatH));
								}
							}
							if (malGG.get(o) != null) {
								int malH = 0;
								try {
									malH = Integer.parseInt(malGG.get(o));
								} catch (Exception e) {
								}
								if (malH > 0) {
									ordTot -= malH;
									ordGG.set(
											o,
											""
													+ (Integer.parseInt(ordGG
															.get(o)) - malH));
								}
							}
							if (dosangGG.get(o) != null) {
								int dosangH = 0;
								try {
									dosangH = Integer.parseInt(dosangGG.get(o));
								} catch (Exception e) {
								}
								if (dosangH > 0) {
									ordTot -= dosangH;
									ordGG.set(
											o,
											""
													+ (Integer.parseInt(ordGG
															.get(o)) - dosangH));
								}
							}
						}
						//
						// response.setEvents(events);
						// response.setNumResults(events.size());
						// response.setMessage("Risultati trovati: "
						// + response.getNumResults());
						// logger.info(methodName + ": Trovati " + events.size()
						// + " eventi");
						// } else {
						// response.setNumResults(0);
						// response.setMessage("Nessun evento trovato");
						// logger.info(methodName + ": Nessun evento trovato");
						// }
					} else {
						logger.debug(methodName + ": Nessun evento trovato");
					}

					logger.debug(methodName + ": Inizio scrittura dati utente "
							+ currUser.getFirstname() + " "
							+ currUser.getLastname());
					Row userRow = sheet.createRow(rowCounter++);
					// headerRow.setHeightInPoints(40);
					Cell userCell0 = userRow.createCell(0);
					userCell0.setCellValue(currUser.getCode());
					Cell userCell1 = userRow.createCell(1);
					userCell1
							.setCellValue((currUser.getFirstname() + " " + currUser
									.getLastname()).toUpperCase());
					Cell userCell2 = userRow.createCell(2);
					userCell2.setCellValue(currUser.getQualify() + ".");
					Cell userCell3 = userRow.createCell(3);
					userCell3.setCellValue("ord.");
					for (int i = 0; i < ordGG.size(); i++) {
						Cell ordCelli = userRow.createCell(4 + i);
						ordCelli.setCellValue(ordGG.get(i));
						// header2Celli.setCellStyle(styles.get("header"));
					}
					Cell totCell1 = userRow.createCell(4 + rosterGS.size());
					totCell1.setCellType(Cell.CELL_TYPE_NUMERIC);
					totCell1.setCellValue(ordTot / 8);
					Cell totCell2 = userRow.createCell(5 + rosterGS.size());
					totCell2.setCellType(Cell.CELL_TYPE_NUMERIC);
					totCell2.setCellValue(ordTot);

					if (strTot > 0) {
						Row strRow = sheet.createRow(rowCounter++);
						// headerRow.setHeightInPoints(40);
						Cell strCell0 = strRow.createCell(0);
						strCell0.setCellValue("");
						Cell strCell1 = strRow.createCell(1);
						strCell1.setCellValue("");
						Cell strCell2 = strRow.createCell(2);
						strCell2.setCellValue("");
						Cell strCell3 = strRow.createCell(3);
						strCell3.setCellValue("str.");
						for (int i = 0; i < strGG.size(); i++) {
							Cell strCelli = strRow.createCell(4 + i);
							strCelli.setCellValue(strGG.get(i));
						}
						Cell strTotCell1 = strRow.createCell(4 + rosterGS
								.size());
						strTotCell1.setCellType(Cell.CELL_TYPE_NUMERIC);
						strTotCell1.setCellValue(strTot / 8);
						Cell strTotCell2 = strRow.createCell(5 + rosterGS
								.size());
						strTotCell2.setCellType(Cell.CELL_TYPE_NUMERIC);
						strTotCell2.setCellValue(strTot);
					}

					if (ferieTot > 0) {
						Row ferieRow = sheet.createRow(rowCounter++);
						// headerRow.setHeightInPoints(40);
						Cell ferieCell0 = ferieRow.createCell(0);
						ferieCell0.setCellValue("");
						Cell ferieCell1 = ferieRow.createCell(1);
						ferieCell1.setCellValue("");
						Cell ferieCell2 = ferieRow.createCell(2);
						ferieCell2.setCellValue("");
						Cell ferieCell3 = ferieRow.createCell(3);
						ferieCell3.setCellValue("ferie");
						for (int i = 0; i < ferieGG.size(); i++) {
							Cell ferieCelli = ferieRow.createCell(4 + i);
							ferieCelli.setCellValue(ferieGG.get(i));
						}
						Cell ferieTotCell1 = ferieRow.createCell(4 + rosterGS
								.size());
						ferieTotCell1.setCellType(Cell.CELL_TYPE_NUMERIC);
						ferieTotCell1.setCellValue(ferieTot / 8);
						Cell ferieTotCell2 = ferieRow.createCell(5 + rosterGS
								.size());
						ferieTotCell2.setCellType(Cell.CELL_TYPE_NUMERIC);
						ferieTotCell2.setCellValue(ferieTot);
					}

					if (permTot > 0) {
						Row permRow = sheet.createRow(rowCounter++);
						// headerRow.setHeightInPoints(40);
						Cell permCell0 = permRow.createCell(0);
						permCell0.setCellValue("");
						Cell permCell1 = permRow.createCell(1);
						permCell1.setCellValue("");
						Cell permCell2 = permRow.createCell(2);
						permCell2.setCellValue("");
						Cell permCell3 = permRow.createCell(3);
						permCell3.setCellValue("perm.");
						for (int i = 0; i < permGG.size(); i++) {
							Cell permCelli = permRow.createCell(4 + i);
							permCelli.setCellValue(permGG.get(i));
						}
						Cell permTotCell1 = permRow.createCell(4 + rosterGS
								.size());
						permTotCell1.setCellType(Cell.CELL_TYPE_NUMERIC);
						permTotCell1.setCellValue(permTot / 8);
						Cell permTotCell2 = permRow.createCell(5 + rosterGS
								.size());
						permTotCell2.setCellType(Cell.CELL_TYPE_NUMERIC);
						permTotCell2.setCellValue(permTot);
					}

					if (malTot > 0) {
						Row malRow = sheet.createRow(rowCounter++);
						// headerRow.setHeightInPoints(40);
						Cell malCell0 = malRow.createCell(0);
						malCell0.setCellValue("");
						Cell malCell1 = malRow.createCell(1);
						malCell1.setCellValue("");
						Cell malCell2 = malRow.createCell(2);
						malCell2.setCellValue("");
						Cell malCell3 = malRow.createCell(3);
						malCell3.setCellValue("mal.");
						for (int i = 0; i < malGG.size(); i++) {
							Cell malCelli = malRow.createCell(4 + i);
							malCelli.setCellValue(malGG.get(i));
						}
						Cell malTotCell1 = malRow.createCell(4 + rosterGS
								.size());
						malTotCell1.setCellType(Cell.CELL_TYPE_NUMERIC);
						malTotCell1.setCellValue(malTot / 8);
						Cell malTotCell2 = malRow.createCell(5 + rosterGS
								.size());
						malTotCell2.setCellType(Cell.CELL_TYPE_NUMERIC);
						malTotCell2.setCellValue(malTot);
					}

					if (allatTot > 0) {
						Row allatRow = sheet.createRow(rowCounter++);
						// headerRow.setHeightInPoints(40);
						Cell allatCell0 = allatRow.createCell(0);
						allatCell0.setCellValue("");
						Cell allatCell1 = allatRow.createCell(1);
						allatCell1.setCellValue("");
						Cell allatCell2 = allatRow.createCell(2);
						allatCell2.setCellValue("");
						Cell allatCell3 = allatRow.createCell(3);
						allatCell3.setCellValue("allat.");
						for (int i = 0; i < allatGG.size(); i++) {
							Cell allatCelli = allatRow.createCell(4 + i);
							allatCelli.setCellValue(allatGG.get(i));
						}
						Cell allatTotCell1 = allatRow.createCell(4 + rosterGS
								.size());
						allatTotCell1.setCellType(Cell.CELL_TYPE_NUMERIC);
						allatTotCell1.setCellValue(allatTot / 8);
						Cell allatTotCell2 = allatRow.createCell(5 + rosterGS
								.size());
						allatTotCell2.setCellType(Cell.CELL_TYPE_NUMERIC);
						allatTotCell2.setCellValue(allatTot);
					}

					if (dosangTot > 0) {
						Row dosangRow = sheet.createRow(rowCounter++);
						// headerRow.setHeightInPoints(40);
						Cell dosangCell0 = dosangRow.createCell(0);
						dosangCell0.setCellValue("");
						Cell dosangCell1 = dosangRow.createCell(1);
						dosangCell1.setCellValue("");
						Cell dosangCell2 = dosangRow.createCell(2);
						dosangCell2.setCellValue("");
						Cell dosangCell3 = dosangRow.createCell(3);
						dosangCell3.setCellValue("dosang.");
						for (int i = 0; i < dosangGG.size(); i++) {
							Cell dosangCelli = dosangRow.createCell(4 + i);
							dosangCelli.setCellValue(dosangGG.get(i));
						}
						Cell dosangTotCell1 = dosangRow.createCell(4 + rosterGS
								.size());
						dosangTotCell1.setCellType(Cell.CELL_TYPE_NUMERIC);
						dosangTotCell1.setCellValue(dosangTot / 8);
						Cell dosangTotCell2 = dosangRow.createCell(5 + rosterGS
								.size());
						dosangTotCell2.setCellType(Cell.CELL_TYPE_NUMERIC);
						dosangTotCell2.setCellValue(dosangTot);
					}
					logger.debug(methodName + ": Fine scrittura dati utente");

				}// for users

				logger.debug(methodName + ": Inizio preparazione risposta ...");

				// FileOutputStream out = new FileOutputStream(rosterFile);
				// wb.write(out);
				// out.flush();
				// out.close();

				// try {
				// // InputStream is =
				// // gestioneFile.readTracciatoFileSystem(nomefile, pathFile);
				// // if(is!=null){
				ByteArrayOutputStream outB = new ByteArrayOutputStream();
				wb.write(outB);
				// ftp.storeFile(tempdir, outB);
				wb.close();
				// outB.close();
				// // lettura dallo stream
				// // int r;
				// // while ((r = is.read()) != -1)
				// // {
				// // out.write(r);
				// // }
				byteArray = outB.toByteArray();
				//
				// outB.close();
				//
				// // is.close();
				// // }else{
				// // throw new IllegalArgumentException("File non trovato");
				// // }
				// } catch (Throwable th) {
				// logger.warn("Errore durante il recupero del file allegato",
				// th);
				// throw new IllegalArgumentException(
				// "Impossibile recuperare il file allegato");
				// }
				// logger.debug(methodName + ": fine preparazione risposta");

			}
		} catch (Throwable th) {
			logger.error("Errore durante la ricerca eventi", th);
			// response.setIsError(true);
			// response.setMessage(th.getMessage());
			status = Response.Status.INTERNAL_SERVER_ERROR;
		} finally {
			sqlSession.close();
			logger.debug(methodName + ": FINE");
		}

		// ResponseBuilder responseBuilder = Response.ok((Object) rosterFile);
		// responseBuilder.header("Content-Disposition",
		// "attachment; filename=\""
		// + filename + ".xls\"");

		// ResponseBuilder responseBuilder = Response.ok(byteArray,
		// MediaType.APPLICATION_OCTET_STREAM).header(
		// "content-disposition",
		// "attachment; filename=" + filename + ".xls");

		// ResponseBuilder response =
		// Response.status(status).entity(rosterFile);
		// response.header("Content-Disposition", "attachment; filename="
		// + filename + ".xls");
		// return responseBuilder.build();

		// final Workbook wbToDownload = wb;
		//
		// StreamingOutput stream = new StreamingOutput() {
		//
		// @Override
		// public void write(OutputStream output) throws IOException,
		// WebApplicationException {
		// try {
		// wbToDownload.write(output);
		// } catch (Exception e) {
		// throw new WebApplicationException(e);
		// }
		// }
		// };

		return Response
				.ok(byteArray)
				.header("Content-Disposition",
						"attachment; filename=" + filename + ".xls").build();

		// return Response
		// .ok(stream)
		// .header("content-disposition",
		// "attachment; filename=" + filename + ".xls").build();

		// return responseBuilder.build();

	}

	private static Map<String, CellStyle> createRosterStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		CellStyle style;
		Font titleFont = wb.createFont();
		titleFont.setFontHeightInPoints((short) 18);
		titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(titleFont);
		styles.put("title", style);

		// Font monthFont = wb.createFont();
		// monthFont.setFontHeightInPoints((short) 11);
		// monthFont.setColor(IndexedColors.WHITE.getIndex());
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		// style.setFont(monthFont);
		// style.setWrapText(true);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styles.put("header", style);

		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setWrapText(true);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styles.put("cell", style);

		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
		styles.put("formula", style);

		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
		styles.put("formula_2", style);

		return styles;
	}

	// @GET
	// @Path("/download/xls")
	// @Produces("application/vnd.ms-excel")
	// public Response downloadExcelFile() {
	//
	// // set file (and path) to be download
	// File file = new File("D:/Demo/download/Sample.xlsx");
	//
	// ResponseBuilder responseBuilder = Response.ok((Object) file);
	// responseBuilder.header("Content-Disposition",
	// "attachment; filename=\"MyJerseyExcelFile.xlsx\"");
	// return responseBuilder.build();
	// }

}
