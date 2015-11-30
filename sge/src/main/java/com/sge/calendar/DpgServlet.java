package com.sge.calendar;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.daypilot.date.DateTime;
import org.daypilot.date.TimeSpan;
import org.daypilot.ui.DayPilotGantt;
import org.daypilot.ui.args.gantt.BeforeTaskRenderArgs;
import org.daypilot.ui.args.gantt.RowCreateArgs;
import org.daypilot.ui.args.gantt.RowMoveArgs;

/**
 * Servlet implementation class DpgServlet
 */
public class DpgServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		new Dpg().process(request, response);
	}
	
	public class Dpg extends DayPilotGantt {

		@Override
		public void onInit() {
			getTasks().add("Task 1", "1", DateTime.today().addDays(1), DateTime.today().addDays(3));
			getTasks().add("Task 2", "2", DateTime.today().addDays(3), DateTime.today().addDays(6));
			
			getLinks().add("1", "2");
			
			setStartDate(DateTime.today());
			setDays(60);
			update();
		}
		

		@Override
		public void onRowMove(RowMoveArgs ea) {
			ea.getTarget().setExpanded(true);
			ea.move();
			update();
		}
		
		@Override
		public void onRowCreate(RowCreateArgs ea) {
			getTasks().add(ea.getText(), UUID.randomUUID().toString(), DateTime.today(), DateTime.today().addDays(1));
			update();
		}
		
		@Override
		public void onBeforeTaskRender(BeforeTaskRenderArgs ea) {
			if (getColumns().size() > 1) {
				TimeSpan duration = ea.getEnd().minus(ea.getStart());
				ea.getRow().getColumns().get(1).setHtml(duration.toString());
			}
		}
		
		
	
	}

}
