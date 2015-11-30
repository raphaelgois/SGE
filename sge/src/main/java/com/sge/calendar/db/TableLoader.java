package com.sge.calendar.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class TableLoader {

	public static Table load(ResultSet rs) throws SQLException{
		return load(rs, -1, -1);
	}
	
	public static Table load(ResultSet rs, int startRow, int maxRows) throws SQLException 
    {
    	Table table = new Table();

        ResultSetMetaData rsmd = rs.getMetaData();
        int noOfColumns = rsmd.getColumnCount();

        // Create the column name array
        String[] columnNames = new String[noOfColumns];
        for (int i = 1; i <= noOfColumns; i++) {
            columnNames[i-1] = rsmd.getColumnName(i);
        }

        // Throw away all rows upto startRow
        for (int i = 0; i < startRow; i++) {
            rs.next();
        }

        // Process the remaining rows upto maxRows
        int processedRows = 0;
        while (rs.next()) {
            if ((maxRows != -1) && (processedRows == maxRows)) {
                //isLimited = true; 
                break;
            }
            Row row = new Row();

            // JDBC uses 1 as the lowest index!
            for (int i = 1; i <= noOfColumns; i++) {
                Object value =  rs.getObject(i);
                if (rs.wasNull()) {
                    value = null;
                }
                row.put(columnNames[i-1], value);
            }
            table.add(row);
            processedRows++;
        }
        
        return table;
    }


}


