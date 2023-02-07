package com.evolveum.polygon.connector.xpto.config;

import com.evolveum.polygon.connector.xpto.XptoConfiguration;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.testng.annotations.Test;

public class TestOpTest extends BaseConnectionTest{

    @Test
    public void shouldBeAbleToConnectToTheDatabase() throws Exception {
        ConnectorFacade connector = setupConnector();
        connector.test();
    }
    @Test(expectedExceptions = ConnectionFailedException.class)
    public void shouldNotBeAbleToConnectToTheDatabaseWithWrongPassword() {
        XptoConfiguration config = new XptoConfiguration();
        config.setDatabaseHost("localhost");
        config.setDatabasePort("5432");
        config.setDatabaseName("salutaris");
        config.setDatabaseUser("salutaris");
        config.setDatabasePassword(new GuardedString("wrong".toCharArray()));

        ConnectorFacade connector = setupConnector(config);
        connector.test();
    }
}
