/*
 * Copyright (c) 2010-2014 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.polygon.connector.xpto;

import com.evolveum.polygon.connector.xpto.database.XptoDatabaseConnectionHandler;
import org.identityconnectors.common.logging.Log;

import java.sql.Connection;
import java.sql.SQLException;

public class XptoConnection extends XptoDatabaseConnectionHandler {

    private static final Log LOG = Log.getLog(XptoConnection.class);

    private XptoConfiguration configuration;
    private Connection connection;

    public XptoConnection(XptoConfiguration configuration) {
        super(configuration);
    }

    public void setUpConnection() throws SQLException {
        connection = databaseConnection();
        LOG.info("The database connection initialized successfully");
    }

    public Connection getDatabaseConnection() { return connection; }

    public void dispose() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}