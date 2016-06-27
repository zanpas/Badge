import it.sintegra.badge.business.dao.mybatis.mapper.CalendarEventMapper;
import it.sintegra.badge.business.dao.mybatis.mapper.UserMapper;
import it.sintegra.badge.business.entity.CalendarEvent;
import it.sintegra.badge.business.entity.CalendarEventExample;
import it.sintegra.badge.business.entity.CalendarEventExample.Criteria;
import it.sintegra.badge.business.entity.User;
import it.sintegra.badge.business.entity.UserExample;
import it.sintegra.badge.business.mybatis.MyBatisConnectionFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;

public class RosterTest {

	private static final Logger logger = Logger.getLogger(RosterTest.class);

	private static final String[] titles = { "Person", "ID", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Total\nHrs", "Overtime\nHrs", "Regular\nHrs" };

	private static Object[][] sample_data = {
			{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Gisella Bronzetti", "GB", 4.0, 3.0, 1.0, 3.5, null, null, 4.0 }, };

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Create a library of cell styles
	 */
	private static Map<String, CellStyle> createStyles(Workbook wb) {
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

		Font monthFont = wb.createFont();
		monthFont.setFontHeightInPoints((short) 11);
		monthFont.setColor(IndexedColors.WHITE.getIndex());
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(monthFont);
		style.setWrapText(true);
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

	@Test
	public void test1() {
		{
			try {
				Workbook wb = new XSSFWorkbook();

				Map<String, CellStyle> styles = createStyles(wb);

				Sheet sheet = wb.createSheet("Timesheet");
				PrintSetup printSetup = sheet.getPrintSetup();
				printSetup.setLandscape(true);
				sheet.setFitToPage(true);
				sheet.setHorizontallyCenter(true);

				// title row
				Row titleRow = sheet.createRow(0);
				titleRow.setHeightInPoints(45);
				Cell titleCell = titleRow.createCell(0);
				titleCell.setCellValue("Sintegra Srl");
				titleCell.setCellStyle(styles.get("title"));
				titleCell.setCellValue("Weekly Timesheet");
				titleCell.setCellStyle(styles.get("title"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("$B$1:$K$1"));
				titleCell.setCellValue("gg lavorative");
				titleCell.setCellStyle(styles.get("title"));

				// header row
				Row headerRow = sheet.createRow(1);
				headerRow.setHeightInPoints(40);
				Cell headerCell;
				for (int i = 0; i < titles.length; i++) {
					headerCell = headerRow.createCell(i);
					headerCell.setCellValue(titles[i]);
					headerCell.setCellStyle(styles.get("header"));
				}

				int rownum = 2;
				for (int i = 0; i < 10; i++) {
					Row row = sheet.createRow(rownum++);
					for (int j = 0; j < titles.length; j++) {
						Cell cell = row.createCell(j);
						if (j == 9) {
							// the 10th cell contains sum over week days, e.g.
							// SUM(C3:I3)
							String ref = "C" + rownum + ":I" + rownum;
							cell.setCellFormula("SUM(" + ref + ")");
							cell.setCellStyle(styles.get("formula"));
						} else if (j == 11) {
							cell.setCellFormula("J" + rownum + "-K" + rownum);
							cell.setCellStyle(styles.get("formula"));
						} else {
							cell.setCellStyle(styles.get("cell"));
						}
					}
				}

				// row with totals below
				Row sumRow = sheet.createRow(rownum++);
				sumRow.setHeightInPoints(35);
				Cell cell;
				cell = sumRow.createCell(0);
				cell.setCellStyle(styles.get("formula"));
				cell = sumRow.createCell(1);
				cell.setCellValue("Total Hrs:");
				cell.setCellStyle(styles.get("formula"));

				for (int j = 2; j < 12; j++) {
					cell = sumRow.createCell(j);
					String ref = (char) ('A' + j) + "3:" + (char) ('A' + j)
							+ "12";
					cell.setCellFormula("SUM(" + ref + ")");
					if (j >= 9)
						cell.setCellStyle(styles.get("formula_2"));
					else
						cell.setCellStyle(styles.get("formula"));
				}
				rownum++;
				sumRow = sheet.createRow(rownum++);
				sumRow.setHeightInPoints(25);
				cell = sumRow.createCell(0);
				cell.setCellValue("Total Regular Hours");
				cell.setCellStyle(styles.get("formula"));
				cell = sumRow.createCell(1);
				cell.setCellFormula("L13");
				cell.setCellStyle(styles.get("formula_2"));
				sumRow = sheet.createRow(rownum++);
				sumRow.setHeightInPoints(25);
				cell = sumRow.createCell(0);
				cell.setCellValue("Total Overtime Hours");
				cell.setCellStyle(styles.get("formula"));
				cell = sumRow.createCell(1);
				cell.setCellFormula("K13");
				cell.setCellStyle(styles.get("formula_2"));

				// set sample data
				for (int i = 0; i < sample_data.length; i++) {
					Row row = sheet.getRow(2 + i);
					for (int j = 0; j < sample_data[i].length; j++) {
						if (sample_data[i][j] == null)
							continue;

						if (sample_data[i][j] instanceof String) {
							row.getCell(j).setCellValue(
									(String) sample_data[i][j]);
						} else {
							row.getCell(j).setCellValue(
									(Double) sample_data[i][j]);
						}
					}
				}

				// finally set column widths, the width is measured in units of
				// 1/256th of a character width
				sheet.setColumnWidth(0, 30 * 256); // 30 characters wide
				for (int i = 2; i < 9; i++) {
					sheet.setColumnWidth(i, 6 * 256); // 6 characters wide
				}
				sheet.setColumnWidth(10, 10 * 256); // 10 characters wide

				// Write the output to a file
				String file = "C:/WS_API_SINTEGRA/test/timesheet.xls";
				if (wb instanceof XSSFWorkbook)
					file += "x";
				FileOutputStream out = new FileOutputStream(file);
				wb.write(out);
				out.close();
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	@Test
	public void test2() {
		try {
			Workbook wb = new XSSFWorkbook();
			XSSFSheet sheet = (XSSFSheet) wb.createSheet();

			// Create
			XSSFTable table = sheet.createTable();
			table.setDisplayName("Test");
			CTTable cttable = table.getCTTable();

			// Set which area the table should be placed in
			AreaReference reference = new AreaReference(
					new CellReference(0, 0), new CellReference(2, 2));
			cttable.setRef(reference.formatAsString());
			cttable.setId(1);
			cttable.setName("Test");
			cttable.setTotalsRowCount(1);

			CTTableColumns columns = cttable.addNewTableColumns();
			columns.setCount(3);
			CTTableColumn column;
			XSSFRow row;
			XSSFCell cell;

			for (int i = 0; i < 3; i++) {
				// Create column
				column = columns.addNewTableColumn();
				column.setName("Column");
				column.setId(i + 1);
				// Create row
				row = sheet.createRow(i);
				for (int j = 0; j < 3; j++) {
					// Create cell
					cell = row.createCell(j);
					if (i == 0) {
						cell.setCellValue("Column" + j);
					} else {
						cell.setCellValue("0");
					}
				}
			}

			FileOutputStream fileOut = new FileOutputStream("ooxml-table.xlsx");
			wb.write(fileOut);
			fileOut.close();
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	@Test
	public void test3() {
		final String methodName = "test3";
		try {

			Date startRoster = new Date();
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(startRoster);

			// int year = cal.get(GregorianCalendar.YEAR);
			// int month = cal.get(GregorianCalendar.MONTH);
			cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
			startRoster = java.sql.Date.valueOf("2016-06-01");

			cal.set(GregorianCalendar.DAY_OF_MONTH,
					cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
			// cal.set(Calendar.HOUR, 23);
			// cal.set(Calendar.MINUTE, 59);
			// cal.set(Calendar.SECOND, 59);
			Date endRoster = java.sql.Date.valueOf("2016-06-30");

			// if (start != null && end != null) {
			// startRoster = new Date(start);
			// endRoster = new Date(end);
			//
			// cal.setTime(startRoster);
			// }

			logger.info(methodName + ": Registro presenze dal " + startRoster
					+ " endRoster " + endRoster);

			SqlSession sqlSession = null;
			String filename = "SINTEGRA_RegistroPresenze_"
					+ cal.get(GregorianCalendar.YEAR) + "-"
					+ (cal.get(GregorianCalendar.MONTH) + 1) + "_"
					+ System.currentTimeMillis();
			File rosterFile = new File("C:/ws_api_sintegra/TempDir/" + filename + ".xls");
			try {
				sqlSession = MyBatisConnectionFactory.getSqlSessionFactory()
						.openSession(true);

				UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

				UserExample userExample = new UserExample();
				// userExample.createCriteria().an
				List<User> users = userMapper.selectByExample(userExample);
				if (users == null || users.size() == 0) {

				} else {

					int rosterDWork = 0;
					int daysInMonth = 0;

					List<String> rosterGM = new ArrayList<String>();
					List<String> rosterGS = new ArrayList<String>();

					// $rootScope.employees = [];

					GregorianCalendar currDay = new GregorianCalendar();
					currDay.setTime(startRoster);

					while (!currDay.getTime().after(endRoster)) {
						daysInMonth++;

						if (currDay.get(GregorianCalendar.DAY_OF_MONTH) < 10) {
							rosterGM.add("0"
									+ currDay
											.get(GregorianCalendar.DAY_OF_MONTH));
						} else {
							rosterGM.add(""
									+ currDay
											.get(GregorianCalendar.DAY_OF_MONTH));
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

					CalendarEventMapper calendarEventMapper = sqlSession
							.getMapper(CalendarEventMapper.class);

					CalendarEventExample holidayExample = new CalendarEventExample();
					holidayExample.setOrderByClause("START ASC");

					int ordTot = 0;
					List<String> ordGG = new ArrayList<String>(daysInMonth);
					int dosangTot = 0;
					List<String> dosangGG = new ArrayList<String>(daysInMonth);
					int strTot = 0;
					List<String> strGG = new ArrayList<String>(daysInMonth);
					int ferieTot = 0;
					List<String> ferieGG = new ArrayList<String>(daysInMonth);
					int permTot = 0;
					List<String> permGG = new ArrayList<String>(daysInMonth);
					int malTot = 0;
					List<String> malGG = new ArrayList<String>(daysInMonth);
					int allatTot = 0;
					List<String> allatGG = new ArrayList<String>(daysInMonth);

					for (String ggw : rosterGS) {
						if (ggw.equalsIgnoreCase("D")
								|| ggw.equalsIgnoreCase("S")) {
							ordGG.add(ggw);
							dosangGG.add(ggw);
							strGG.add(ggw);
							ferieGG.add(ggw);
							permGG.add(ggw);
							malGG.add(ggw);
							allatGG.add(ggw);
						} else {
							ordGG.add("8");
							ordTot += 8;
							dosangGG.add("");
							strGG.add("");
							ferieGG.add("");
							permGG.add("");
							malGG.add("");
							allatGG.add("");
						}

						currDay.add(GregorianCalendar.DAY_OF_MONTH, 1);
					}

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
							startRosterHoliday.setTime(holidayEvents.get(i)
									.getStart());
							Date endRosterHoliday = holidayEvents.get(i)
									.getEnd();

							while (startRosterHoliday.getTime().before(
									endRosterHoliday)) {
								if (startRosterHoliday
										.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.SUNDAY
										&& startRosterHoliday
												.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.SATURDAY) {
									rosterDWork--;

									if (holidayEvents.get(i).getCategory() == 8) {
										ordGG.set(
												startRosterHoliday
														.get(GregorianCalendar.DAY_OF_MONTH) - 1,
												"C");
										dosangGG.set(
												startRosterHoliday
														.get(GregorianCalendar.DAY_OF_MONTH) - 1,
												"C");
										strGG.set(
												startRosterHoliday
														.get(GregorianCalendar.DAY_OF_MONTH) - 1,
												"C");
										ferieGG.set(
												startRosterHoliday
														.get(GregorianCalendar.DAY_OF_MONTH) - 1,
												"C");
										permGG.set(
												startRosterHoliday
														.get(GregorianCalendar.DAY_OF_MONTH) - 1,
												"C");
										malGG.set(
												startRosterHoliday
														.get(GregorianCalendar.DAY_OF_MONTH) - 1,
												"C");
										allatGG.set(
												startRosterHoliday
														.get(GregorianCalendar.DAY_OF_MONTH) - 1,
												"C");
									} else if (holidayEvents.get(i)
											.getCategory() == 7) {
										ordGG.set(
												startRosterHoliday
														.get(GregorianCalendar.DAY_OF_MONTH) - 1,
												"F");
										dosangGG.set(
												startRosterHoliday
														.get(GregorianCalendar.DAY_OF_MONTH) - 1,
												"F");
										strGG.set(
												startRosterHoliday
														.get(GregorianCalendar.DAY_OF_MONTH) - 1,
												"F");
										ferieGG.set(
												startRosterHoliday
														.get(GregorianCalendar.DAY_OF_MONTH) - 1,
												"F");
										permGG.set(
												startRosterHoliday
														.get(GregorianCalendar.DAY_OF_MONTH) - 1,
												"F");
										malGG.set(
												startRosterHoliday
														.get(GregorianCalendar.DAY_OF_MONTH) - 1,
												"F");
										allatGG.set(
												startRosterHoliday
														.get(GregorianCalendar.DAY_OF_MONTH) - 1,
												"F");
									}
									ordTot -= 8;

								}
								startRosterHoliday.add(
										GregorianCalendar.DAY_OF_MONTH, 1);
							}
						}
					}

					Workbook wb = new XSSFWorkbook();

					// Map<String, CellStyle> styles = createStyles(wb);

					Sheet sheet = wb.createSheet("Timesheet");
					PrintSetup printSetup = sheet.getPrintSetup();
					printSetup.setLandscape(true);
					sheet.setFitToPage(true);
					sheet.setHorizontallyCenter(true);

					CellStyle style = wb.createCellStyle();
					Font titleFont = wb.createFont();
					titleFont.setFontHeightInPoints((short) 18);
					titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
					style.setAlignment(CellStyle.ALIGN_CENTER);
					style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
					style.setFont(titleFont);

					// title row
					Row titleRow = sheet.createRow(0);
					// titleRow.setHeightInPoints(45);
					Cell titleCell0 = titleRow.createCell(0);
					titleCell0.setCellValue("Sintegra Srl");
					titleCell0.setCellStyle(style);
					Cell titleCell1 = titleRow.createCell(1);
					Cell titleCell2 = titleRow.createCell(2);
					titleCell2.setCellValue("Registro Presenze");
					titleCell2.setCellStyle(style);
					Cell titleCell3 = titleRow.createCell(3);
					for (int i = 0; i < rosterGS.size(); i++) {
						Cell titleCelli = titleRow.createCell(4 + i);
						titleCelli.setCellValue(rosterGS.get(i));
						// header2Celli.setCellStyle(styles.get("header"));
					}
					Cell titleCelli1 = titleRow.createCell(4 + rosterGS.size());
					titleCelli1.setCellValue("gg lavorative " + rosterDWork);
					titleCelli1.setCellStyle(style);
					Cell titleCelli2 = titleRow.createCell(5 + rosterGS.size());

					sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 2,
							3 + rosterGS.size()));
					sheet.addMergedRegion(new CellRangeAddress(0, 0,
							4 + rosterGS.size(), 5 + rosterGS.size()));

					Row header1Row = sheet.createRow(1);
					// header1Row.setHeightInPoints(40);
					Cell header1Cell0 = header1Row.createCell(0);
					header1Cell0.setCellValue("Codice");
					Cell header1Cell1 = header1Row.createCell(1);
					header1Cell1.setCellValue("Cognome e Nome");
					Cell header1Cell2 = header1Row.createCell(2);
					header1Cell2.setCellValue("Qualifica");
					Cell header1Cell3 = header1Row.createCell(3);
					header1Cell3.setCellValue("g/m");
					for (int i = 0; i < rosterGM.size(); i++) {
						Cell header1Celli = header1Row.createCell(4 + i);
						header1Celli.setCellValue(rosterGM.get(i));
						// headerCell.setCellStyle(styles.get("header"));
					}
					Cell header1TotCell1 = header1Row.createCell(4 + rosterGS
							.size());
					header1TotCell1.setCellValue("Totali");
					Cell header1TotCell2 = header1Row.createCell(5 + rosterGS
							.size());
					header1TotCell2.setCellValue("");

					Row header2Row = sheet.createRow(2);
					// headerRow.setHeightInPoints(40);
					Cell header2Cell0 = header2Row.createCell(0);
					header2Cell0.setCellValue("");
					Cell header2Cell1 = header2Row.createCell(1);
					header2Cell1.setCellValue("");
					Cell header2Cell2 = header2Row.createCell(2);
					header2Cell2.setCellValue("");
					Cell header2Cell3 = header2Row.createCell(3);
					header2Cell3.setCellValue("g/S");
					for (int i = 0; i < rosterGS.size(); i++) {
						Cell header2Celli = header2Row.createCell(4 + i);
						header2Celli.setCellValue(rosterGS.get(i));
						// header2Celli.setCellStyle(styles.get("header"));
					}
					Cell header2TotCell1 = header2Row.createCell(4 + rosterGS
							.size());
					header2TotCell1.setCellValue("gg");
					Cell header2TotCell2 = header2Row.createCell(5 + rosterGS
							.size());
					header2TotCell2.setCellValue("ore");

					CalendarEventExample eventExample = new CalendarEventExample();
					eventExample.setOrderByClause("START ASC");

					Criteria eventCriteria = eventExample.createCriteria()
							.andStartGreaterThanOrEqualTo(startRoster)
							.andEndLessThan(endRoster);

					// for(User currUser : users){

					User currUser = new User();
					currUser.setId((short) 8);
					currUser.setFirstname("donato");
					currUser.setLastname("savino");

					eventCriteria.andUseridEqualTo(currUser.getId());

					List<CalendarEvent> calendarEvents = calendarEventMapper
							.selectByExample(eventExample);
					if (calendarEvents != null && calendarEvents.size() > 0) {
						logger.debug(methodName + ": Trovati "
								+ calendarEvents.size() + " eventi");
						for (CalendarEvent currEvent : calendarEvents) {
							cal.setTime(currEvent.getStart());
							switch (currEvent.getCategory()) {
							case 1:
								dosangTot += 8;
								dosangGG.set(
										cal.get(GregorianCalendar.DAY_OF_MONTH) - 1,
										"8");
								break;
							case 2:
								long permHours = (((currEvent.getEnd()
										.getTime() - currEvent.getStart()
										.getTime()) / 1000) / 60) / 60;
								permTot += permHours;
								permGG.set(
										cal.get(GregorianCalendar.DAY_OF_MONTH) - 1,
										"" + permHours);
								break;
							case 3:
								malTot += 8;
								malGG.set(
										cal.get(GregorianCalendar.DAY_OF_MONTH) - 1,
										"8");
								break;
							case 4:
								long allHours = (((currEvent.getEnd().getTime() - currEvent
										.getStart().getTime()) / 1000) / 60) / 60;
								allatTot += allHours;
								allatGG.set(
										cal.get(GregorianCalendar.DAY_OF_MONTH) - 1,
										"" + allHours);
								break;
							case 5:
								ferieTot += 8;
								ferieGG.set(
										cal.get(GregorianCalendar.DAY_OF_MONTH) - 1,
										"8");
								break;
							case 6:
								// straordinario
								long strHours = (((currEvent.getEnd().getTime() - currEvent
										.getStart().getTime()) / 1000) / 60) / 60;
								strTot += strHours;
								strGG.set(
										cal.get(GregorianCalendar.DAY_OF_MONTH) - 1,
										"" + strHours);
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
											|| rosterGS.get(o)
													.equalsIgnoreCase("S")) {
										ordGG.set(o, "" + strH);
									} else {
										ordGG.set(
												o,
												""
														+ (Integer.parseInt(ordGG
																.get(o)) + strH));
										ordTot += Integer
												.parseInt(strGG.get(o));
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
									ordGG.set(
											o,
											""
													+ (Integer.parseInt(ordGG
															.get(o)) - ferieH));
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
									ordGG.set(
											o,
											""
													+ (Integer.parseInt(ordGG
															.get(o)) - permH));
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

					Row userRow = sheet.createRow(3);
					// headerRow.setHeightInPoints(40);
					Cell userCell0 = userRow.createCell(0);
					userCell0.setCellValue("1");
					Cell userCell1 = userRow.createCell(1);
					userCell1
							.setCellValue((currUser.getFirstname() + " " + currUser
									.getLastname()).toUpperCase());
					Cell userCell2 = userRow.createCell(2);
					userCell2.setCellValue("IMP.");
					Cell userCell3 = userRow.createCell(3);
					userCell3.setCellValue("ord.");
					for (int i = 0; i < ordGG.size(); i++) {
						Cell ordCelli = userRow.createCell(4 + i);
						ordCelli.setCellValue(ordGG.get(i));
						// header2Celli.setCellStyle(styles.get("header"));
					}
					Cell totCell1 = userRow.createCell(4 + rosterGS.size());
					totCell1.setCellValue((ordTot / 8));
					Cell totCell2 = userRow.createCell(5 + rosterGS.size());
					totCell2.setCellValue(ordTot);

					// }for users

					FileOutputStream out = new FileOutputStream(rosterFile);
					wb.write(out);
					out.close();
					wb.close();

				}
			} catch (Throwable th) {
				logger.error("Errore durante la ricerca eventi", th);

			} finally {
				sqlSession.close();
				logger.debug(methodName + ": FINE");
			}

		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

}
