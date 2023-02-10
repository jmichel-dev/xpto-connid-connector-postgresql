package com.evolveum.polygon.connector.xpto.config;

import com.evolveum.polygon.connector.xpto.utils.XptoAttributesConstants;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.filter.AndFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class SearchTest extends BaseConnectionTest{

    @Test
    public void shouldGetUserByEmail() throws Exception {
        ConnectorFacade facade = setupConnector();

        ListResultHandler handler = new ListResultHandler();
        Attribute attr = AttributeBuilder.build(XptoAttributesConstants.XPTO_EMAIL, "jean.engenheirocomp@gmail.com");
        EqualsFilter equalsFilter = new EqualsFilter(attr);
        facade.search(ObjectClass.ACCOUNT, equalsFilter, handler, null);

        AssertJUnit.assertTrue(handler.getObjects().size() > 0);
    }
}
