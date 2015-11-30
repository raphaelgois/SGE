package com.sge.calendar;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.daypilot.data.Area;
import org.daypilot.data.Resource;
import org.daypilot.data.ResourceColumn;
import org.daypilot.date.DateTime;
import org.daypilot.date.DayOfWeek;
import org.daypilot.date.Week;
import com.sge.calendar.db.Db;
import com.sge.calendar.db.Row;
import com.sge.calendar.db.Table;
import org.daypilot.ui.DayPilotScheduler;
import org.daypilot.ui.args.scheduler.BeforeCellRenderArgs;
import org.daypilot.ui.args.scheduler.BeforeEventRenderArgs;
import org.daypilot.ui.args.scheduler.BeforeResHeaderRenderArgs;
import org.daypilot.ui.args.scheduler.BeforeTimeHeaderRenderArgs;
import org.daypilot.ui.args.scheduler.CommandArgs;
import org.daypilot.ui.args.scheduler.EventBubbleArgs;
import org.daypilot.ui.args.scheduler.EventMenuClickArgs;
import org.daypilot.ui.args.scheduler.EventMoveArgs;
import org.daypilot.ui.args.scheduler.EventResizeArgs;
import org.daypilot.ui.args.scheduler.ResourceBubbleArgs;
import org.daypilot.ui.args.scheduler.TimeRangeSelectedArgs;
import org.daypilot.ui.enums.UpdateType;

public class DpsServlet extends HttpServlet {
	
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
			setTimeZone(DateTime.UTC);
			
			if ("dps_navigator".equals(getId())) {
				setStartDate(Week.firstDayOfWeek(DateTime.getToday(), getResolvedWeekStart()));
			}
			else if ("dps_timezone".equals(getId())) {
				DateTime start = Week.firstDayOfWeek(DateTime.getToday(getTimeZone()), getResolvedWeekStart());
				setStartDate(start);
			}
			else if ("dps_timeline".equals(getId())) {
				
				getTimeline().clear();
				DateTime start = new DateTime("2015-02-09T05:00:00");
				for (int i = 0; i < 24; i++) {
					getTimeline().add(start.addHours(i*4), start.addHours((i+1)*4));
				}
				
				TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
				setTimeZone(zone);
			}
			else {
				setStartDate(DateTime.getToday().firstDayOfYear());
			}
			setScrollX(DateTime.getToday());
						
			update(UpdateType.FULL);
		}
		
		private void loadResources() {
			getResources().clear();
			Resource r = getResources().add("Room A", "A");
			r.getChildren().add("Room A.1", "A.1");
			
			if (!"dps_autofit".equals(getId())) {
				r.setExpanded(true);
			}
			
			getResources().add("Room B", "B");
			getResources().add("Room C", "C");
			getResources().add("Room D", "D");
			getResources().add("Room E", "E");
			getResources().add("Room F", "F");
			getResources().add("Room G", "G");
			getResources().add("Room H", "H").getColumns().add(new ResourceColumn("Test"));
			
		}

		@Override
		public void onPrepare() throws Exception {
			// create the in-memory DB if it's not ready
			if (!Db.tableExists(getRequest(), "EVENTS")) {
				Db.createTable(getRequest());
			}
		}
		
		@Override
		public void onEventMove(EventMoveArgs ea) throws Exception {
			
			// update the DB
			if ("dps_joint".equals(getId())) {  // joint events/multimove, update all moved events
				for (EventMoveArgs item : ea.getMultimove()) {
					Db.moveEvent(getRequest(), item.getValue(), item.getNewStart().toTimeStamp(), item.getNewEnd().toTimeStamp(), item.getNewResource());					
				}				
			}
			else {
				Db.moveEvent(getRequest(), ea.getValue(), ea.getNewStart().toTimeStamp(), ea.getNewEnd().toTimeStamp(), ea.getNewResource());
			}

			update();
			
			if ("dps_position".equals(getId())) {
				updateWithMessage("Event moved to position: " + ea.getPosition(), UpdateType.EVENTS_ONLY);
			}
		}
		
		@Override
		public void onEventResize(EventResizeArgs ea) throws Exception {
			Db.resizeEvent(getRequest(), ea.getValue(), ea.getNewStart().toTimeStamp(), ea.getNewEnd().toTimeStamp());
			update();
		}
		
		@Override
		public void onTimeRangeSelected(TimeRangeSelectedArgs ea) throws Exception {
			Db.insertEvent(getRequest(), "New event", ea.getStart().toTimeStamp(), ea.getEnd().toTimeStamp(), ea.getResource(), false);
			update();
		}
		
		@Override
		public void onBeforeTimeHeaderRender(BeforeTimeHeaderRenderArgs ea) throws Exception {
			//ea.setInnerHTML("Week " + Week.weekNrISO8601(ea.getStart()));
			if ("dps_timeline".equals(getId())) {
				if (ea.getLevel() == 1) {
					ea.setInnerHTML("" + ea.getStart().getHour());
				}
			}
		}
		
		@Override
		public void onEventMenuClick(EventMenuClickArgs ea) throws Exception {
			if (ea.commandEquals("delete")) {
				Db.deleteEvent(getRequest(), ea.getValue());
			}
			update();
		}
		
		@Override
		public void onEventBubble(EventBubbleArgs ea) throws Exception {
			StringBuilder sb = new StringBuilder();
			sb.append("Event details loaded from the server.");
			sb.append("<br/>");
			sb.append("Start: ");
			sb.append(ea.getStart().tz(getTimeZone()));
			sb.append("<br/>");
			sb.append("End: ");
			sb.append(ea.getEnd().tz(getTimeZone()));
			if (getTimeZone() != null) {
				sb.append("<br/>");
				sb.append("TimeZone: ");
				sb.append(getTimeZone().getDisplayName());
			}
			ea.setBubbleHTML(sb.toString());
		}

		@Override
		public void onResourceBubble(ResourceBubbleArgs ea) throws Exception {
			ea.setBubbleHTML("Resource details loaded from the server. " + ea.getId());
		}
		
		@Override
		public void onCommand(CommandArgs ea) throws Exception {
			if ("previous".equals(ea.getCommand())) {
				setStartDate(getStartDate().addYears(-1));
				setDays(getStartDate().getDaysOfYear());
				update(UpdateType.FULL);
			}
			else if ("next".equals(ea.getCommand())) {
				setStartDate(getStartDate().addYears(1));
				setDays(getStartDate().getDaysOfYear());
				update(UpdateType.FULL);
			}
			else if ("this".equals(ea.getCommand())) {
				setStartDate(DateTime.getToday().firstDayOfYear());
				setDays(getStartDate().getDaysOfYear());
				update(UpdateType.FULL);
			}
			else if ("refresh".equals(ea.getCommand())) {
				updateWithMessage("Refreshed.", UpdateType.EVENTS_ONLY);
			}
			else if ("timezone".equals(ea.getCommand())) {
				TimeZone newZone = TimeZone.getTimeZone(ea.getData().getString("zone"));
				DateTime local = getStartDate().inZoneByName(newZone);
				
				setStartDate(local);
				setTimeZone(newZone);
				
				updateWithMessage("Time zone set.", UpdateType.FULL);
			}
			else if ("navigate".equals(ea.getCommand())) {
				DateTime start = DateTime.parseString(ea.getData().getString("start"));
				TimeZone tz = getTimeZone();
				start = start.inZoneByName(tz);
				setStartDate(start);
				setScrollX(0);
				update(UpdateType.FULL);
			}
			else if ("delete".equals(ea.getCommand())) {
				Db.deleteEvent(getRequest(), ea.getData().getString("id"));
				update(UpdateType.EVENTS_ONLY);
			}
		}
		
		@Override
		public void onBeforeResHeaderRender(BeforeResHeaderRenderArgs ea) {
			if (ea.isCorner()) {
				String c = String.format("<div style='padding:5px; font-weight: bold; font-size:22px; text-align:center'>%s</div>", getStartDate().getYear());
				ea.setInnerHTML(c);
			}
			if ("dps_areas".equals(getId())) {
				ea.getAreas().add(new Area().width(17).bottom(1).right(0).top(0).cssClass("resource_action_menu").html("<div><div></div></div>").javaScript("alert(e.Value);"));
			}
		}
		
		@Override
		public void onBeforeCellRender(BeforeCellRenderArgs ea) {
			if ("dps_highlightingrow".equals(getId())) {
				if ("B".equals(ea.getResourceId())) {
					if (ea.isBusiness()) {
						ea.setBackgroundColor("#ffaaaa");
					}
					else {
						ea.setBackgroundColor("#ff6666");
					}
				}
			}
			if ("dps_highlightingtoday".equals(getId())) {
				if (ea.getStart().getDatePart().equals(DateTime.getToday())) {
					if (ea.isBusiness()) {
						ea.setBackgroundColor("#ffaaaa");
					}
					else {
						ea.setBackgroundColor("#ff6666");
					}
				}
			}
			if ("dps_parents".equals(getId())) {
				Resource r = getResources().findById(ea.getResourceId());
				if (r.getChildren().size() > 0) {
					ea.setBackgroundColor("#ffffff");
				}
			}
			
			if ("dps_cells".equals(getId())) {
				if (ea.getStart().getDayOfWeek() == DayOfWeek.SUNDAY || ea.getStart().getDayOfWeek() == DayOfWeek.SATURDAY) {
					ea.setBackgroundColor("#dddddd");
				}
			}
		}
		
		
		@Override
		public void onBeforeEventRender(BeforeEventRenderArgs ea) {
			if ("dps_limit".equals(getId())) {
				try {
					String first = ea.getValue().substring(0, 2);
	                int id = Integer.parseInt(first, 16);
	
	                if (id % 2 == 0)
	                {
	                    ea.setDurationBarColor("red");
	                    ea.setEventMoveVerticalEnabled(false);
	                }
	                else
	                {
	                    ea.setDurationBarColor("blue");
	                    ea.setEventMoveHorizontalEnabled(false);
	                }
				}
				catch (Exception e) {
					// skip this event
				}
			}
			
			if ("dps_areas".equals(getId())) {
				ea.getAreas().add(new Area().width(17).bottom(9).right(2).top(3).cssClass("event_action_delete").javaScript("dps.commandCallBack('delete', {id:e.value() });"));
				ea.getAreas().add(new Area().width(17).bottom(9).right(19).top(3).cssClass("event_action_menu").contextMenu("cmSpecial"));
			}
			
			if ("dps_complete".equals(getId())) {
				int complete = new SecureRandom().nextInt(100);
                ea.setPercentComplete(complete);

                String cs = String.format("%s%%", complete);
                ea.setInnerHTML(cs);
			}
			
			if (ea.isRecurrent()) {
				ea.setBackgroundColor("lightyellow");
				ea.setInnerHTML(ea.getInnerHTML() + " (R)");
			}

			
		}
		
		@Override
		public void onScroll() throws Exception {
			update(UpdateType.EVENTS_ONLY);
		}
		
	
		@Override
		public void onFinish() throws Exception {
			
			if (getUpdateType() == UpdateType.NONE) {
				return;
			}
			
			if (getUpdateType() == UpdateType.FULL) {
				loadResources();
			}
			
			// set the database fields
			setDataResourceField("event_resource");
			setDataIdField("event_id");
			setDataTextField("event_name");
			setDataStartField("event_start");
			setDataEndField("event_end");
			
			if ("dps_recurring".equals(getId())) {
				setDataRecurrenceField("event_recurrence");
			}
			
			if (("dps_joint").equals(getId())) {
				setDataJoinField("event_join");
			}			
			
			// reload events
			Table events = Db.getEvents(getRequest(), getStartDate().toDate(), getStartDate().addDays(getDays()).toDate()); 
			setEvents(events);
			
			// links
			if ("dps_links".equals(getId())) {
				Row r1 = events.get(0);
				Row r2 = events.get(1);
				
				if (r1 != null && r2 != null) {
					getLinks().clear();
					getLinks().add((String)r1.get("event_id"), (String)r2.get("event_id"));
				}				
			}

			if (getUpdateType() == UpdateType.EVENTS_ONLY) {
				return;
			}
			
			
		}
		
	}
}
