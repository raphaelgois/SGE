/* Copyright © 2005 - 2014 Annpoint, s.r.o.
   Use of this software is subject to license terms. 
   http://www.daypilot.org/
*/

package com.sge.calendar.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.daypilot.date.DateTime;
import org.daypilot.json.JSONException;
import org.daypilot.recurrence.RecurrenceRule;

public class Db {

	
	/**
	 * @param from
	 * @param to
	 * @return
	 * @throws SQLException
	 * @throws JSONException
	 * @throws ClassNotFoundException
	 */
	public static Table getEvents(HttpServletRequest request, Date from, Date to) throws SQLException, JSONException, ClassNotFoundException {
		Class.forName("org.mysql.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "root", "root");

		PreparedStatement st = c.prepareStatement("SELECT event_id, event_name, event_start, event_end, event_resource, event_allday, event_recurrence, event_join FROM EVENTS WHERE NOT ((event_end <= ?) OR (event_start >= ?));");
		st.setTimestamp(1, new Timestamp(from.getTime()), Calendar.getInstance(DateTime.UTC));
		st.setTimestamp(2, new Timestamp(to.getTime()), Calendar.getInstance(DateTime.UTC));
		ResultSet rs = st.executeQuery();
		Table table = TableLoader.load(rs);
		
		rs.close();
		st.close();
		c.close();
		
		return table; 
	}
	
	public static Table getEvents(HttpServletRequest request) throws SQLException, JSONException, ClassNotFoundException {
		Timestamp start = new DateTime("2008-01-01T00:00:00").toTimeStamp();
		Timestamp end = new DateTime("2010-01-01T00:00:00").toTimeStamp();
		return getEvents(request, start, end);
	}
	

	public static void createTable(HttpServletRequest request) throws SQLException, ClassNotFoundException {
		Class.forName("org.mysql.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "root", "root");
		Statement st = c.createStatement();
		st.execute("CREATE TABLE EVENTS (event_id VARCHAR(200), event_name VARCHAR(200), event_start TIMESTAMP, event_end TIMESTAMP, event_resource VARCHAR(200), event_allday BOOLEAN, event_recurrence VARCHAR(2000), event_join VARCHAR(200) );");
		st.close();
		c.close();
		
		createSampleEvents(request);
	}
	
	public static void createSampleEvents(HttpServletRequest request) throws ClassNotFoundException, SQLException {
		insertEvent(request, "Event 1", new DateTime().toTimeStamp(), new DateTime().addMinutes(30).toTimeStamp(), "A.1", false, "1");
		insertEvent(request, "Event 2", new DateTime().addDays(1).toTimeStamp(), new DateTime().addDays(1).addMinutes(30).toTimeStamp(), "B", false, "1");
		insertEvent(request, "Event 3", new DateTime().addDays(-1).toTimeStamp(), new DateTime().addDays(-1).addHours(3).toTimeStamp(), "C", false, "2");
		insertEvent(request, "Event 4", new DateTime().getDatePart().addHours(11).toTimeStamp(), new DateTime().getDatePart().addHours(14).toTimeStamp(), "D", false, "2");
		insertEvent(request, "Event 5", new DateTime().toTimeStamp(), new DateTime().addDays(8).toTimeStamp(), "D", true, null);

		createRecurringEvent(request, "Event 6", DateTime.today(), DateTime.today().addDays(1), "E", "daily");
		
		createRecurringEvent(request, "Event 7", DateTime.today(), DateTime.today().addDays(1), "F", "weekly");
		
	}
	
	private static void createRecurringEvent(HttpServletRequest request, String name, DateTime start, DateTime end, String resource, String repeat) throws ClassNotFoundException, SQLException {
		String id = insertEvent(request, name, start.toTimeStamp(), end.toTimeStamp(), resource, true, null);
		RecurrenceRule rule = RecurrenceRule.fromDateTime(id, start).indefinitely();
		if ("daily".equals(repeat)) {
			rule.daily();
		}
		else if ("weekly".equals(repeat)) {
			rule.weekly();
		}
		updateEventRecurrence(request, id, rule.encode());
		
		if ("daily".equals(repeat)) {  // create a sample exception
			String eid1 = insertEvent(request, name, start.addDays(1).toTimeStamp(), end.addDays(1).toTimeStamp(), resource, true, null);
			String estring1 = RecurrenceRule.encodeExceptionDeleted(id, start.addDays(1));
			updateEventRecurrence(request, eid1, estring1);

			String eid2 = insertEvent(request, "ex: " + name, start.addDays(2).toTimeStamp(), end.addDays(2).toTimeStamp(), resource, true, null);
			String estring2 = RecurrenceRule.encodeExceptionModified(id, start.addDays(2));
			updateEventRecurrence(request, eid2, estring2);

		}
		
	}

	public static String insertEvent(HttpServletRequest request, String name, Date start, Date end, String resource, boolean allday) throws ClassNotFoundException, SQLException {
		return insertEvent(request, name, start, end, resource, allday, null);
	}

	public static String insertEvent(HttpServletRequest request, String name, Date start, Date end, String resource, boolean allday, String join) throws ClassNotFoundException, SQLException {
		String id = UUID.randomUUID().toString();
		
		Class.forName("org.mysql.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "root", "root");
		PreparedStatement st = c.prepareStatement("INSERT INTO EVENTS (event_id, event_name, event_start, event_end, event_resource, event_allday, event_join) VALUES (?, ?, ?, ?, ?, ?, ?);");
		st.setString(1, id);
		st.setString(2, name);
		st.setTimestamp(3, new Timestamp(start.getTime()), Calendar.getInstance(DateTime.UTC));
		st.setTimestamp(4, new Timestamp(end.getTime()), Calendar.getInstance(DateTime.UTC));
		st.setString(5, resource);
		st.setBoolean(6, allday);
		st.setString(7, join);
		st.execute();
		st.close();
		c.close();
		
		return id;
	}
	
	public static void deleteEvent(HttpServletRequest request, String id) throws ClassNotFoundException, SQLException {
		Class.forName("org.mysql.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "root", "root");
		PreparedStatement st = c.prepareStatement("DELETE FROM events WHERE event_id = ?;");
		st.setString(1, id);
		st.execute();
		st.close();
		c.close();
	}


	public static boolean tableExists(HttpServletRequest request, String name) throws SQLException, JSONException, ClassNotFoundException {
		Class.forName("org.mysql.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "root", "root");
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery("SELECT count(*) FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE table_name = '" + name.toUpperCase() + "'");

		boolean result = false;
		if (rs.next()) {
			int count = rs.getInt(1);
			result = count == 1;
		}
		
		rs.close();
		st.close();
		c.close();
		
		return result;
	}


	public static void moveEvent(HttpServletRequest request, String id, Timestamp start,
			Timestamp end, String resource) throws ClassNotFoundException, SQLException {
		Class.forName("org.mysql.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "root", "root");
		PreparedStatement st = c.prepareStatement("UPDATE EVENTS SET event_start = ?, event_end = ?, event_resource = ? WHERE event_id = ?;");
		st.setTimestamp(1, start, Calendar.getInstance(DateTime.UTC));
		st.setTimestamp(2, end, Calendar.getInstance(DateTime.UTC));
		st.setString(3, resource);
		st.setString(4, id);
		st.execute();
		st.close();
		c.close();
	}

	public static void updateEventRecurrence(HttpServletRequest request, String id, String recurrence) throws ClassNotFoundException, SQLException {
		Class.forName("org.mysql.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "root", "root");
		PreparedStatement st = c.prepareStatement("UPDATE EVENTS SET event_recurrence = ? WHERE event_id = ?;");
		st.setString(1, recurrence);
		st.setString(2, id);
		st.execute();
		st.close();
		c.close();
	}

	public static void resizeEvent(HttpServletRequest request, String id, Timestamp start,
			Timestamp end) throws ClassNotFoundException, SQLException {
		Class.forName("org.mysql.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "root", "root");
		PreparedStatement st = c.prepareStatement("UPDATE EVENTS SET event_start = ?, event_end = ? WHERE event_id = ?;");
		st.setTimestamp(1, start, Calendar.getInstance(DateTime.UTC));
		st.setTimestamp(2, end, Calendar.getInstance(DateTime.UTC));
		st.setString(3, id);
		st.execute();
		st.close();
		c.close();
	}
	
	private static String getConnectionString(HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		
		if (request.getSession().getAttribute("cs") == null) {
			ServletContext context = session.getServletContext();
			String path = context.getRealPath("/WEB-INF/data/" + new DateTime().toDateString()); 
			
			try {
				new File(path).mkdirs();
			}
			catch (Exception e) { // unable to create the on-disk database
				path = null;
			}
			
			String cs;
			if (path == null) {
				cs = "jdbc:mysql:mem:daypilot";
			}
			else {
				cs = "jdbc:mysql:file:" + path + "/" + session.getId();
			}
			
			session.setAttribute("cs", cs);
			
		}
		
		String cs = (String) session.getAttribute("cs"); 
		return cs;
	}

	public static void updateEventText(HttpServletRequest request,
			String value, String newText) throws SQLException, ClassNotFoundException {
		Class.forName("org.mysql.jdbcDriver" );
		Connection c = DriverManager.getConnection(getConnectionString(request), "root", "root");
		PreparedStatement st = c.prepareStatement("UPDATE EVENTS SET event_name = ? WHERE event_id = ?;");
		st.setString(1, newText);
		st.setString(2, value);
		st.execute();
		st.close();
		c.close();
	}

	public static Row getEvent(HttpServletRequest request, String id) {
		try {
			Class.forName("org.mysql.jdbcDriver" );
			Connection c = DriverManager.getConnection(getConnectionString(request), "root", "root");
	
			PreparedStatement st = c.prepareStatement("SELECT event_id, event_name, event_start, event_end, event_resource, event_allday, event_recurrence, event_join FROM EVENTS WHERE event_id = ?;");
			st.setString(1, id);
			ResultSet rs = st.executeQuery();
			Table table = TableLoader.load(rs);
			
			rs.close();
			st.close();
			c.close();
	
			if (table.size() > 0) {
				return table.get(0);
			}
			return null;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void updateEvent(HttpServletRequest request, String id, String name,
			Timestamp start, Timestamp end, String resource) {

		try {
			Class.forName("org.mysql.jdbcDriver" );
			Connection c = DriverManager.getConnection(getConnectionString(request), "root", "root");
			PreparedStatement st = c.prepareStatement("UPDATE EVENTS SET event_start = ?, event_end = ?, event_resource = ?, event_name = ? WHERE event_id = ?;");
			st.setTimestamp(1, start, Calendar.getInstance(DateTime.UTC));
			st.setTimestamp(2, end, Calendar.getInstance(DateTime.UTC));
			st.setString(3, resource);
			st.setString(4, name);
			st.setString(5, id);
			st.execute();
			st.close();
			c.close();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

}
