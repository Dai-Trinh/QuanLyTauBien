package com.facenet.mdm.config;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MultiTenantSchemaConnectionProvider implements MultiTenantConnectionProvider {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);
    private final DataSource dataSource;

    @Autowired
    public MultiTenantSchemaConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        log.trace("Get connection for tenant {}", tenantIdentifier);
        final Connection connection = getAnyConnection();
        connection.setCatalog(tenantIdentifier);
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        log.trace("Release connection for tenant {}", tenantIdentifier);
        connection.setCatalog(CurrentTenantResolver.DEFAULT_SCHEMA);
        releaseAnyConnection(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
}
