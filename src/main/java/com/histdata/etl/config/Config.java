package com.histdata.etl.config;

import com.histdata.etl.exception.ConfigurationException;

/**
 * Interface for ETL tool configuration.
 * Defines the contract for accessing connection details for all systems.
 */
public interface Config {

    /**
     * Returns COS configuration.
     *
     * @return CosConfig instance
     */
    CosConfig getCosConfig();

    /**
     * Returns MySQL configuration.
     *
     * @return MySqlConfig instance
     */
    MySqlConfig getMySqlConfig();

    /**
     * Returns DolphinDB configuration.
     *
     * @return DolphinDbConfig instance
     */
    DolphinDbConfig getDolphinDbConfig();

    /**
     * Validates all configuration parameters.
     *
     * @throws ConfigurationException if validation fails
     */
    void validate() throws ConfigurationException;
}
