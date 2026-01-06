package com.histdata.etl.config;

import com.histdata.etl.exception.ConfigurationException;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Loads and validates INI configuration files for ETL tool.
 * Uses Apache Commons Configuration library for parsing.
 */
public class IniConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(IniConfigLoader.class);

    /**
     * Loads configuration from specified INI file or embedded default.
     *
     * @param configPath Path to configuration file, or null for embedded default
     * @return Loaded Config instance
     * @throws ConfigurationException if file not found or validation fails
     */
    public static Config load(String configPath) throws ConfigurationException {
        try {
            String configFilePath = (configPath != null) ? configPath :
                    "src/main/resources/config.ini";

            logger.info("Loading configuration from: {}", configFilePath);
            Parameters params = new Parameters();
            FileBasedConfigurationBuilder<INIConfiguration> builder =
                    new FileBasedConfigurationBuilder<>(INIConfiguration.class)
                            .configure(params.fileBased()
                                    .setFile(new File(configFilePath)));
            INIConfiguration config = builder.getConfiguration();

            // Validate required sections exist
            validateSections(config);

            // Load configuration sections
            CosConfig cosConfig = loadCosConfig(config);
            MySqlConfig mySqlConfig = loadMySqlConfig(config);
            DolphinDbConfig dolphinDbConfig = loadDolphinDbConfig(config);

            // Load COS credentials from JVM parameters
            loadCosCredentialsFromJvm(cosConfig);

            // Create composite config
            Config loadedConfig = new Config() {
                @Override
                public CosConfig getCosConfig() {
                    return cosConfig;
                }

                @Override
                public MySqlConfig getMySqlConfig() {
                    return mySqlConfig;
                }

                @Override
                public DolphinDbConfig getDolphinDbConfig() {
                    return dolphinDbConfig;
                }

                @Override
                public void validate() throws ConfigurationException {
                    cosConfig.validate();
                    mySqlConfig.validate();
                    dolphinDbConfig.validate();
                }
            };

            logger.info("Configuration loaded successfully");
            return loadedConfig;

        } catch (org.apache.commons.configuration2.ex.ConfigurationException e) {
            throw new ConfigurationException("Failed to parse configuration file: " + e.getMessage(), e);
        }
    }

    /**
     * Validates that all required sections exist in configuration.
     */
    private static void validateSections(INIConfiguration config) throws ConfigurationException {
        String[] requiredSections = {"xbond", "future", "ddb"};

        for (String section : requiredSections) {
            if (!config.getSections().contains(section)) {
                throw new ConfigurationException(
                        String.format("Missing required section in configuration file: [%s]", section)
                );
            }
        }
    }

    /**
     * Loads COS configuration from [xbond] section.
     */
    private static CosConfig loadCosConfig(INIConfiguration config) {
        CosConfig cosConfig = new CosConfig();
        cosConfig.setDomain(config.getString("xbond.domain", ""));
        cosConfig.setRegion(config.getString("xbond.region", ""));
        cosConfig.setBucket(config.getString("xbond.bucket", ""));
        return cosConfig;
    }

    /**
     * Loads MySQL configuration from [future] section.
     */
    private static MySqlConfig loadMySqlConfig(INIConfiguration config) {
        MySqlConfig mySqlConfig = new MySqlConfig();
        mySqlConfig.setHost(config.getString("future.host", "localhost"));
        mySqlConfig.setPort(config.getInt("future.port", 3306));
        mySqlConfig.setDatabase(config.getString("future.database", ""));
        mySqlConfig.setUsername(config.getString("future.username", ""));
        mySqlConfig.setPassword(config.getString("future.password", ""));
        return mySqlConfig;
    }

    /**
     * Loads DolphinDB configuration from [ddb] section.
     */
    private static DolphinDbConfig loadDolphinDbConfig(INIConfiguration config) {
        DolphinDbConfig dolphinDbConfig = new DolphinDbConfig();
        dolphinDbConfig.setHost(config.getString("ddb.host", "localhost"));
        dolphinDbConfig.setPort(config.getInt("ddb.port", 8848));
        dolphinDbConfig.setUsername(config.getString("ddb.username", ""));
        dolphinDbConfig.setPassword(config.getString("ddb.password", ""));
        dolphinDbConfig.setDatabase(config.getString("ddb.database", "dfs://Zing_MDS"));
        return dolphinDbConfig;
    }

    /**
     * Loads COS credentials from JVM system properties.
     * Credentials are passed via: -Dcos.secret.id, -Dcos.secret.key, -Dcos.trust.key
     */
    private static void loadCosCredentialsFromJvm(CosConfig cosConfig) {
        String secretId = System.getProperty("cos.secret.id");
        String secretKey = System.getProperty("cos.secret.key");
        String trustKey = System.getProperty("cos.trust.key");

        if (secretId != null && !secretId.trim().isEmpty()) {
            cosConfig.setSecretId(secretId.trim());
            logger.debug("COS secret.id loaded from JVM parameter");
        }

        if (secretKey != null && !secretKey.trim().isEmpty()) {
            cosConfig.setSecretKey(secretKey.trim());
            logger.debug("COS secret.key loaded from JVM parameter");
        }

            if (trustKey != null && !trustKey.trim().isEmpty()) {
            cosConfig.setTrustKey(trustKey.trim());
            logger.debug("COS trust.key loaded from JVM parameter");
        }
    }

    /**
     * Loads default configuration from embedded resources.
     *
     * @return Loaded Config instance
     * @throws ConfigurationException if loading fails
     */
    public static Config loadDefault() throws ConfigurationException {
        return load(null);
    }
}
