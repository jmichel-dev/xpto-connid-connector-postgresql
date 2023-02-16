package com.evolveum.polygon.connector.xpto.config;

import com.evolveum.polygon.connector.xpto.utils.XptoAttributesConstants;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

public class UpdateOpTest extends BaseConnectionTest {

    private static final String UID = "jean.engenheirocomp@gmail.com";

    @Test(groups = "UpdateTestOp")
    public void shouldUpdateUserFirstName() throws Exception {
        ConnectorFacade facade = setupConnector();

        Set<AttributeDelta> attributeDeltas = new HashSet<>();

        AttributeDeltaBuilder builder = new AttributeDeltaBuilder();
        builder.setName(XptoAttributesConstants.XPTO_FIRST_NAME);
        builder.addValueToReplace("Joao");

        attributeDeltas.add(builder.build());

        facade.updateDelta(ObjectClass.ACCOUNT, new Uid(UID), attributeDeltas, null);

        ConnectorObject object = facade.getObject(ObjectClass.ACCOUNT, new Uid(UID), null);
        Attribute attr = AttributeUtil.find(XptoAttributesConstants.XPTO_FIRST_NAME, object.getAttributes());

        AssertJUnit.assertEquals("Joao", attr.getValue().get(0));
    }

    @Test(groups = "UpdateTestOp")
    public void shouldUpdateUserLastName() throws Exception {
        ConnectorFacade facade = setupConnector();

        Set<AttributeDelta> attributeDeltas = new HashSet<>();

        AttributeDeltaBuilder builder = new AttributeDeltaBuilder();
        builder.setName(XptoAttributesConstants.XPTO_LAST_NAME);
        builder.addValueToReplace("Silva");

        attributeDeltas.add(builder.build());

        facade.updateDelta(ObjectClass.ACCOUNT, new Uid(UID), attributeDeltas, null);

        ConnectorObject object = facade.getObject(ObjectClass.ACCOUNT, new Uid(UID), null);
        Attribute attr = AttributeUtil.find(XptoAttributesConstants.XPTO_LAST_NAME, object.getAttributes());

        AssertJUnit.assertEquals("Silva", attr.getValue().get(0));
    }

    @Test(groups = "UpdateTestOp")
    public void shouldUpdateUsersPassword() throws Exception {
        ConnectorFacade facade = setupConnector();

        Set<AttributeDelta> attributeDeltas = new HashSet<>();

        AttributeDeltaBuilder builder = new AttributeDeltaBuilder();
        builder.setName(OperationalAttributes.PASSWORD_NAME);
        builder.addValueToReplace(new GuardedString("smartway123".toCharArray()));

        attributeDeltas.add(builder.build());

        facade.updateDelta(ObjectClass.ACCOUNT, new Uid(UID), attributeDeltas, null);
    }

    @Test(groups = "UpdateTestOp")
    public void shouldDisableEnableUser() throws Exception {
        ConnectorFacade facade = setupConnector();

        Set<AttributeDelta> attributeDeltas = new HashSet<>();

        AttributeDeltaBuilder builder = new AttributeDeltaBuilder();
        builder.setName(OperationalAttributes.ENABLE_NAME);
        builder.addValueToReplace(false);

        attributeDeltas.add(builder.build());

        facade.updateDelta(ObjectClass.ACCOUNT, new Uid(UID), attributeDeltas, null);

        ConnectorObject object = facade.getObject(ObjectClass.ACCOUNT, new Uid(UID), null);
        Attribute attr = AttributeUtil.find(OperationalAttributes.ENABLE_NAME, object.getAttributes());

        AssertJUnit.assertEquals(false, attr.getValue().get(0));

        AttributeDeltaBuilder builder2 = new AttributeDeltaBuilder();
        builder2.setName(OperationalAttributes.ENABLE_NAME);
        builder2.addValueToReplace(true);

        Set<AttributeDelta> attributeDeltas1 = new HashSet<>();
        attributeDeltas1.add(builder2.build());

        facade.updateDelta(ObjectClass.ACCOUNT, new Uid(UID), attributeDeltas1, null);

        ConnectorObject objectEnable = facade.getObject(ObjectClass.ACCOUNT, new Uid(UID), null);
        Attribute attrEnable = AttributeUtil.find(OperationalAttributes.ENABLE_NAME, objectEnable.getAttributes());

        AssertJUnit.assertEquals(true, attrEnable.getValue().get(0));
    }

    @Test(groups = "UpdateTestOp", expectedExceptions = ConnectorException.class)
    public void shouldNotRemoveEmailAttributeFromResource() throws Exception {
        ConnectorFacade facade = setupConnector();

        Set<AttributeDelta> attributeDeltas = new HashSet<>();

        AttributeDeltaBuilder builder = new AttributeDeltaBuilder();
        builder.setName(XptoAttributesConstants.XPTO_EMAIL);
        builder.addValueToRemove(UID);

        attributeDeltas.add(builder.build());

        facade.updateDelta(ObjectClass.ACCOUNT, new Uid(UID), attributeDeltas, null);

    }
}
