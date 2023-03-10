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

    public XptoUser searchById(String uid) throws SQLException {
        String sql = "SELECT * FROM users WHERE email ILIKE ?";
        XptoUser user = new XptoUser();

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, uid);

        ResultSet result = stmt.executeQuery();

        if (result.next()) {
            String firstName = result.getString("first_name");
            String lastName = result.getString("last_name");
            String email = result.getString("email");
            Boolean isActive = result.getBoolean("is_active");

            user.setEmail(email);
            user.setLast_name(lastName);
            user.setFirst_name(firstName);
            user.setIs_active(isActive);
            user.setName(email);
            user.setId(email);
        }

        return user;
    }

    public void delete(String uid) throws SQLException {
        String sql = "DELETE FROM users WHERE email ILIKE '" + uid + "'";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.execute();
    }

    public String updateUser(XptoUser user) throws SQLException {
        String sql = "UPDATE users SET first_name=?, last_name=?, email=?, is_active=? WHERE email ILIKE ?";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, user.getFirst_name());
        stmt.setString(2, user.getLast_name());
        stmt.setString(3, user.getEmail());

        stmt.setBoolean(4, user.getIs_active());
        stmt.setString(5, user.getEmail());

        if (user.getPassword() != null) {
            changePassword(user);
        }

        int output = stmt.executeUpdate();
        return user.getEmail();
    }

    private void changePassword(XptoUser user) throws SQLException {
        LOG.info("Starting change password of user {0} in database", user);

        String sql = "UPDATE users SET password = ? WHERE email ILIKE ?";

        PreparedStatement stmt = connection.prepareStatement(sql);

        StringAccessor accessor = new StringAccessor();
        GuardedString pwd = user.getPassword();
        pwd.access(accessor);
        stmt.setString(1, accessor.getValue());
        stmt.setString(2, user.getEmail());

        stmt.executeUpdate();
    }
}
