package com.evolveum.polygon.connector.xpto.service;

import com.evolveum.polygon.connector.xpto.entity.XptoUser;
import com.evolveum.polygon.connector.xpto.utils.StringAccessor;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class XptoUserService {
    private static final Log LOG = Log.getLog(XptoUserService.class);
    private final Connection connection;

    public XptoUserService(Connection connection) { this.connection = connection; }

    public String create(XptoUser user) throws SQLException {
        LOG.info("Starting insert of user {0} in database", user);

        String sql = "INSERT INTO users(first_name, last_name, email, password, is_active) VALUES(?, ?, ?, ?, ?)";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, user.getFirst_name());
        stmt.setString(2, user.getLast_name());
        stmt.setString(3, user.getEmail());
        StringAccessor accessor = new StringAccessor();
        GuardedString pwd = user.getPassword();
        pwd.access(accessor);
        stmt.setString(4, accessor.getValue());
        stmt.setBoolean(5, user.getIs_active());
        stmt.execute();

        return user.getEmail();

    }

    public List<XptoUser> search(String filter) throws SQLException {
        String sql = "SELECT * from users";

        if (filter != null && !filter.isEmpty()) {
            sql += " where " + filter;
        }
        List<XptoUser> users = new ArrayList<>();

        PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet result = stmt.executeQuery();

        while (result.next()) {
            String firstName = result.getString("first_name");
            String lastName = result.getString("last_name");
            String email = result.getString("email");
            Boolean isActive = result.getBoolean("is_active");

            XptoUser user = new XptoUser();
            user.setEmail(email);
            user.setLast_name(lastName);
            user.setFirst_name(firstName);
            user.setIs_active(isActive);

            users.add(user);
        }

        return users;
    }
}
