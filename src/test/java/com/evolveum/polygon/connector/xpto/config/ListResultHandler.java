package com.evolveum.polygon.connector.xpto.config;

import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ResultsHandler;

import java.util.ArrayList;
import java.util.List;

public class ListeResultHandler implements ResultsHandler {
    private List<ConnectorObject> objects = new ArrayList<>();
    @Override
    public boolean handle(ConnectorObject connectorObject) {
        objects.add(connectorObject);
        return false;
    }

    public List<ConnectorObject> getObjects() {
        return objects;
    }
}
