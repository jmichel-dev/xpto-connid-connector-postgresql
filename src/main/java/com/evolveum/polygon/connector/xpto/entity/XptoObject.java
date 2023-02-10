package com.evolveum.polygon.connector.xpto.entity;

import com.evolveum.polygon.connector.xpto.utils.ManageAttributes;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;

import java.util.Objects;
import java.util.Set;

public class XptoObject {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XptoObject that = (XptoObject) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    public static void translateObject(Uid uid, Set<Attribute> attributes, XptoObject object) {
        if (uid != null) {
            object.setId(uid.getUidValue());
        }

        String nameUpdate = ManageAttributes.getAttributeValue(Name.NAME, String.class, attributes);

        Name name;

        if (nameUpdate != null && !nameUpdate.isEmpty()) {
            name = new Name(nameUpdate);
        } else {
            name = AttributeUtil.getNameFromAttributes(attributes);
        }

        if (name == null || StringUtil.isEmpty(name.getNameValue())) {
            throw new InvalidAttributeValueException("Name not defined or its's empty");
        }

        object.setName(name.getNameValue());
    }
}
