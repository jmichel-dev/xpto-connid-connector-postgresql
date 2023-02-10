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

import com.evolveum.polygon.connector.xpto.entity.XptoUser;
import com.evolveum.polygon.connector.xpto.service.XptoUserService;
import com.evolveum.polygon.connector.xpto.utils.FilterHandler;
import com.evolveum.polygon.connector.xpto.utils.GenericExceptionHandler;
import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectionBrokenException;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.TestOp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@ConnectorClass(displayNameKey = "xpto.connector.display", configurationClass = XptoConfiguration.class)
public class XptoConnector implements Connector, TestOp, CreateOp, SearchOp<Filter> {

    private static final Log LOG = Log.getLog(XptoConnector.class);

    private XptoConfiguration configuration;
    private XptoConnection connection;
    private Connection databaseConnection;
    private XptoUserService userService;

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void init(Configuration configuration) {
        this.configuration = (XptoConfiguration)configuration;
        this.connection = new XptoConnection(this.configuration);

        try {
            connection.setUpConnection();
            databaseConnection = connection.getDatabaseConnection();
            userService = connection.getXptoUserService();
        } catch (SQLException e) {
            throw new ConnectionFailedException(e);
        }
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

    @Override
    public void test() {
        try {
            databaseConnection.isValid(5);
            LOG.info("Test service execution finished successfully");
        } catch (SQLException e) {
            throw new ConnectionBrokenException(e);
        }
    }

    @Override
    public Uid create(ObjectClass objectClass, Set<Attribute> set, OperationOptions operationOptions) {
        try {
            String uid;

            if (ObjectClass.ACCOUNT.equals(objectClass)) {
                LOG.info("Started procedure for create user.");
                LOG.info("Translating user...");
                XptoUser user = XptoUser.translateUser(null, set);
                LOG.info("User {0] translated successfully", user);
                LOG.info("Creating user in the target...");
                uid = userService.create(user);
            } else {
                throw new UnsupportedOperationException("Unknown object class " + objectClass);
            }

            LOG.ok("Created new object with the UID: {0}", uid);
            return new Uid(uid);
        } catch (Exception ex) {
            new GenericExceptionHandler().handleGenericException(ex, "Couldn't create object " + objectClass + " with attributes " + set + ", reason: " + ex.getMessage());
        }

        return null;
    }

    @Override
    public FilterTranslator<Filter> createFilterTranslator(ObjectClass objectClass, OperationOptions operationOptions) {
        return new FilterTranslator<Filter>() {
            @Override
            public List<Filter> translate(Filter filter) {
                return CollectionUtil.newList(filter);
            }
        };
    }

    @Override
    public void executeQuery(ObjectClass objectClass, Filter filter, ResultsHandler resultsHandler, OperationOptions operationOptions) {
        String query = "";

        if (filter != null) {
            query = filter.accept(new FilterHandler(), "");
            LOG.info("Query will be executed with the following filter: {0}", query);
            LOG.info("The object class from which the filter will be executed: {0}", objectClass.getDisplayNameKey());
        }
    }
}
