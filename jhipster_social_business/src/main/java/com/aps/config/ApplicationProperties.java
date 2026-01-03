package com.aps.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Whatsapp Product Service Pro.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();
    private final RateLimiting rateLimiting = new RateLimiting();

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    public RateLimiting getRateLimiting() {
        return rateLimiting;
    }

    private final Licensing licensing = new Licensing();

    public Licensing getLicensing() {
        return licensing;
    }

    public static class Licensing {

        private String url;
        private String cron;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }
    }

    // jhipster-needle-application-properties-property-getter

    public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }

    public static class RateLimiting {

        private Boolean enabled;

        private Long capacity;

        private Integer durationInMinutes;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Long getCapacity() {
            return capacity;
        }

        public void setCapacity(Long capacity) {
            this.capacity = capacity;
        }

        public Integer getDurationInMinutes() {
            return durationInMinutes;
        }

        public void setDurationInMinutes(Integer durationInMinutes) {
            this.durationInMinutes = durationInMinutes;
        }
    }
    // jhipster-needle-application-properties-property-class
}
