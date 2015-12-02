package com.sge.calendar;

import java.sql.Connection;
import java.sql.DriverManager;
 
import com.dhtmlx.connector.ConnectorServlet;
import com.dhtmlx.connector.DBType;
import com.dhtmlx.connector.SchedulerConnector;
import com.dhtmlx.connector.ThreadSafeConnectorServlet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 
/**
 * The Class BasicConnector.
*/
@WebServlet(name = "DataServlet")
public class BasicConnector extends ThreadSafeConnectorServlet {
    @Override
    protected void configure(HttpServletRequest req, HttpServletResponse res) {
        Connection con= ( new DatabaseConnection()).getConnection();
      SchedulerConnector c = new SchedulerConnector(con);
      c.servlet(req, res);
      c.render_table("escala", "escala_id", "start_date,end_date,escala_name,aprovacao");
    }

    
}