package pl.zdaze.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class ClockConfigTest {

    private static final ZoneId WARSAW = ZoneId.of("Europe/Warsaw");

    @Test
    void fixedMomentFreezesTheClock() {
        var fixed = OffsetDateTime.parse("2026-11-17T15:40:00+01:00");

        var clock = ClockConfig.clockFor(WARSAW, fixed);

        assertThat(clock.instant()).isEqualTo(Instant.parse("2026-11-17T14:40:00Z"));
        assertThat(clock.getZone()).isEqualTo(WARSAW);
    }

    @Test
    void withoutFixedMomentTheClockFollowsSystemTimeInTheConfiguredZone() {
        var before = Instant.now(java.time.Clock.systemUTC());

        var clock = ClockConfig.clockFor(WARSAW, null);

        assertThat(clock.getZone()).isEqualTo(WARSAW);
        assertThat(clock.instant()).isBetween(before, before.plusSeconds(5));
    }
}
