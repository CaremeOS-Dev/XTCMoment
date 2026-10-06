package com.xtc.database.ormlite;

/** Name and definition of a single database column. */
public class ColumnStruct {

    private String columnName;
    private String columnLimit;

    public ColumnStruct() {
    }

    public ColumnStruct(String columnName, String columnLimit) {
        this.columnName = columnName;
        this.columnLimit = columnLimit;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnLimit() {
        return this.columnLimit;
    }

    public void setColumnLimit(String columnLimit) {
        this.columnLimit = columnLimit;
    }

    @Override
    public String toString() {
        return "ColumnStruct{columnName='" + this.columnName + "', columnLimit='" + this.columnLimit + "'}";
    }
}