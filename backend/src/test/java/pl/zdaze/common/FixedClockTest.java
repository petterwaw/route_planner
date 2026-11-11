package pl.zdaze.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "app.clock.fixed=2026-11-17T15:40:00+01:00")
@ActiveProfiles("test")
class FixedClockTest {

    @Autowired
    Clock clock;

    @Test
    void clockBeanStandsStillAtTheConfiguredMoment() {
        assertThat(clock.instant()).isEqualTo(Instant.parse("2026-11-17T14:40:00Z"));
    }
}
