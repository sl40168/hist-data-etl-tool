package com.histdata.etl.cli;

import com.histdata.etl.config.*;
import com.histdata.etl.datasource.*;
import com.histdata.etl.exception.*;
import com.histdata.etl.loader.DolphinDbLoader;
import com.histdata.etl.model.*;
import com.histdata.etl.transformer.*;
import com.histdata.etl.util.*;
import com.histdata.etl.loader.DataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVRecord;
import java.util.Map;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

/**
 * Main entry point for the ETL CLI tool.
 * Orchestrates extraction, transformation, and loading of historical financial data.
 */
public class EtlCli {
    private static final Logger logger = LoggerFactory.getLogger(EtlCli.class);
    private static final String VERSION = "1.0.0";
    private static final String BUILD_DATE = "2025-01-06";

    private EtlJobContext context;
    private ProgressMonitor progressMonitor;
    private FileLock fileLock;

    public static void main(String[] args) {
        EtlCli cli = new EtlCli();
        try {
            cli.run(args);
            System.exit(0);
        } catch (InsufficientMemoryException e) {
            logger.error("Insufficient memory: {}", e.getMessage());
            System.exit(7);
        } catch (ConcurrentExecutionException e) {
            logger.error("Concurrent execution: {}", e.getMessage());
            System.exit(8);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid arguments: {}", e.getMessage());
            System.exit(1);
        } catch (ConfigurationException e) {
            logger.error("Configuration error: {}", e.getMessage());
            System.exit(2);
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            System.exit(9);
        }
    }

    public void run(String[] args) throws Exception {
        if (args.length == 0 || "--help".equals(args[0])) {
            printHelp();
            return;
        }

        if ("--version".equals(args[0])) {
            printVersion();
            return;
        }

        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: java -jar etl-tool.jar <START_DATE> <END_DATE> [CONFIG_FILE]");
        }

        String startDateStr = args[0];
        String endDateStr = args[1];
        String configPath = args.length > 2 ? args[2] : null;

        LocalDate startDate = LocalDate.parse(startDateStr, DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.BASIC_ISO_DATE);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must not be after end date");
        }

        fileLock = FileLock.acquireLock();

        Config config = loadConfig(configPath);
        context = new EtlJobContext(startDate, endDate, configPath, config, UUID.randomUUID().toString());
        progressMonitor = new ProgressMonitor(context);

        checkMemory();

        processDateRange(startDate, endDate);

        progressMonitor.displaySummary();
    }

    private Config loadConfig(String configPath) throws ConfigurationException {
        IniConfigLoader loader = new IniConfigLoader();
        if (configPath != null && !configPath.isEmpty()) {
            File configFile = new File(configPath);
            if (!configFile.exists()) {
                throw new ConfigurationException("Configuration file not found: " + configPath);
            }
            return loader.load(configPath);
        } else {
            return loader.loadDefault();
        }
    }

    private void checkMemory() throws InsufficientMemoryException {
        long availableMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        long usableMemory = maxMemory - (totalMemory - availableMemory);

        long daysToProcess = context.getEndDate().toEpochDay() - context.getStartDate().toEpochDay() + 1;
        long estimatedMemoryRequirement = daysToProcess * 500 * 1024 * 1024; // 500MB per day

        logger.info("Memory check: available={}MB, required={}MB", usableMemory / 1024 / 1024, estimatedMemoryRequirement / 1024 / 1024);

        if (estimatedMemoryRequirement > usableMemory * 0.9) {
            throw new InsufficientMemoryException(
                    String.format("Insufficient memory: available %d MB, required %d MB. " +
                                    "Please increase heap size using -Xmx option.",
                            usableMemory / 1024 / 1024, estimatedMemoryRequirement / 1024 / 1024));
        }
    }

    private void processDateRange(LocalDate startDate, LocalDate endDate) throws Exception {
        int totalDays = (int) ((endDate.toEpochDay() - startDate.toEpochDay()) + 1);
        int currentDay = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            currentDay++;
            logger.info("Processing day {}/{}: {}", currentDay, totalDays, date);

            context.setCurrentDate(date);
            context.setJobStatus(JobStatus.EXTRACTING);

            processSingleDay(date);

            if (currentDay < totalDays) {
                logger.info("Day {} completed successfully", date);
            }
        }

        context.setJobStatus(JobStatus.COMPLETED);
        logger.info("All {} days processed successfully", totalDays);
    }

    private void processSingleDay(LocalDate date) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        CompletableFuture<List<XbondQuoteRecord>> quoteFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return extractAndTransformQuotes(date);
            } catch (Exception e) {
                throw new RuntimeException("Failed to extract quotes", e);
            }
        }, executorService);

        CompletableFuture<List<XbondTradeRecord>> tradeFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return extractAndTransformTrades(date);
            } catch (Exception e) {
                throw new RuntimeException("Failed to extract trades", e);
            }
        }, executorService);

        CompletableFuture<List<FutureQuoteRecord>> futureFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return extractAndTransformFutures(date);
            } catch (Exception e) {
                throw new RuntimeException("Failed to extract futures", e);
            }
        }, executorService);

        CompletableFuture.allOf(quoteFuture, tradeFuture, futureFuture).join();

        List<XbondQuoteRecord> quotes = quoteFuture.get();
        List<XbondTradeRecord> trades = tradeFuture.get();
        List<FutureQuoteRecord> futures = futureFuture.get();

        executorService.shutdown();

        context.setJobStatus(JobStatus.TRANSFORMING);
        logger.info("Extracted {} quotes, {} trades, {} futures", quotes.size(), trades.size(), futures.size());

        List<Object> allRecords = new ArrayList<>();
        allRecords.addAll(quotes);
        allRecords.addAll(trades);
        allRecords.addAll(futures);

        allRecords.sort((r1, r2) -> {
            Timestamp t1 = null;
            Timestamp t2 = null;
            if (r1 instanceof XbondQuoteRecord) {
                t1 = ((XbondQuoteRecord) r1).getReceiveTime();
            } else if (r1 instanceof XbondTradeRecord) {
                t1 = ((XbondTradeRecord) r1).getReceiveTime();
            } else if (r1 instanceof FutureQuoteRecord) {
                t1 = ((FutureQuoteRecord) r1).getReceiveTime();
            }

            if (r2 instanceof XbondQuoteRecord) {
                t2 = ((XbondQuoteRecord) r2).getReceiveTime();
            } else if (r2 instanceof XbondTradeRecord) {
                t2 = ((XbondTradeRecord) r2).getReceiveTime();
            } else if (r2 instanceof FutureQuoteRecord) {
                t2 = ((FutureQuoteRecord) r2).getReceiveTime();
            }

            return t1.compareTo(t2);
        });

        logger.info("Sorted {} records by receive_time", allRecords.size());

        context.setJobStatus(JobStatus.LOADING);

        DolphinDbLoader loader = new DolphinDbLoader(context.getConfig().getDolphinDbConfig());
        try {
            loader.initialize();
            loader.createTemporaryTables();
            loader.load(allRecords);
            loader.cleanup();
        } finally {
            loader.close();
        }

        logger.info("Loaded {} records into DolphinDB", allRecords.size());

        context.getProgressStatus().setLoadedRecords(allRecords.size());
    }

    private List<XbondQuoteRecord> extractAndTransformQuotes(LocalDate date) throws Exception {
        XbondQuoteExtractor extractor = new XbondQuoteExtractor(context.getConfig().getCosConfig());
        XbondQuoteTransformer transformer = new XbondQuoteTransformer();

        List<CSVRecord> rawRecords = extractor.extract(date);

        return rawRecords.stream()
                .map(record -> {
                    try {
                        return transformer.transform(record);
                    } catch (Exception e) {
                        logger.warn("Failed to transform quote record: {}", e.getMessage());
                        return null;
                    }
                })
                .filter(record -> record != null)
                .collect(Collectors.toList());
    }

    private List<XbondTradeRecord> extractAndTransformTrades(LocalDate date) throws Exception {
        XbondTradeExtractor extractor = new XbondTradeExtractor(context.getConfig().getCosConfig());
        XbondTradeTransformer transformer = new XbondTradeTransformer();

        List<CSVRecord> rawRecords = extractor.extract(date);

        return rawRecords.stream()
                .map(record -> {
                    try {
                        return transformer.transform(record);
                    } catch (Exception e) {
                        logger.warn("Failed to transform trade record: {}", e.getMessage());
                        return null;
                    }
                })
                .filter(record -> record != null)
                .collect(Collectors.toList());
    }

    private List<FutureQuoteRecord> extractAndTransformFutures(LocalDate date) throws Exception {
        MySqlFutureExtractor extractor = new MySqlFutureExtractor(context.getConfig().getMySqlConfig());
        FutureQuoteTransformer transformer = new FutureQuoteTransformer();

        List<Map<String, Object>> rawRecords = extractor.extract(date);

        return rawRecords.stream()
                .map(record -> {
                    try {
                        return transformer.transform(record);
                    } catch (Exception e) {
                        logger.warn("Failed to transform future record: {}", e.getMessage());
                        return null;
                    }
                })
                .filter(record -> record != null)
                .collect(Collectors.toList());
    }

    private void printHelp() {
        System.out.println("ETL CLI Tool - Historical Financial Data ETL");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java -jar etl-tool.jar <START_DATE> <END_DATE> [CONFIG_FILE]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  START_DATE    Start date in YYYYMMDD format");
        System.out.println("  END_DATE      End date in YYYYMMDD format (inclusive)");
        System.out.println("  CONFIG_FILE   Path to INI configuration file (optional)");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --help        Display this help message");
        System.out.println("  --version     Display version information");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar etl-tool.jar 20250101 20250101 config.ini");
        System.out.println("  java -jar etl-tool.jar 20250101 20250103");
        System.out.println();
        System.out.println("Exit Codes:");
        System.out.println("  0 - Success");
        System.out.println("  1 - Invalid arguments");
        System.out.println("  2 - Configuration error");
        System.out.println("  3 - Connection error");
        System.out.println("  4 - Extraction error");
        System.out.println("  5 - Transformation error");
        System.out.println("  6 - Loading error");
        System.out.println("  7 - Memory error");
        System.out.println("  8 - Concurrent execution");
        System.out.println("  9 - Unexpected error");
    }

    private void printVersion() {
        System.out.println("ETL CLI Tool v" + VERSION);
        System.out.println("Build Date: " + BUILD_DATE);
        System.out.println("Java Version: " + System.getProperty("java.version"));
    }
}
