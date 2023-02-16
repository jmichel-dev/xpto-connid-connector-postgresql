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

import com.evolveum.polygon.connector.xpto.entity.XptoObject;
import com.evolveum.polygon.connector.xpto.entity.XptoUser;
import com.evolveum.polygon.connector.xpto.service.XptoUserService;
import com.evolveum.polygon.connector.xpto.utils.FilterHandler;
import com.evolveum.polygon.connector.xpto.utils.GenericExceptionHandler;
import com.evolveum.polygon.connector.xpto.utils.ProcessingAttributesDelta;
import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectionBrokenException;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ConnectorClass(displayNameKey = "xpto.connector.display", configurationClass = XptoConfiguration.class)
public class XptoConnector implements Connector, TestOp, CreateOp, SearchOp<Filter>, DeleteOp, UpdateDeltaOp {

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
                LOG.info("User {0} translated successfully", user);
                LOG.info("Creating user in the target...");
                uid = userService.create(user);
            } else {
                throw new UnsupportedOperationException("Unknown object class " + objectClass);
            }

            LOG.ok("Created new object with the UID: {0}", uid);
            return new Uid(uid);
        } catch (Exception ex) {
            GenericExceptionHandler.handleGenericException(ex, "Couldn't create object " + objectClass + " with attributes " + set + ", reason: " + ex.getMessage());
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
        String query = null;

        if (filter != null) {
            query = filter.accept(new FilterHandler(), "");
            LOG.info("Query will be executed with the following filter: {0}", query);
            LOG.info("The object class from which the filter will be executed: {0}", objectClass.getDisplayNameKey());
        }

        try {
            List<XptoUser> users = new ArrayList<>();
            if (ObjectClass.ACCOUNT.equals(objectClass)) {
                LOG.info("initializing search service...");
                users = userService.search(query);
            } else {
                throw new UnsupportedOperationException("Unknown object class " + objectClass);
            }

            if (users == null || users.isEmpty()) {
                LOG.info("No objects returned by query.");

                return;
            }

            LOG.ok("Objects found: {0}", users.size());

            for (XptoUser user: users) {
                if (user == null) return;

                ConnectorObject connectorObject = user.translateToMidpointConnectorObject();

                if (!resultsHandler.handle(connectorObject)) break;
            }
        } catch (Exception e) {
            GenericExceptionHandler.handleGenericException(e, "Couldn't search " + objectClass + " with filter " + query + ", reason: " + e.getMessage());
        }
    }

    @Override
    public void delete(ObjectClass objectClass, Uid uid, OperationOptions operationOptions) {
        try {
            if (ObjectClass.ACCOUNT.equals(objectClass)) {
                userService.delete(uid.getUidValue());
            } else {
                throw new UnsupportedOperationException("Unknown object class " + objectClass);
            }

            LOG.ok("The object with the uid {0} was deleted by the connector instance.", uid);
        } catch (Exception e) {
            GenericExceptionHandler.handleGenericException(e, "Couldn't delete " + objectClass + " with the uid " + uid + ", reason: " + e.getMessage());
        }
    }

    @Override
    public Set<AttributeDelta> updateDelta(ObjectClass objectClass, Uid uid, Set<AttributeDelta> set, OperationOptions operationOptions) {
        Uid newUid = null;
        ProcessingAttributesDelta deltas = new ProcessingAttributesDelta(set);
        Set<Attribute> attrsToReplace = deltas.getAttrsToReplace();
        Set<Attribute> attrsToRemove = deltas.getAttrsToRemove();
        Set<Attribute> attrsToAdd = deltas.getAttrsToAdd();

        try {
            ConnectorObject old = getOldObject(objectClass, uid);

            LOG.ok("fetched the object with the uid {0} from the resource for delta update", uid.getUidValue());

            Set<Attribute> processedAttrs = new HashSet<>(old.getAttributes());

            if (!attrsToReplace.isEmpty()) {
                LOG.ok("Processing through replace set of attributes in the update attribute delta op");

                attrsToReplace.forEach(newAttr -> {
                    Attribute oldAttr = AttributeUtil.find(newAttr.getName(), processedAttrs);
                    if (oldAttr != null) {
                        processedAttrs.remove(oldAttr);
                    }
                    processedAttrs.add(newAttr);
                });
            }

            if (!attrsToAdd.isEmpty()) {
                LOG.ok("Processing through ADD set of attributes in the update attribute delta op");

                attrsToAdd.forEach(newAttr -> {
                    Attribute oldAttr = AttributeUtil.find(newAttr.getName(), processedAttrs);

                    if (oldAttr != null) {
                        List values = new ArrayList();
                        if (oldAttr.getValue() != null) {
                            values.addAll(oldAttr.getValue());
                        }

                        values.addAll(newAttr.getValue());
                        processedAttrs.remove(oldAttr);
                        processedAttrs.add(AttributeBuilder.build(oldAttr.getName(), values));
                    } else {
                        processedAttrs.add(newAttr);
                    }
                });
            }

            if (!attrsToRemove.isEmpty()) {
                LOG.ok("Processing through DELETE set of attributes in the update attribute delta op.");

                attrsToRemove.forEach(removeAttr -> {
                    Attribute oldAttr = AttributeUtil.find(removeAttr.getName(), processedAttrs);

                    if (oldAttr != null) {
                        List values = new ArrayList();

                        if (oldAttr.getValue() != null) {
                            values.addAll(oldAttr.getValue());
                        }

                        values.removeAll(removeAttr.getValue());
                        processedAttrs.remove(oldAttr);
                        processedAttrs.add(AttributeBuilder.build(oldAttr.getName(), values));
                    }
                });
            }

            newUid = updateObject(objectClass, uid, processedAttrs);

        } catch (Exception e) {
            GenericExceptionHandler.handleGenericException(e, "Couldn't modify attribute values from object " + objectClass +
                    " with uid " + uid + ", reason: " + e.getMessage());
        }

        Set<AttributeDelta> returnDelta = new HashSet<>();

        if (newUid == null || newUid != uid) {
            AttributeDelta newUidAttributeDelta = AttributeDeltaBuilder.build(Uid.NAME, uid.getUidValue());
            returnDelta.add(newUidAttributeDelta);
        }

        return returnDelta;
    }

    private ConnectorObject getOldObject(ObjectClass objectClass, Uid uid) {
        XptoUser object;

        try {
            if (ObjectClass.ACCOUNT.equals(objectClass)) {
                object = userService.searchById(uid.getUidValue());
            } else {
                throw new UnsupportedOperationException("Unknown object class " + objectClass);
            }

            if (object == null) {
                throw new UnknownUidException("Couldn't find object " + objectClass + " with uid " + uid.getUidValue());
            }

            //LOG.info("old object found " + object.toString());

            return object.translateToMidpointConnectorObject();
        } catch (Exception e) {
            GenericExceptionHandler.handleGenericException(e, "Couldn't search object by uid " + uid.getUidValue() + ", reason: " + e.getMessage());
        }

        return null;
    }

    private Uid updateObject(ObjectClass objectClass, Uid uid, Set<Attribute> attributes) {
        String uidString;
        try {
            if (ObjectClass.ACCOUNT.equals(objectClass)) {
                XptoUser user = XptoUser.translateUser(uid, attributes);
                LOG.info("New user processed {0}", user);
                uidString = userService.updateUser(user);
            } else {
                throw new UnsupportedOperationException("Unknown object class " + objectClass);
            }
            if (uidString != null) {
                return new Uid(uidString);
            } else {
                throw new ConnectorException("Unexpected exception occurred. No uid returned by resource after update" +
                        " operation execution for the object with the uid: " + uid + ".");
            }
        } catch (Exception e) {
            GenericExceptionHandler.handleGenericException(e, "Couldn't update " + objectClass + " with the uid " + uid + ", reason: " + e.getMessage());
        }

        return null;
    }
}
