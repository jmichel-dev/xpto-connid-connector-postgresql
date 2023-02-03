package com.evolveum.polygon.connector.xpto.database;

import com.evolveum.polygon.connector.xpto.XptoConfiguration;
import com.evolveum.polygon.connector.xpto.utils.StringAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class XptoDatabaseConnectionHandler {
    private XptoConfiguration configuration;

    public XptoDatabaseConnectionHandler(XptoConfiguration configuration) { this.configuration = configuration; }

    protected Connection databaseConnection() throws SQLException {
        String uri = "jdbc:postgresql://" +
                configuration.getDatabaseHost() +
                ":" + configuration.getDatabasePort() +
                "/" + configuration.getDatabaseName();

        StringAccessor accessor = new StringAccessor();

        Properties props = new Properties();
        props.setProperty("user", configuration.getDatabaseUser());
        
        this.configuration.getDatabasePassword().access(accessor);
        props.setProperty("password", accessor.getValue());
        
        return DriverManager.getConnection(uri, props);
    }

    protected void setConfiguration(XptoConfiguration configuration) {
        this.configuration = configuration;
    }
}
