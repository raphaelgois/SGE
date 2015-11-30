package com.sge.calendar;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.daypilot.date.DateTime;
import com.sge.calendar.db.Db;

public class NewServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		DateTime start = DateTime.parseString(request.getParameter("start"));
		DateTime end = DateTime.parseString(request.getParameter("end"));
		String resource = request.getParameter("res");
		
		request.setAttribute("start", start);
		request.setAttribute("end", end);
		request.setAttribute("resource", resource);
		
		request.getRequestDispatcher("/WEB-INF/jsp/New.jsp").forward(request, response);
		
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String name = request.getParameter("new_name");
		DateTime start = DateTime.parseString(request.getParameter("new_start"));
		DateTime end = DateTime.parseString(request.getParameter("new_end"));
		String resource = request.getParameter("new_resource");
		
		try {
			Db.insertEvent(request, name, start.toTimeStamp(), end.toTimeStamp(), resource, false);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		response.getOutputStream().print("'OK'");
		response.getOutputStream().close();
		
	}
	
}
