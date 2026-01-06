package com.histdata.etl.config;

import com.histdata.etl.exception.ConfigurationException;

/**
 * Configuration for DolphinDB connection.
 * Connection parameters are loaded from [ddb] section of INI file.
 */
public class DolphinDbConfig {

    private String host;
    private int port = 8848; // Default DolphinDB port
    private String username;
    private String password;
    private String database = "dfs://Zing_MDS"; // Default database path

    /**
     * Creates default DolphinDbConfig.
     */
    public DolphinDbConfig() {
        this.host = "localhost";
    }

    /**
     * Validates all DolphinDB configuration parameters.
     *
     * @throws ConfigurationException if validation fails
     */
    public void validate() throws ConfigurationException {
        if (host == null || host.trim().isEmpty()) {
            throw new ConfigurationException("[ddb] host is required");
        }

        if (port < 1 || port > 65535) {
            throw new ConfigurationException(
                    String.format("[ddb] port must be between 1 and 65535, got: %d", port)
            );
        }

        if (username == null || username.trim().isEmpty()) {
            throw new ConfigurationException("[ddb] username is required");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new ConfigurationException("[ddb] password is required");
        }

        if (database == null || database.trim().isEmpty()) {
            throw new ConfigurationException("[ddb] database is required");
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

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
