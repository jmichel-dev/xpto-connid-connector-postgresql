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

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;

import java.sql.SQLException;

@ConnectorClass(displayNameKey = "xpto.connector.display", configurationClass = XptoConfiguration.class)
public class XptoConnector implements Connector {

    private static final Log LOG = Log.getLog(XptoConnector.class);

    private XptoConfiguration configuration;
    private XptoConnection connection;

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void init(Configuration configuration) {
        this.configuration = (XptoConfiguration)configuration;
        this.connection = new XptoConnection(this.configuration);
    }

    @Override
    public void dispose() {
        configuration = null;
        if (connection != null) {
            try {
                connection.dispose();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            connection = null;
        }
    }
}
