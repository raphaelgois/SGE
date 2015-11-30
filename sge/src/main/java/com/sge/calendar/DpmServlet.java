/* Copyright © 2005 - 2014 Annpoint, s.r.o.
   Use of this software is subject to license terms. 
   http://www.daypilot.org/
*/
package com.sge.calendar;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.daypilot.data.Area;
import org.daypilot.data.AreaVisibility;
import org.daypilot.date.DateTime;
import com.sge.calendar.db.Db;
import org.daypilot.ui.DayPilotMonth;
import org.daypilot.ui.args.month.BeforeCellRenderArgs;
import org.daypilot.ui.args.month.BeforeEventRenderArgs;
import org.daypilot.ui.args.month.CommandArgs;
import org.daypilot.ui.args.month.EventMenuClickArgs;
import org.daypilot.ui.args.month.EventMoveArgs;
import org.daypilot.ui.args.month.EventResizeArgs;
import org.daypilot.ui.args.month.TimeRangeSelectedArgs;
import org.daypilot.ui.enums.TimeFormat;
import org.daypilot.ui.enums.UpdateType;
import org.daypilot.ui.enums.WeekStarts;
import org.daypilot.util.LocaleParser;

public class DpmServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		new Dpm().process(request, response);
	}
	
	public class Dpm extends DayPilotMonth {

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
			Db.moveEvent(getRequest(), ea.getValue(), ea.getNewStart().toTimeStamp(), ea.getNewEnd().toTimeStamp(), null);
			update();
			
			if ("dpm_position".equals(getId())) {
				updateWithMessage("Event moved to position: " + ea.getPosition());
			}
		}
		
		@Override
		public void onEventResize(EventResizeArgs ea) throws Exception {
			Db.resizeEvent(getRequest(), ea.getValue(), ea.getNewStart().toTimeStamp(), ea.getNewEnd().toTimeStamp());
			update();
		}
		
		@Override
		public void onTimeRangeSelected(TimeRangeSelectedArgs ea) throws Exception {
			Db.insertEvent(getRequest(), "New event", ea.getStart().toTimeStamp(), ea.getEnd().toTimeStamp(), null, false);
			update();
		}
		
		@Override
		public void onCommand(CommandArgs ea) throws Exception {
			if ("next".equals(ea.getCommand())) {
				setStartDate(getStartDate().addMonths(1));
				update(UpdateType.FULL);
			}
			else if ("previous".equals(ea.getCommand())) {
				setStartDate(getStartDate().addMonths(-1));
				update(UpdateType.FULL);
			}
			else if ("this".equals(ea.getCommand())) {
				setStartDate(DateTime.getToday());
				update(UpdateType.FULL);
			}
			else if ("locale".equals(ea.getCommand())) {
				setLocale(LocaleParser.parse(ea.getData().getString("value")));
				setTimeFormat(TimeFormat.AUTO);
				setWeekStarts(WeekStarts.AUTO);
				update(UpdateType.FULL);
			}
			else if ("navigate".equals(ea.getCommand())) {
				setStartDate(ea.getData().getDateTime("start"));
				update(UpdateType.FULL);
			}
			else if ("delete".equals(ea.getCommand())) {
				String id = ea.getData().getString("id");
				Db.deleteEvent(getRequest(), id);
				update(UpdateType.EVENTS_ONLY);
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
		public void onInit() throws Exception {
			setWeekStarts(WeekStarts.AUTO);
			update(UpdateType.FULL);
		}
		
		@Override
		public void onBeforeEventRender(BeforeEventRenderArgs ea) {
			if ("dpm_customrendering".equals(getId())) {
				ea.setBackgroundColor("lightgreen");
			}
			if ("dpm_areas".equals(getId())) {
                ea.getAreas().add(new Area().width(17).bottom(9).right(2).top(3).cssClass("event_action_delete").javaScript("dpm.commandCallBack('delete', {id:e.value() });"));
                ea.getAreas().add(new Area().width(17).bottom(9).right(19).top(3).cssClass("event_action_menu").contextMenu("menu"));
			}
			
			if (ea.isRecurrent()) {
				ea.setBackgroundColor("lightyellow");
				ea.setInnerHTML(ea.getInnerHTML() + " (R)");
			}

		}
		
		@Override
		public void onBeforeCellRender(BeforeCellRenderArgs ea) {
		}
		
		@Override
		public void onFinish() throws Exception {

			// only load events if update was requested
			if ( getUpdateType()== UpdateType.NONE) {
				return;
			}
			
			// set the database fields
			setDataValueField("event_id");
			setDataTextField("event_name");
			setDataStartField("event_start");
			setDataEndField("event_end");
			
			if ("dpm_recurring".equals(getId())) {
				setDataRecurrenceField("event_recurrence");
			}

			
			// reload events
			setEvents(Db.getEvents(getRequest(), getVisibleStart().toDate(), getVisibleEnd().toDate()));
			
		}
	}
}
