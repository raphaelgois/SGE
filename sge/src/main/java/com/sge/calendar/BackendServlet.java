
package com.sge.calendar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.daypilot.date.DateTime;
import org.daypilot.ui.DayPilotScheduler;
import org.daypilot.ui.enums.UpdateType;

public class BackendServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		new Dps().process(request, response);
	}
	
	public class Dps extends DayPilotScheduler {

		@Override
		public void onInit() throws Exception {
			setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
			
			DateTime start = new DateTime("2015-03-01");
			for (int i = 0; i < 24; i++) {
				getTimeline().add(start.addHours(i), start.addHours(i+1));
			}
			
			loadResources();
			loadEvents();
			
			update(UpdateType.FULL);
		}
		
		private void loadResources() {
			getResources().clear();
			getResources().add("Room A", "A");
			getResources().add("Room B", "B");
			getResources().add("Room C", "C");
			getResources().add("Room D", "D");
			getResources().add("Room E", "E");
			getResources().add("Room F", "F");
			getResources().add("Room G", "G");
			getResources().add("Room H", "H");
			
		}
		
		private void loadEvents() {

			setDataResourceField("resource");
			setDataIdField("id");
			setDataTextField("text");
			setDataStartField("start");
			setDataEndField("end");
			
			List<Event> events = new ArrayList<Event>();
			
			events.add(new Event("2015-03-01T12:00:00", "2015-03-01T14:00:00", "1", "A", "Event 1, 12-14 UTC" ));
			
			setEvents(events);

		}
		
		public class Event {
			private String start;
			private String end;
			private String id;
			private String resource;
			private String text;
			
			
			private Event(String start, String end, String id, String resource,
					String text) {
				this.start = start;
				this.end = end;
				this.id = id;
				this.resource = resource;
				this.text = text;
			}
			
			public String getId() {
				return id;
			}
			public void setId(String id) {
				this.id = id;
			}
			public String getResource() {
				return resource;
			}
			public void setResource(String resource) {
				this.resource = resource;
			}
			public String getText() {
				return text;
			}
			public void setText(String text) {
				this.text = text;
			}
			public String getStart() {
				return start;
			}
			public void setStart(String start) {
				this.start = start;
			}
			public String getEnd() {
				return end;
			}
			public void setEnd(String end) {
				this.end = end;
			}
		}

		
		
	}

}
