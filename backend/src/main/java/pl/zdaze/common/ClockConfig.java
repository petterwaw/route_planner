package pl.zdaze.common;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** The single source of "now" for the whole application. Never call {@code now()} without it. */
@Configuration
class ClockConfig {

    @Bean
    Clock clock(AppProperties props) {
        return clockFor(props.zone(), props.clock().fixed());
    }

    /** System time in {@code zone}, or a clock frozen at {@code fixed} when it is set. */
    static Clock clockFor(ZoneId zone, OffsetDateTime fixed) {
        return fixed == null ? Clock.system(zone) : Clock.fixed(fixed.toInstant(), zone);
    }
}
