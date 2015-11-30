package com.sge.calendar;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.daypilot.data.Area;
import org.daypilot.data.Column;
import org.daypilot.date.DateTime;
import org.daypilot.date.Week;
import com.sge.calendar.db.Db;
import org.daypilot.ui.DayPilotCalendar;
import org.daypilot.ui.args.calendar.BeforeCellRenderArgs;
import org.daypilot.ui.args.calendar.BeforeEventRenderArgs;
import org.daypilot.ui.args.calendar.BeforeHeaderRenderArgs;
import org.daypilot.ui.args.calendar.CommandArgs;
import org.daypilot.ui.args.calendar.EventBubbleArgs;
import org.daypilot.ui.args.calendar.EventDeleteArgs;
import org.daypilot.ui.args.calendar.EventEditArgs;
import org.daypilot.ui.args.calendar.EventMenuClickArgs;
import org.daypilot.ui.args.calendar.EventMoveArgs;
import org.daypilot.ui.args.calendar.EventResizeArgs;
import org.daypilot.ui.args.calendar.TimeRangeSelectedArgs;
import org.daypilot.ui.enums.UpdateType;
import org.daypilot.ui.enums.ViewType;

public class DpcServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		new Dpc().process(request, response);
	}
	
	public class Dpc extends DayPilotCalendar {
		
		@Override
		public void onPrepare() throws Exception {
			// create the in-memory DB if it's not ready
			if (!Db.tableExists(getRequest(), "EVENTS")) {
				Db.createTable(getRequest());
			}
			
			setLocale(Locale.US);
		}
		
		@Override
		public void onEventMove(EventMoveArgs ea) throws Exception {
			// update the DB
			Db.moveEvent(getRequest(), ea.getValue(), ea.getNewStart().toTimeStamp(), ea.getNewEnd().toTimeStamp(), ea.getNewResource());
			update();
		}
		
		@Override
		public void onEventResize(EventResizeArgs ea) throws Exception {
			Db.resizeEvent(getRequest(), ea.getValue(), ea.getNewStart().toTimeStamp(), ea.getNewEnd().toTimeStamp());
			update();
		}
		
		@Override
		public void onTimeRangeSelected(TimeRangeSelectedArgs ea) throws Exception {
			Db.insertEvent(getRequest(), "New event", ea.getStart().toDate(), ea.getEnd().toDate(), ea.getResource(), false);
			update();
		}
		
		@Override
		public void onEventDelete(EventDeleteArgs ea) throws Exception {
			Db.deleteEvent(getRequest(), ea.getValue());
			update();
		}
		
		@Override
		public void onEventEdit(EventEditArgs ea ) throws Exception {
			Db.updateEventText(getRequest(), ea.getValue(), ea.getNewText());
			update();
		}
		
		@Override
		public void onInit() {
			if ("dpc_daysresources".equals(getId())) {
				defineColumnsForDaysResourcesJsp();
			}
			update(UpdateType.FULL);
		}
		
		@Override
		public void onEventBubble(EventBubbleArgs ea) {
			ea.setBubbleHTML("Hello from the server.");
		}
		
		@Override
		public void onCommand(CommandArgs ea) throws Exception {
			if ("navigate".equals(ea.getCommand())) {
				DateTime start = DateTime.parseString(ea.getData().getString("start"));
				
				start = Week.firstDayOfWeek(start, getFirstDayOfWeek());
				
				setStartDate(start);
				update(UpdateType.FULL);
			}
			else if ("day".equals(ea.getCommand())) {
				setViewType(ViewType.DAY);
				update(UpdateType.FULL);
			}
			else if ("week".equals(ea.getCommand())) {
				setViewType(ViewType.WEEK);
				update(UpdateType.FULL);
			}
			else if ("previous".equals(ea.getCommand())) {
				switch (getViewType()) {
					case DAY:
						setStartDate(getStartDate().addDays(-1));
						break;
					case WEEK:
						setStartDate(getStartDate().addDays(-7));
						break;
				}
				update(UpdateType.FULL);
			}
			else if ("next".equals(ea.getCommand())) {
				switch (getViewType()) {
					case DAY:
						setStartDate(getStartDate().addDays(1));
						break;
					case WEEK:
						setStartDate(getStartDate().addDays(7));
						break;
				}
				update(UpdateType.FULL);
				
			}
			else if ("today".equals(ea.getCommand())) {
				setStartDate(DateTime.getToday());
				update(UpdateType.FULL);
			}
			/*
			else if ("height".equals(ea.getCommand())) {
				setHeightSpec(HeightSpec.parse(ea.getData().getString("value")));
				update(UpdateType.FULL);
			}*/
		}
		
		@Override
		public void onEventMenuClick(EventMenuClickArgs ea) throws Exception {
			if (ea.commandEquals("delete")) {
				Db.deleteEvent(getRequest(), ea.getValue());
			}
			update();
		}
		
		@Override
		public void onFinish() throws Exception {

			// load the events only if update was requested
			if (getUpdateType() == UpdateType.NONE) {
				return;
			}
			
			// set the database fields
			setDataResourceField("event_resource");
			setDataValueField("event_id");
			setDataTextField("event_name");
			setDataStartField("event_start");
			setDataEndField("event_end");
			setDataAllDayField("event_allday");
			setDataTagFields("event_id, event_name");

			if ("dpc_recurring".equals(getId())) {
				setDataRecurrenceField("event_recurrence");
			}

			
			// reload events
			if (getViewType() == ViewType.RESOURCES) {
				// getVisibleStart() and getVisibleEnd() can't be used here
				setEvents(Db.getEvents(getRequest(), DateTime.MIN.toDate(), DateTime.MAX.toDate()));
			}
			else {
				setEvents(Db.getEvents(getRequest(), getStartDate().toDate(), getStartDate().addDays(getDays()).toDate()));
			}
			
		}
		
		@Override
		public void onBeforeEventRender(BeforeEventRenderArgs e) {
			e.setToolTip(null);
			
			if ("dpc_context_menu".equals(getId())) {
				e.setContextMenuClientName("alternateMenu");
			}
			
			if ("dpc_areas".equals(getId())) {
                e.setCssClass("calendar_white_event_withheader");

                e.getAreas().add(new Area().right(3).top(3).width(15).height(15).cssClass("event_action_delete").javaScript("dpc.eventDeleteCallBack(e);"));
                e.getAreas().add(new Area().right(20).top(3).width(15).height(15).cssClass("event_action_menu").javaScript("dpc.bubble.showEvent(e, true);"));
                e.getAreas().add(new Area().left(0).bottom(5).right(0).height(5).cssClass("event_action_bottomdrag").resizeEnd());
                e.getAreas().add(new Area().left(15).top(1).right(46).height(11).cssClass("event_action_move").move());

			}
			
			if (e.isRecurrent()) {
				e.setBackgroundColor("lightyellow");
				e.setInnerHTML(e.getInnerHTML() + " (R)");
			}
			
		}
		
		@Override
		public void onBeforeHeaderRender(BeforeHeaderRenderArgs ea) {
			if ("dpc_autofit".equals(getId())) {
				String html = ea.getInnerHTML() + " adding some longer text so the autofit can be tested";
				ea.setInnerHTML(html);
			}
			if ("dpc_areas".equals(getId())) {
				ea.getAreas().add(new Area().right(1).top(0).width(17).bottom(1).cssClass("resource_action_menu").html("<div><div></div></div>").javaScript("alert(e.date);"));
			}
		}
		
		@Override
		public void onBeforeCellRender(BeforeCellRenderArgs e) {
			if ("dpc_cells".equals(getId())) {
				if (e.getStart().getHour() <= 12) {
					e.setBackgroundColor("#ffdddd");
				}
				else {
					e.setBackgroundColor("#ffffdd");
				}
			}
		}
		
		
	    private void defineColumnsForDaysResourcesJsp()
	    {
	        getColumns().clear();

	        DateTime first = Week.firstDayOfWeek(getStartDate(), getFirstDayOfWeek());

	        for (int i = 0; i < 7; i++)
	        {
	            DateTime day = first.addDays(i);

	            Column c = new Column(day.toString("MMMM d, yyyy", getLocale()), day.toStringSortable());
	            c.setDate(day);
	            getColumns().add(c);

	            Column c1 = new Column("A", "A");
	            c1.setDate(day);
	            c.getChildren().add(c1);

	            Column c2 = new Column("B", "B");
	            c2.setDate(day);
	            c.getChildren().add(c2);

	            if (day.getDatePart().equals(DateTime.getToday()))
	            {
	                Column c3 = new Column("C", "C");
	                c3.setDate(day);
	                c.getChildren().add(c3);
	            }

	        }
	    }

		
	}
}
