package com.github.strdn.rundeck.plugin.jdbcexecutor;

public enum DBTypes {
    ORACLE("oracle.jdbc.driver.OracleDriver"), // Verify if there's a newer class name for newer Oracle drivers
    MYSQL("com.mysql.cj.jdbc.Driver"),
    MSSQL("com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    POSTGRES("org.postgresql.Driver");

    private final String driverName;

    DBTypes(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverName() {
        return driverName;
    }
}
