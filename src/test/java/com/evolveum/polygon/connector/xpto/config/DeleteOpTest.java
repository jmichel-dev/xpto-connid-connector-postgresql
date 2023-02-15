package com.evolveum.polygon.connector.xpto.config;

import com.evolveum.polygon.connector.xpto.utils.XptoAttributesConstants;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.util.List;

public class DeleteOpTest extends BaseConnectionTest {
    @Test(groups = "DeleteOpTest")
    public void shouldRemoveAnUserWithAnExistentRecord() throws Exception {
        String email = "jean.engenheirocomp@gmail.com";
        ConnectorFacade facade = setupConnector();

        facade.delete(ObjectClass.ACCOUNT, new Uid(email), null);

        ListResultHandler handler = new ListResultHandler();
        Attribute attr = AttributeBuilder.build(XptoAttributesConstants.XPTO_EMAIL, "jean.engenheirocomp@gmail.com");
        EqualsFilter equalsFilter = new EqualsFilter(attr);
        facade.search(ObjectClass.ACCOUNT, equalsFilter, handler, null);

        AssertJUnit.assertEquals(0, handler.getObjects().size());
    }
}
