package me.spthiel.database;

import java.sql.SQLException;

public interface DBConnector {
	
	void setupTables() throws SQLException;
	void setup();
	
}
