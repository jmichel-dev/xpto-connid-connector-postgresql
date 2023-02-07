package com.evolveum.polygon.connector.xpto.config;

import com.evolveum.polygon.connector.xpto.XptoConfiguration;
import com.evolveum.polygon.connector.xpto.XptoConnector;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.api.APIConfiguration;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.api.ConnectorFacadeFactory;
import org.identityconnectors.test.common.TestHelpers;

public abstract class BaseConnectionTest {
    protected ConnectorFacade setupConnector() {
        XptoConfiguration config = new XptoConfiguration();
        config.setDatabaseHost("localhost");
        config.setDatabasePort("5432");
        config.setDatabaseName("xpto");
        config.setDatabaseUser("xpto");
        config.setDatabasePassword(new GuardedString("smartway".toCharArray()));

        return setupConnector(config);
    }

    protected ConnectorFacade setupConnector(XptoConfiguration config) {

        ConnectorFacadeFactory factory = ConnectorFacadeFactory.getInstance();

        APIConfiguration impl = TestHelpers.createTestConfiguration(XptoConnector.class, config);

        impl.getResultsHandlerConfiguration().setEnableAttributesToGetSearchResultsHandler(false);
        impl.getResultsHandlerConfiguration().setEnableCaseInsensitiveFilter(false);
        impl.getResultsHandlerConfiguration().setEnableFilteredResultsHandler(false);
        impl.getResultsHandlerConfiguration().setEnableNormalizingResultsHandler(false);
        impl.getResultsHandlerConfiguration().setFilteredResultsHandlerInValidationMode(false);

        return factory.newInstance(impl);
    }
}
