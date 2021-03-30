package me.spthiel.database;

import me.spthiel.Config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
	
	public static void main(String[] args) throws SQLException {
		
		Connection connection = DriverManager.getConnection(
				"jdbc:mariadb://" + Config.DBHost + "/" + Config.DBDatabase, Config.DBUser, Config.DBPassword
		);
		
		//		Statement stmt = connection.createStatement();
		//		stmt.executeUpdate("CREATE TABLE a (id int not null primary key, value varchar(20))");
		//		stmt.close();
		//		connection.close();
		
		DBConnection db = new DBConnection();
		Connection con = db.getCon();
		
		Table asdf = new Table("asdf")
				.addField(new Table.Field("movie", Table.FieldType.VARCHAR, "20").setPrimary());
		
		Table suggestionsTable = new Table("suggestions")
				.addField(new Table.Field("userid", Table.FieldType.BIGINT).setPrimary())
				.addField(new Table.Field("suggestions", Table.FieldType.VARCHAR, "20").setForeign("asdf", "movie"));
		
		db.registerTable(asdf);
		db.registerTable(suggestionsTable);
		con.close();
	}
	
	public void registerTable(Table table) throws SQLException {
		Connection con = getCon();
		
		if (tableExists(con, table)) {
			insertMissingColumns(con, table);
		} else {
			Statement st = con.createStatement();
			st.executeUpdate("CREATE TABLE " + table.toString());
			st.close();
		}
		con.close();
	}
	
	public boolean tableExists(Connection con, Table table) throws SQLException {
		DatabaseMetaData meta = con.getMetaData();
		ResultSet results = meta.getTables(null, null, table.getName(), null);
		return results.next();
	}
	
	public void insertMissingColumns(Connection con, Table table) throws SQLException {
		Statement statement = con.createStatement();
		ResultSet results = statement.executeQuery("SELECT table_schema, table_name, column_name, ordinal_position, data_type," +
		                                           "       numeric_precision, column_type, column_default, is_nullable, column_comment" +
		                                           "  FROM information_schema.columns" +
		                                           "  WHERE (table_schema='schema_name' and table_name = " + table.getName() + ")" +
		                                           "  order by ordinal_position;");
		ResultSetMetaData metadata = results.getMetaData();
		
		for (Table.Field field : table.getFields()) {
			int columnIndex = getColumnIndex(metadata, field);
			
			if (columnIndex == -1) {
				Statement st = con.createStatement();
				st.executeUpdate("ALTER TABLE " + table.getName() + " ADD " + field.toString());
				st.close();
				continue;
			}
			
		}
	}
	
	private void modifyColumn(Connection con,
	                          Table table,
	                          Table.Field field,
	                          ResultSetMetaData tableMeta,
	                          int columnIndex) throws SQLException {
		
		boolean typeMatches = tableMeta.getColumnTypeName(columnIndex).equals(field.getType().toString());
		int nullable = tableMeta.isNullable(columnIndex);
		boolean nullableMatches =
				field.isNotnull() && nullable == ResultSetMetaData.columnNoNulls ||
				!field.isNotnull() && nullable == ResultSetMetaData.columnNullable;
		boolean incrementMatches = field.isAutoIncrement() == tableMeta.isAutoIncrement(columnIndex);
		
//		Statement st = con.createStatement();
//		st.executeUpdate("ALTER TABLE " + table.getName() + " MODIFY " + field.toString());
//		st.close();
	}
	
	private int getColumnIndex(ResultSetMetaData metadata, Table.Field field) throws SQLException {
		int columnCount = metadata.getColumnCount();
		
		for (int i = 1; i <= columnCount; i++) {
			if (metadata.getColumnName(i).equals(field.getName())) {
				return i;
			}
		}
		return -1;
	}
	
	private Connection getCon() throws SQLException {
		return DriverManager.getConnection(
				"jdbc:mariadb://" + Config.DBHost + "/" + Config.DBDatabase,
				Config.DBUser,
				Config.DBPassword
		);
	}
}
