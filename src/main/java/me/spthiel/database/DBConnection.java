package me.spthiel.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import me.spthiel.Config;

public class DBConnection {
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		
		Connection connection = DriverManager.getConnection(
			"jdbc:mariadb://" + Config.DBHost + "/" + Config.DBDatabase, Config.DBUser, "root"
		);
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("CREATE TABLE a (id int not null primary key, value varchar(20))");
		stmt.close();
		connection.close();
	}
	
	public void registerTable(Table table) {
	
	}
	
}
