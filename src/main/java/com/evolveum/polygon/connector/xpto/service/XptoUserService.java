package com.evolveum.polygon.connector.xpto.service;

import com.evolveum.polygon.connector.xpto.entity.XptoUser;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class XptoUserService {
    private static final Log LOG = Log.getLog(XptoUserService.class);
    private final Connection connection;

    public XptoUserService(Connection connection) { this.connection = connection; }

    public String create(XptoUser user) {
        LOG.info("Starting insert of user {0} in database", user);

        String sql = "INSERT INTO users(first_name, last_name, email, password, is_active) VALUES(?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, user.getFirst_name());
            stmt.setString(2, user.getLast_name());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setBoolean(5, user.getIs_active());
            stmt.execute();

            return user.getEmail();
        } catch (SQLException e) {
            LOG.error("Failed to create user in the database, reason: " + e.getMessage());
            throw new InvalidAttributeValueException("failed to create user, reason " + e.getMessage());
        }
    }
}
