package me.spthiel.database;

import java.util.LinkedList;

public class Table {
	
	private final String            name;
	private       LinkedList<Field> fields;
	
	public Table(String name) {
		
		this.name = name;
		this.fields = new LinkedList<>();
	}
	
	public Table addField(Field field) {
		
		this.fields.add(field);
		return this;
	}
	
	public static class Field {
		
		private final String            name;
		private final VariableFieldType variableFieldType;
		
		private boolean notnull       = false;
		private boolean autoIncrement = false;
		private String  defaultValue  = null;
		
		public Field(String name, FieldType type) {
			
			this(name, type, null);
		}
		
		public Field(String name, FieldType type, String argument) {
			
			this.name = name;
			this.variableFieldType = new VariableFieldType(type, argument);
		}
		
		public Field setNonNull() {
			
			this.notnull = true;
			return this;
		}
		
		public Field setAutoIncrement() {
			
			this.autoIncrement = true;
			return this;
		}
		
		public Field setDefaultValue(String defaultValue) {
			
			this.defaultValue = defaultValue;
			return this;
		}
		
		@Override
		public String toString() {
			
			return this.name + " " + variableFieldType + " " + (notnull ? "not null " : "") + (autoIncrement ? "auto_increment " : "") + (defaultValue != null ? "DEFAULT " + defaultValue : "");
		}
	}
	
	public static enum FieldType {
		TINYINT,
		BOOLEAN,
		SMALLINT,
		MEDIUMINT,
		INT,
		INTEGER,
		BIGINT,
		DECIMAL,
		FLOAT,
		DOUBLE,
		BIT,
		CHAR,
		VARCHAR,
		BLOB,
		MEDIUMBLOB,
		LONGBLOB,
		TEXT,
		MEDIUMTEXT,
		DATE,
		TIME,
		DATETIME,
		TIMESTAMP;
		
		@Override
		public String toString() {
			
			return name();
		}
	}
	
	private static class VariableFieldType {
		
		private final FieldType type;
		private final String    argument;
		
		public VariableFieldType(FieldType type) {
			
			this.type = type;
			this.argument = null;
		}
		
		public VariableFieldType(FieldType type, String argument) {
			
			this.type = type;
			this.argument = argument;
		}
		
		@Override
		public String toString() {
			
			if (argument == null) {
				return this.type.toString();
			}
			return this.type.toString() + "(" + argument + ")";
		}
	}
	
}
