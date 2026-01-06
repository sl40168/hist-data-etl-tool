package com.histdata.etl.config;

import com.histdata.etl.exception.ConfigurationException;

/**
 * Configuration for MySQL database connection.
 * Connection parameters are loaded from [future] section of INI file.
 */
public class MySqlConfig {

    private String host;
    private int port = 3306; // Default MySQL port
    private String database;
    private String username;
    private String password;

    /**
     * Creates default MySqlConfig.
     */
    public MySqlConfig() {
        this.host = "localhost";
        this.database = "bond";
    }

    /**
     * Validates all MySQL configuration parameters.
     *
     * @throws ConfigurationException if validation fails
     */
    public void validate() throws ConfigurationException {
        if (host == null || host.trim().isEmpty()) {
            throw new ConfigurationException("[future] host is required");
        }

        if (port < 1 || port > 65535) {
            throw new ConfigurationException(
                    String.format("[future] port must be between 1 and 65535, got: %d", port)
            );
        }

        if (database == null || database.trim().isEmpty()) {
            throw new ConfigurationException("[future] database is required");
        }

        if (username == null || username.trim().isEmpty()) {
            throw new ConfigurationException("[future] username is required");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new ConfigurationException("[future] password is required");
        }
    }

    // Getters and setters
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
