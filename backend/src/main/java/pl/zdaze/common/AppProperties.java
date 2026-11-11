package pl.zdaze.common;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Application settings under the {@code app} prefix. Defaults live in {@code application.yml} and
 * match the table in {@code docs/architecture.md}. API keys are masked in {@code toString()} so they
 * cannot leak into logs.
 *
 * <p>{@code @DefaultValue} on a nested group binds an empty instance instead of null when none of its
 * values are set (e.g. an empty {@code APP_CLOCK_FIXED}).
 */
@ConfigurationProperties("app")
public record AppProperties(
        ZoneId zone, @DefaultValue ClockSettings clock, Routing routing, Planning planning, Cache cache, Ai ai) {

    /** {@code fixed} is null unless the clock is frozen for a demo or a manual test. */
    public record ClockSettings(OffsetDateTime fixed) {}

    public record Routing(
            String provider,
            @DefaultValue Google google,
            int dailyCallLimit,
            boolean trafficAware,
            int trafficWindowMin) {

        public record Google(String apiKey) {
            @Override
            public String toString() {
                return "Google[apiKey=" + mask(apiKey) + "]";
            }
        }
    }

    public record Planning(
            int driveParkingBufferMin,
            double walkMaxKm,
            double bikeMaxKm,
            int transitMaxOptions,
            double recommendDurationFactor,
            int recommendDurationExtraMin) {}

    public record Cache(
            int plannedTtlMin, int nearTtlMin, int nearWindowMin, int liveTtlSec, int replanMinIntervalSec) {}

    public record Ai(
            String provider,
            String baseUrl,
            String apiKey,
            String model,
            String responseFormat,
            int dailyCallLimit,
            long maxImageBytes,
            int maxItems) {

        @Override
        public String toString() {
            return "Ai[provider=" + provider + ", baseUrl=" + baseUrl + ", apiKey=" + mask(apiKey) + ", model="
                    + model + ", responseFormat=" + responseFormat + ", dailyCallLimit=" + dailyCallLimit
                    + ", maxImageBytes=" + maxImageBytes + ", maxItems=" + maxItems + "]";
        }
    }

    private static String mask(String secret) {
        return secret == null || secret.isBlank() ? "<empty>" : "<set>";
    }
}
