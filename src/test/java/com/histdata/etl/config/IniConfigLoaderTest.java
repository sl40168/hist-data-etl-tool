package com.histdata.etl.config;

import com.histdata.etl.exception.ConfigurationException;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for IniConfigLoader.
 */
@RunWith(MockitoJUnitRunner.class)
public class IniConfigLoaderTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Mock
    private FileBasedConfigurationBuilder<INIConfiguration> mockBuilder;

    @Mock
    private INIConfiguration mockConfig;

    private File tempConfigFile;

    @Before
    public void setUp() throws Exception {
        // Create a temporary config file with valid content
        tempConfigFile = tempFolder.newFile("config.ini");
        writeValidConfig(tempConfigFile);
    }

    @Test
    public void testLoadSuccess() throws ConfigurationException {
        // This test uses the actual file loading mechanism
        // Since IniConfigLoader uses static methods, we'll test with real file
        
        // Create a valid config file
        String configPath = tempConfigFile.getAbsolutePath();
        
        // Load configuration
        Config config = IniConfigLoader.load(configPath);
        
        assertNotNull("Config should not be null", config);
        assertNotNull("COS config should not be null", config.getCosConfig());
        assertNotNull("MySQL config should not be null", config.getMySqlConfig());
        assertNotNull("DolphinDB config should not be null", config.getDolphinDbConfig());
        
        // Validate should succeed
        config.validate();
    }

    @Test(expected = ConfigurationException.class)
    public void testLoadMissingSection() throws ConfigurationException, IOException {
        // Create config file missing a required section
        File badFile = tempFolder.newFile("bad.ini");
        try (FileWriter writer = new FileWriter(badFile)) {
            writer.write("[xbond]\n");
            writer.write("domain = https://ap-beijing.myqcloud.com\n");
            writer.write("region = ap-beijing\n");
            writer.write("bucket = bond-data\n");
            writer.write("\n");
            // Missing [future] and [ddb] sections
        }
        
        IniConfigLoader.load(badFile.getAbsolutePath());
    }

    @Test(expected = ConfigurationException.class)
    public void testLoadFileNotFound() throws ConfigurationException {
        IniConfigLoader.load("nonexistent.ini");
    }

    @Test
    public void testLoadDefault() throws ConfigurationException {
        // This test depends on existence of src/main/resources/config.ini
        // We'll mock system properties to avoid actual COS credential errors
        
        // Set dummy COS credentials via system properties
        System.setProperty("cos.secret.id", "test-secret-id");
        System.setProperty("cos.secret.key", "test-secret-key");
        System.setProperty("cos.trust.key", "test-trust-key");
        
        try {
            Config config = IniConfigLoader.loadDefault();
            assertNotNull("Default config should not be null", config);
            
            // Validate should succeed with dummy credentials
            config.validate();
        } finally {
            // Clean up system properties
            System.clearProperty("cos.secret.id");
            System.clearProperty("cos.secret.key");
            System.clearProperty("cos.trust.key");
        }
    }

    @Test
    public void testLoadCosCredentialsFromJvm() throws Exception {
        // Test that COS credentials are loaded from JVM parameters
        System.setProperty("cos.secret.id", "test-id-123");
        System.setProperty("cos.secret.key", "test-key-456");
        System.setProperty("cos.trust.key", "test-trust-789");
        
        try {
            // Load configuration
            Config config = IniConfigLoader.load(tempConfigFile.getAbsolutePath());
            
            // Check that credentials were loaded
            CosConfig cosConfig = config.getCosConfig();
            assertNotNull("COS config should not be null", cosConfig);
            assertEquals("COS secret ID should match", "test-id-123", cosConfig.getSecretId());
            assertEquals("COS secret key should match", "test-key-456", cosConfig.getSecretKey());
            assertEquals("COS trust key should match", "test-trust-789", cosConfig.getTrustKey());
        } finally {
            System.clearProperty("cos.secret.id");
            System.clearProperty("cos.secret.key");
            System.clearProperty("cos.trust.key");
        }
    }

    @Test
    public void testLoadCosCredentialsMissing() throws Exception {
        // Test that missing credentials don't cause validation failure
        // (credentials are optional at load time - will fail at connection time)
        System.clearProperty("cos.secret.id");
        System.clearProperty("cos.secret.key");
        System.clearProperty("cos.trust.key");
        
        Config config = IniConfigLoader.load(tempConfigFile.getAbsolutePath());
        
        CosConfig cosConfig = config.getCosConfig();
        assertNull("COS secret ID should be null when not provided", cosConfig.getSecretId());
        assertNull("COS secret key should be null when not provided", cosConfig.getSecretKey());
        assertNull("COS trust key should be null when not provided", cosConfig.getTrustKey());
        
        // Validation should still pass (credentials are validated separately)
        config.validate();
    }

    @Test
    public void testValidateConfigSuccess() throws ConfigurationException {
        // Test validation with valid config
        Config config = IniConfigLoader.load(tempConfigFile.getAbsolutePath());
        config.validate(); // Should not throw
    }

    @Test(expected = ConfigurationException.class)
    public void testValidateConfigFailure() throws ConfigurationException, IOException {
        // Create config file with invalid port
        File invalidFile = tempFolder.newFile("invalid.ini");
        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write("[xbond]\n");
            writer.write("domain = https://ap-beijing.myqcloud.com\n");
            writer.write("region = ap-beijing\n");
            writer.write("bucket = bond-data\n");
            writer.write("\n");
            writer.write("[future]\n");
            writer.write("host = localhost\n");
            writer.write("port = 99999\n"); // Invalid port
            writer.write("database = bond\n");
            writer.write("username = testuser\n");
            writer.write("password = testpass\n");
            writer.write("\n");
            writer.write("[ddb]\n");
            writer.write("host = localhost\n");
            writer.write("port = 8848\n");
            writer.write("username = admin\n");
            writer.write("password = 123456\n");
            writer.write("database = dfs://Zing_MDS\n");
        }
        
        Config config = IniConfigLoader.load(invalidFile.getAbsolutePath());
        config.validate(); // Should throw
    }

    private void writeValidConfig(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("[xbond]\n");
            writer.write("domain = https://ap-beijing.myqcloud.com\n");
            writer.write("region = ap-beijing\n");
            writer.write("bucket = bond-data\n");
            writer.write("\n");
            writer.write("[future]\n");
            writer.write("host = localhost\n");
            writer.write("port = 3306\n");
            writer.write("database = bond\n");
            writer.write("username = testuser\n");
            writer.write("password = testpass\n");
            writer.write("\n");
            writer.write("[ddb]\n");
            writer.write("host = localhost\n");
            writer.write("port = 8848\n");
            writer.write("username = admin\n");
            writer.write("password = 123456\n");
            writer.write("database = dfs://Zing_MDS\n");
        }
    }
}