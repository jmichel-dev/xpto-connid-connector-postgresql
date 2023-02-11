package com.evolveum.polygon.connector.xpto.utils;

import org.identityconnectors.framework.common.exceptions.*;
import org.postgresql.util.PSQLException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;

public class GenericExceptionHandler {
    public static void handleGenericException(Exception ex, String message) {


        if ( ex instanceof ConnectorException || ex instanceof UnsupportedOperationException || ex instanceof IllegalArgumentException) {
            throw (RuntimeException) ex;
        }

        if (ex instanceof PSQLException) {
            throw new ConnectionFailedException(message + ", Postgresql database exception occur, reason: " + ex.getMessage(), ex);
        }

        if (ex instanceof SQLException) {
            throw new ConnectionFailedException(message + ", Postgresql database exception occur, reason: " + ex.getMessage(), ex);
        }

        if (ex instanceof IOException) {
            if ((ex instanceof SocketTimeoutException || ex instanceof NoRouteToHostException)) {
                throw new OperationTimeoutException(message + ", timeout occured, reason: " + ex.getMessage(), ex);
            }

            throw new ConnectorIOException(message + " IO exception occcured, reason: " + ex.getMessage(), ex);
        }
    }
}
