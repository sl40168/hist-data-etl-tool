package com.histdata.etl.config;

import com.histdata.etl.exception.ConfigurationException;

/**
 * Configuration for COS (Tencent Cloud Object Storage) connection.
 * Connection parameters are loaded from [xbond] section of INI file.
 */
public class CosConfig {

    private String domain;
    private String region;
    private String bucket;
    // Credentials are passed via JVM parameters
    private String secretId;
    private String secretKey;
    private String trustKey;

    /**
     * Creates default CosConfig with empty values.
     */
    public CosConfig() {
        this.domain = "";
        this.region = "";
        this.bucket = "";
    }

    /**
     * Validates all COS configuration parameters.
     *
     * @throws ConfigurationException if validation fails
     */
    public void validate() throws ConfigurationException {
        if (domain == null || domain.trim().isEmpty()) {
            throw new ConfigurationException("[xbond] domain is required");
        }

        if (region == null || region.trim().isEmpty()) {
            throw new ConfigurationException("[xbond] region is required");
        }

        if (bucket == null || bucket.trim().isEmpty()) {
            throw new ConfigurationException("[xbond] bucket is required");
        }

        // Validate URL format for domain
        if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
            domain = "https://" + domain;
        }
    }

    // Getters and setters
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTrustKey() {
        return trustKey;
    }

    public void setTrustKey(String trustKey) {
        this.trustKey = trustKey;
    }
}
