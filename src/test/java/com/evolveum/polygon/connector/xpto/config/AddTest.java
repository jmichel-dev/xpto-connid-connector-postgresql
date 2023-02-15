package com.evolveum.polygon.connector.xpto.config;

import com.evolveum.polygon.connector.xpto.utils.XptoAttributesConstants;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;

public class AddTest extends BaseConnectionTest {

    private Uid createdUid;
    private static final String PASSWORD = "smartway";

    @Test(groups = "AddOpTest")
    public void shouldCreateAnUserSuccessfully() throws Exception {
        ConnectorFacade facade = setupConnector();

        Set<Attribute> set = new HashSet<>();
        set.add(AttributeBuilder.build(Name.NAME, "jean.engenheirocomp@gmail.com"));
        set.add(AttributeBuilder.build(XptoAttributesConstants.XPTO_FIRST_NAME, "Jean"));
        set.add(AttributeBuilder.build(XptoAttributesConstants.XPTO_LAST_NAME, "Santos"));
        set.add(AttributeBuilder.build(XptoAttributesConstants.XPTO_EMAIL, "jean.engenheirocomp@gmail.com"));
        set.add(AttributeBuilder.build(OperationalAttributes.ENABLE_NAME, true));
        set.add(AttributeBuilder.build(OperationalAttributes.PASSWORD_NAME, new GuardedString(PASSWORD.toCharArray())));

        createdUid = facade.create(ObjectClass.ACCOUNT, set, null);
        AssertJUnit.assertNotNull(createdUid);
        AssertJUnit.assertEquals("jean.engenheirocomp@gmail.com", createdUid.getUidValue());
    }

    @Test(groups = "AddOpTest", expectedExceptions = AlreadyExistsException.class)
    public void shouldNotCreateUserWithTheAnEmailThatAlreadyExists() throws Exception {
        ConnectorFacade facade = setupConnector();

        Set<Attribute> set = new HashSet<>();
        set.add(AttributeBuilder.build(Name.NAME, "jean.engenheirocomp@gmail.com"));
        set.add(AttributeBuilder.build(XptoAttributesConstants.XPTO_FIRST_NAME, "Jean"));
        set.add(AttributeBuilder.build(XptoAttributesConstants.XPTO_LAST_NAME, "Santos"));
        set.add(AttributeBuilder.build(XptoAttributesConstants.XPTO_EMAIL, "jean.engenheirocomp@gmail.com"));
        set.add(AttributeBuilder.build(OperationalAttributes.ENABLE_NAME, true));
        set.add(AttributeBuilder.build(OperationalAttributes.PASSWORD_NAME, new GuardedString(PASSWORD.toCharArray())));

        createdUid = facade.create(ObjectClass.ACCOUNT, set, null);
    }

    @Test(groups = "AddOpTest", expectedExceptions = InvalidAttributeValueException.class)
    public void shouldNotCreateUserWithoutAFirstName() throws Exception {
        ConnectorFacade facade = setupConnector();

        Set<Attribute> set = new HashSet<>();
        set.add(AttributeBuilder.build(Name.NAME, "jean.engenheirocomp@gmail.com"));
        set.add(AttributeBuilder.build(XptoAttributesConstants.XPTO_LAST_NAME, "Santos"));
        set.add(AttributeBuilder.build(XptoAttributesConstants.XPTO_EMAIL, "jean.engenheirocomp@gmail.com"));
        set.add(AttributeBuilder.build(OperationalAttributes.ENABLE_NAME, true));
        set.add(AttributeBuilder.build(OperationalAttributes.PASSWORD_NAME, new GuardedString(PASSWORD.toCharArray())));

        createdUid = facade.create(ObjectClass.ACCOUNT, set, null);
    }

    @Test(groups = "AddOpTest", expectedExceptions = InvalidAttributeValueException.class)
    public void shouldNotCreateUserWithoutTheNameId() throws Exception {
        ConnectorFacade facade = setupConnector();

        Set<Attribute> set = new HashSet<>();
        set.add(AttributeBuilder.build(XptoAttributesConstants.XPTO_FIRST_NAME, "Joao"));
        set.add(AttributeBuilder.build(XptoAttributesConstants.XPTO_LAST_NAME, "Santos"));
        set.add(AttributeBuilder.build(XptoAttributesConstants.XPTO_EMAIL, "joao1@test.com"));
        set.add(AttributeBuilder.build(OperationalAttributes.ENABLE_NAME, true));
        set.add(AttributeBuilder.build(OperationalAttributes.PASSWORD_NAME, new GuardedString(PASSWORD.toCharArray())));

        createdUid = facade.create(ObjectClass.ACCOUNT, set, null);
    }
}
