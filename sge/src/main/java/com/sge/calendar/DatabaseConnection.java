package com.sge.calendar;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

	public  Connection getConnection(){
		
		Connection con = null;
		try{
			String url = "jdbc:mysql://localhost:3306/dbsge";
			String user = "root";
			String password = "root";
			con = DriverManager.getConnection(url,user,password);
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());	
		}catch(Exception e){
			e.printStackTrace();
		}	
		return con;
	}
}
