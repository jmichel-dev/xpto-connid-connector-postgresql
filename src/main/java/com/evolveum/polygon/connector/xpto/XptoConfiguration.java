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

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.spi.ConfigurationProperty;

import javax.naming.ConfigurationException;

public class XptoConfiguration extends AbstractConfiguration {

    private static final Log LOG = Log.getLog(XptoConfiguration.class);

    private String databaseHost;
    private String databasePort;
    private String databaseName;
    private String databaseUser;
    private GuardedString databasePassword;

    @ConfigurationProperty(
            displayMessageKey = "xpto.connector.databaseHost",
            helpMessageKey = "xpto.connector.databaseHost.help",
            required = true,
            order = 1
    )
    public String getDatabaseHost() {
        return databaseHost;
    }

    public void setDatabaseHost(String databaseHost) {
        this.databaseHost = databaseHost;
    }

    @ConfigurationProperty(
            displayMessageKey = "xpto.connector.databasePort",
            helpMessageKey = "xpto.connector.databasePort.help",
            order = 2
    )
    public String getDatabasePort() {
        return databasePort;
    }

    public void setDatabasePort(String databasePort) {
        this.databasePort = databasePort;
    }

    @ConfigurationProperty(
            displayMessageKey = "xpto.connector.databaseName",
            helpMessageKey = "xpto.connector.databaseName.help",
            required = true,
            order = 3
    )
    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    @ConfigurationProperty(
            displayMessageKey = "xpto.connector.databaseUser",
            helpMessageKey = "xpto.connector.databaseUser.help",
            required = true,
            order = 4
    )
    public String getDatabaseUser() {
        return databaseUser;
    }

    public void setDatabaseUser(String databaseUser) {
        this.databaseUser = databaseUser;
    }

    @ConfigurationProperty(
            displayMessageKey = "xpto.connector.databasePassword",
            helpMessageKey = "xpto.connector.databasePassword.help",
            required = true,
            order = 5
    )
    public GuardedString getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(GuardedString databasePassword) {
        this.databasePassword = databasePassword;
    }

    @Override
    public void validate() {
        String exceptionMsg = null;

        if (StringUtil.isBlank(databaseHost)) {
            exceptionMsg = "Database host was no provided";
        } else if(StringUtil.isBlank(databaseName)) {
            exceptionMsg = "Database name was not provided";
        } else if(StringUtil.isBlank(databaseUser)) {
            exceptionMsg = "Database user was not provided";
        } else if (databasePassword == null) {
            exceptionMsg = "Database password was not provided";
        } else if (StringUtil.isNotBlank(databasePort)) {
            try {
                Integer.parseInt(databasePort);
            } catch (Exception e) {
                exceptionMsg = "Database port was not provied properly";
            }
        }

        if (!StringUtil.isBlank(exceptionMsg)) {
            LOG.info("End of configuration validation procedure");
            return;
        }

        LOG.error("Configuration validation procedure failed {0}", exceptionMsg);
        try {
            throw new ConfigurationException(exceptionMsg);
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }


}