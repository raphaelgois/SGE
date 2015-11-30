/* Copyright © 2005 - 2014 Annpoint, s.r.o.
   Use of this software is subject to license terms. 
   http://www.daypilot.org/
*/

package com.sge.calendar.db;

import java.sql.Timestamp;
import java.util.TreeMap;

import org.daypilot.date.DateTime;

public class Row extends TreeMap<String, Object> {
	private static final long serialVersionUID = -2826532351751710037L;

	public Row() {
		super(String.CASE_INSENSITIVE_ORDER);
	}
	
	public String str(String field) {
		return (String) get(field);
	}
	
	public DateTime dateTime(String field) {
		return new DateTime((Timestamp)get(field));
	}
	
	public boolean isEmpty(String field) {
		if (isNull(field)) {
			return true;
		}
		if (get(field).toString().trim().equals("")) {
			return true;
		}
		return false;
	}
	
	public boolean isNull(String field) {
		return get(field) == null;
	}
	
}
