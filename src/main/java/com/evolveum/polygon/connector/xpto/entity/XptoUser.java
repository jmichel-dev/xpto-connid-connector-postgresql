package com.evolveum.polygon.connector.xpto.entity;

import com.evolveum.polygon.connector.xpto.service.XptoUserService;
import com.evolveum.polygon.connector.xpto.utils.ManageAttributes;
import com.evolveum.polygon.connector.xpto.utils.XptoAttributesConstants;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.*;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class XptoUser extends XptoObject {
    private static final Log LOG = Log.getLog(XptoUser.class);
    private int user_id;
    private String first_name;
    private String last_name;
    private String email;
    private GuardedString password;
    private Boolean is_active;

    public int getUserId() {
        return user_id;
    }

    public void setId(int user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GuardedString getPassword() {
        return password;
    }

    public void setPassword(GuardedString password) {
        this.password = password;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        XptoUser xptoUser = (XptoUser) o;
        return getUserId() == xptoUser.getUserId() && Objects.equals(getFirst_name(), xptoUser.getFirst_name()) && Objects.equals(getLast_name(), xptoUser.getLast_name()) && Objects.equals(getEmail(), xptoUser.getEmail()) && Objects.equals(getPassword(), xptoUser.getPassword()) && Objects.equals(getIs_active(), xptoUser.getIs_active());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getFirst_name(), getLast_name(), getEmail(), getPassword(), getIs_active());
    }

    @Override
    public String toString() {
        return "XptoUser{" +
                "id=" + user_id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", is_active=" + is_active +
                '}';
    }

    public static XptoUser translateUser(Uid uid, Set<Attribute> attributes) {
        XptoUser user = new XptoUser();
        XptoObject.translateObject(uid, attributes, user);

        user.setFirst_name(ManageAttributes.getAttributeValue(XptoAttributesConstants.XPTO_FIRST_NAME, String.class, attributes));
        user.setLast_name(ManageAttributes.getAttributeValue(XptoAttributesConstants.XPTO_LAST_NAME, String.class, attributes));
        user.setEmail(ManageAttributes.getAttributeValue(XptoAttributesConstants.XPTO_EMAIL, String.class, attributes));
        user.setPassword(ManageAttributes.getAttributeValue(OperationalAttributes.PASSWORD_NAME, GuardedString.class, attributes));
        user.setIs_active(ManageAttributes.getAttributeValue(OperationalAttributes.ENABLE_NAME, Boolean.class, attributes));
        return user;
    }

    public ConnectorObject translateToMidpointConnectorObject() {
        ConnectorObjectBuilder builder = new ConnectorObjectBuilder();

        builder.setObjectClass(ObjectClass.ACCOUNT);
        addAttribute(builder, Uid.NAME, email);
        addAttribute(builder, Name.NAME, email);
        addAttribute(builder, XptoAttributesConstants.XPTO_EMAIL, email);
        addAttribute(builder, XptoAttributesConstants.XPTO_FIRST_NAME, first_name);
        addAttribute(builder, XptoAttributesConstants.XPTO_LAST_NAME, last_name);
        addAttribute(builder, OperationalAttributes.ENABLE_NAME, is_active);

        return builder.build();

    }

    private void addAttribute(ConnectorObjectBuilder builder, String name, Object value) {
        if (value == null) {
            return;
        }

        AttributeBuilder ab = new AttributeBuilder();
        ab.setName(name);

        if (value instanceof Collection) {
            ab.addValue((Collection) value);
        } else {
            ab.addValue(value);
        }

        //LOG.ok("Attribute: " + name + " with value(s): " + value.toString() + " added to construction.");
        builder.addAttribute(ab.build());
    }
}
