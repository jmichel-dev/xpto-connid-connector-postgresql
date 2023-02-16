package com.evolveum.polygon.connector.xpto.utils;

import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeDelta;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProcessingAttributesDelta {
    private final Set<AttributeDelta> set;

    public ProcessingAttributesDelta(Set<AttributeDelta> set) {
        this.set = set;
    }

    public Set<Attribute> getAttrsToReplace() {
        Set<Attribute> attrsToReplace = new HashSet<>();

        set.forEach(delta -> {
            List<Object> valuesToReplace = delta.getValuesToReplace();

            if (valuesToReplace != null) {
                attrsToReplace.add(AttributeBuilder.build(delta.getName(), valuesToReplace));
            }
        });

        return attrsToReplace;
    }

    public Set<Attribute> getAttrsToRemove() {
        Set<Attribute> attrsToRemove = new HashSet<>();

        set.forEach(delta -> {
            List<Object> valuesToRemove = delta.getValuesToRemove();

            if (valuesToRemove != null) {
                attrsToRemove.add(AttributeBuilder.build(delta.getName(), valuesToRemove));
            }
        });

        return attrsToRemove;
    }

    public Set<Attribute> getAttrsToAdd() {
        Set<Attribute> attrsToAdd = new HashSet<>();

        set.forEach(delta -> {
            List<Object> valuesToAdd = delta.getValuesToAdd();

            if (valuesToAdd != null) {
                attrsToAdd.add(AttributeBuilder.build(delta.getName(), valuesToAdd));
            }
        });

        return attrsToAdd;
    }
}
