package me.spthiel.database;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class Table {
	
	private final String name;
	private final LinkedList<Field> fields;
	
	public Table(String name) {
		this.name = name;
		this.fields = new LinkedList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public LinkedList<Field> getFields() {
		return fields;
	}
	
	public Table addField(Field field) {
		this.fields.add(field);
		return this;
	}
	
	@Override
	public String toString() {
		return name + " (" +
		       fields.stream()
				       .map(Field::toString)
				       .collect(Collectors.joining(", ")) +
		       fields.stream()
				       .filter(Field::isPrimary)
				       .map(field -> ", PRIMARY KEY (" + field.getName() + ")")
				       .collect(Collectors.joining(", ")) +
		       fields.stream()
				       .filter(Field::isForeign)
				       .map(field -> ", FOREIGN KEY (" + field.getName() + ") REFERENCES " + field.getForeignTable() + "(" + field.getForeignField() + ")")
				       .collect(Collectors.joining(", ")) +
		       ")";
	}
	
	public static class Field {
		
		private final String name;
		private final VariableFieldType variableFieldType;
		
		private boolean notnull = false;
		private boolean autoIncrement = false;
		private String defaultValue = null;
		
		private boolean primary;
		private String foreignTable;
		private String foreignField;
		
		public Field(String name, FieldType type) {
			this(name, type, null);
		}
		
		public Field(String name, FieldType type, String argument) {
			this.name = name;
			this.variableFieldType = new VariableFieldType(type, argument);
		}
		
		public String getName() {
			return name;
		}
		
		public VariableFieldType getVariableFieldType() {
			return variableFieldType;
		}
		
		public Table.FieldType getType() {
			return variableFieldType.getType();
		}
		
		public boolean isPrimary() {
			return primary;
		}
		
		public boolean isForeign() {
			return foreignTable != null && foreignField != null;
		}
		
		public String getForeignTable() {
			return foreignTable;
		}
		
		public String getForeignField() {
			return foreignField;
		}
		
		public boolean isNotnull() {
			return notnull;
		}
		
		public boolean isAutoIncrement() {
			return autoIncrement;
		}
		
		public String getDefaultValue() {
			return defaultValue;
		}
		
		public Field setPrimary() {
			this.primary = true;
			return this;
		}
		
		public Field setForeign(String foreignTable, String foreignField) {
			this.foreignTable = foreignTable;
			this.foreignField = foreignField;
			return this;
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
			return this.name + " " + variableFieldType +
			       (notnull ? " not null " : "") +
			       (autoIncrement ? " auto_increment " : "") +
			       (defaultValue != null ? " DEFAULT " + defaultValue : "");
		}
		
		public String toString2() {
			return this.name + " " + variableFieldType +
			       (notnull ? " not null " : "") +
			       (autoIncrement ? " auto_increment " : "") +
			       (defaultValue != null ? " DEFAULT " + defaultValue : "");
		}
	}
	
	public static enum FieldType {
		TINYINT,
		SMALLINT,
		MEDIUMINT,
		INTEGER,
		BIGINT,
		BIT,
		
		FLOAT,
		DOUBLE,
		DECIMAL,
		CHAR,
		VARCHAR,
		MEDIUMTEXT,
		TEXT,
		
		BOOLEAN,
		BLOB,
		MEDIUMBLOB,
		LONGBLOB,
		
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
		private final String argument;
		
		public VariableFieldType(FieldType type) {
			this.type = type;
			this.argument = null;
		}
		
		public VariableFieldType(FieldType type, String argument) {
			this.type = type;
			this.argument = argument;
		}
		
		public FieldType getType() {
			return type;
		}
		
		public String getArgument() {
			return argument;
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
