package com.xtc.database.ormlite;

/** Metadata of an opened database. */
public class DatabaseInfo {

    private String databaseName;
    private int databaseVersion;
    private DatabaseHelper helper;

    public String getDatabaseName() {
        return this.databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public int getDatabaseVersion() {
        return this.databaseVersion;
    }

    public void setDatabaseVersion(int databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public DatabaseHelper getHelper() {
        return this.helper;
    }

    public void setHelper(DatabaseHelper helper) {
        this.helper = helper;
    }

    @Override
    public String toString() {
        return "DatabaseInfo{databaseName='" + this.databaseName + "', databaseVersion=" + this.databaseVersion + '}';
    }
}