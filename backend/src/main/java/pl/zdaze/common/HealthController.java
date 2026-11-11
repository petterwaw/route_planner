package pl.zdaze.common;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** {@code GET /api/health} (docs/api.md, "Health"). */
@RestController
class HealthController {

    private final Clock clock;
    private final AppProperties props;

    HealthController(Clock clock, AppProperties props) {
        this.clock = clock;
        this.props = props;
    }

    @GetMapping("/api/health")
    HealthResponse health() {
        return new HealthResponse(
                "ok",
                OffsetDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS),
                props.routing().provider(),
                props.ai().provider());
    }

    public record HealthResponse(String status, OffsetDateTime now, String routingProvider, String aiProvider) {}
}
