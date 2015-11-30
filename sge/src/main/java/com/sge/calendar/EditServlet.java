package com.sge.calendar;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.daypilot.date.DateTime;
import com.sge.calendar.db.Db;
import com.sge.calendar.db.Row;

public class EditServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
	
		String id = request.getParameter("id");
		
		Row r = Db.getEvent(request, id);

		request.setAttribute("name", r.get("event_name"));
		request.setAttribute("start", DateTime.parseAsLocal(r.get("event_start")));
		request.setAttribute("end", DateTime.parseAsLocal(r.get("event_end")));
		request.setAttribute("resource", r.get("event_resource"));
		
		request.getRequestDispatcher("/WEB-INF/jsp/Edit.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String id = request.getParameter("id");

		String name = request.getParameter("new_name");
		DateTime start = DateTime.parseString(request.getParameter("new_start"));
		DateTime end = DateTime.parseString(request.getParameter("new_end"));
		String resource = request.getParameter("new_resource");
		
		try {
			Db.updateEvent(request, id, name, start.toTimeStamp(), end.toTimeStamp(), resource);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		response.getOutputStream().print("'OK'");
		response.getOutputStream().close();
		
	}
	
}
