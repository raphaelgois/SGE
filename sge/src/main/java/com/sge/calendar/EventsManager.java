
package com.sge.calendar;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import com.dhtmlx.planner.DHXEv.*;
import com.dhtmlx.planner.DHXEvent;
import com.dhtmlx.planner.DHXEventsManager;
import com.dhtmlx.planner.DHXStatus;
import com.sge.calendar.DatabaseConnection;
import com.dhtmlx.planner.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;


public class EventsManager extends DHXEventsManager{
    public EventsManager (HttpServletRequest request){
        super(request);
    }
   
    public Iterable<DHXEv>getEvents(){
       DHXEventsManager.date_format = "yyyy-MM-dd HH:mm:ss";
       List <DHXEv> evs = new ArrayList<DHXEv>();
       try {
           java.sql.Connection conn = DatabaseConnection.getConnection();
           java.sql.Statement statement = conn.createStatement();
           String query = "SELECT escala_id, escala_nome, data_inicio, data_fim FROM escalas";
           ResultSet resultset = statement.executeQuery(query);
           while (resultset.next()) {
               DHXEvent e = new DHXEvent();
               e.setId(Integer.parseInt((resultset.getString("escala_id"))));
               e.setText(resultset.getString("escala_nome"));
               e.setStart_date(resultset.getString("data_inicio"));
               e.setEnd_date(resultset.getString("data_fim"));
               evs.add(e);
           }
           conn.close();
       } catch (SQLException e1){
           e1.printStackTrace();
       }
       DHXEventsManager.date_format = "MM/dd/yyyy HH:mm";
       return evs;
    }

    @Override
    public DHXStatus saveEvent(DHXEv event, DHXStatus status){
        java.sql.Connection conn = DatabaseConnection.getConnection();
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet result = null;
        try {
            String query = null;
            String data_inicio = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(event.getStart_date());
            String data_fim = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(event.getEnd_date());
           // Profile profile = new Profile();
            //while (profile.getTipo().equals("user")){
            if (status ==DHXStatus.UPDATE) {
                query = "UPDATE escalas SET escala_nome=?, data_inicio=?, data_fim=? WHERE escala_id=?";
                ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, event.getText());
                ps.setString(2, data_inicio);
                ps.setString(3, data_fim);
                ps.setInt(4, event.getId());
            } else if (status ==DHXStatus.INSERT) {
                query = "INSERT INTO escalas (escala_id, escala_nome, data_inicio, data_fim) VALUES (null, ?, ?, ?)";
                ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, event.getText());
                ps.setString(2, data_inicio);
                ps.setString(3, data_fim);
            } else if (status == DHXStatus.DELETE){
                query = "DELETE FROM escalas WHERE escala_id=? LIMIT 1";
                ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, event.getId());
            }
            //}
            if (ps!=null){
                ps.executeUpdate();
                result = ps.getGeneratedKeys();
                if (result.next()){
                    event.setId(result.getInt(1));
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (result != null) try {result.close();} catch (SQLException e) {}
            if (ps != null) try {ps.close();} catch (SQLException e) {}
            if (conn != null) try {conn.close();} catch (SQLException e) {}
        }
        return status;
    }
    @Override
    public DHXEv createEvent (String id, DHXStatus status){
        return new DHXEvent();
    }
}