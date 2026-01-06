package com.histdata.etl.cli;

import com.histdata.etl.config.Config;
import com.histdata.etl.model.JobStatus;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

/**
 * Unit tests for EtlCli.
 */
public class EtlCliTest {
    private EtlCli cli;

    @Before
    public void setUp() {
        cli = new EtlCli();
    }

    @Test
    public void testHelpOption() throws Exception {
        cli.run(new String[]{"--help"});
    }

    @Test
    public void testVersionOption() throws Exception {
        cli.run(new String[]{"--version"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoArguments() throws Exception {
        cli.run(new String[]{});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidStartDate() throws Exception {
        cli.run(new String[]{"invalid", "20250101"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEndDate() throws Exception {
        cli.run(new String[]{"20250101", "invalid"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStartAfterEnd() throws Exception {
        cli.run(new String[]{"20250102", "20250101"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingEndDate() throws Exception {
        cli.run(new String[]{"20250101"});
    }
}
