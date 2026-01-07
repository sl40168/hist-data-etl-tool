package com.histdata.etl.config;

import com.histdata.etl.exception.ConfigurationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Config interface.
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigTest {

    @Mock
    private CosConfig mockCosConfig;

    @Mock
    private MySqlConfig mockMySqlConfig;

    @Mock
    private DolphinDbConfig mockDolphinDbConfig;

    @Test
    public void testGetters() {
        // Create a Config implementation using anonymous class
        Config config = new Config() {
            @Override
            public CosConfig getCosConfig() {
                return mockCosConfig;
            }

            @Override
            public MySqlConfig getMySqlConfig() {
                return mockMySqlConfig;
            }

            @Override
            public DolphinDbConfig getDolphinDbConfig() {
                return mockDolphinDbConfig;
            }

            @Override
            public void validate() throws ConfigurationException {
                // Do nothing for this test
            }
        };

        assertSame("COS config should be returned", mockCosConfig, config.getCosConfig());
        assertSame("MySQL config should be returned", mockMySqlConfig, config.getMySqlConfig());
        assertSame("DolphinDB config should be returned", mockDolphinDbConfig, config.getDolphinDbConfig());
    }

    @Test
    public void testValidateSuccess() throws ConfigurationException {
        // Create a Config implementation that validates all sub-configs
        Config config = new Config() {
            @Override
            public CosConfig getCosConfig() {
                return mockCosConfig;
            }

            @Override
            public MySqlConfig getMySqlConfig() {
                return mockMySqlConfig;
            }

            @Override
            public DolphinDbConfig getDolphinDbConfig() {
                return mockDolphinDbConfig;
            }

            @Override
            public void validate() throws ConfigurationException {
                mockCosConfig.validate();
                mockMySqlConfig.validate();
                mockDolphinDbConfig.validate();
            }
        };

        // Set up mocks to not throw exceptions
        doNothing().when(mockCosConfig).validate();
        doNothing().when(mockMySqlConfig).validate();
        doNothing().when(mockDolphinDbConfig).validate();

        // Should not throw
        config.validate();

        verify(mockCosConfig, times(1)).validate();
        verify(mockMySqlConfig, times(1)).validate();
        verify(mockDolphinDbConfig, times(1)).validate();
    }

    @Test(expected = ConfigurationException.class)
    public void testValidateFailure() throws ConfigurationException {
        // Create a Config implementation
        Config config = new Config() {
            @Override
            public CosConfig getCosConfig() {
                return mockCosConfig;
            }

            @Override
            public MySqlConfig getMySqlConfig() {
                return mockMySqlConfig;
            }

            @Override
            public DolphinDbConfig getDolphinDbConfig() {
                return mockDolphinDbConfig;
            }

            @Override
            public void validate() throws ConfigurationException {
                mockCosConfig.validate();
                mockMySqlConfig.validate();
                mockDolphinDbConfig.validate();
            }
        };

        // Set up COS config validation to fail
        doThrow(new ConfigurationException("COS validation failed"))
                .when(mockCosConfig).validate();

        config.validate(); // Should throw
    }

    @Test
    public void testCompositeValidationOrder() throws ConfigurationException {
        // Create a Config implementation
        Config config = new Config() {
            @Override
            public CosConfig getCosConfig() {
                return mockCosConfig;
            }

            @Override
            public MySqlConfig getMySqlConfig() {
                return mockMySqlConfig;
            }

            @Override
            public DolphinDbConfig getDolphinDbConfig() {
                return mockDolphinDbConfig;
            }

            @Override
            public void validate() throws ConfigurationException {
                mockCosConfig.validate();
                mockMySqlConfig.validate();
                mockDolphinDbConfig.validate();
            }
        };

        // Set up mocks
        doNothing().when(mockCosConfig).validate();
        doNothing().when(mockMySqlConfig).validate();
        doNothing().when(mockDolphinDbConfig).validate();

        config.validate();

        // Verify order of validation calls
        verify(mockCosConfig, times(1)).validate();
        verify(mockMySqlConfig, times(1)).validate();
        verify(mockDolphinDbConfig, times(1)).validate();
    }

    @Test
    public void testDefaultImplementation() {
        // Test that a simple implementation works
        Config config = new Config() {
            private CosConfig cosConfig = new CosConfig();
            private MySqlConfig mySqlConfig = new MySqlConfig();
            private DolphinDbConfig dolphinDbConfig = new DolphinDbConfig();

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
                // Default implementation does nothing
            }
        };

        assertNotNull("COS config should not be null", config.getCosConfig());
        assertNotNull("MySQL config should not be null", config.getMySqlConfig());
        assertNotNull("DolphinDB config should not be null", config.getDolphinDbConfig());

        // Should not throw
        try {
            config.validate();
        } catch (ConfigurationException e) {
            fail("Default validate() should not throw: " + e.getMessage());
        }
    }
}