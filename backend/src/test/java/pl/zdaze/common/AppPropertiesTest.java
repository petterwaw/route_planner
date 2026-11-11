package pl.zdaze.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AppPropertiesTest {

    @Autowired
    AppProperties props;

    @Autowired
    Clock clock;

    @Test
    void defaultsMatchTheDocumentedConfiguration() {
        assertThat(props.zone()).isEqualTo(ZoneId.of("Europe/Warsaw"));
        assertThat(props.clock().fixed()).isNull();

        assertThat(props.routing().provider()).isEqualTo("mock");
        assertThat(props.routing().dailyCallLimit()).isEqualTo(150);
        assertThat(props.routing().trafficAware()).isFalse();
        assertThat(props.routing().trafficWindowMin()).isEqualTo(120);

        assertThat(props.planning().driveParkingBufferMin()).isEqualTo(5);
        assertThat(props.planning().walkMaxKm()).isEqualTo(3);
        assertThat(props.planning().bikeMaxKm()).isEqualTo(15);
        assertThat(props.planning().transitMaxOptions()).isEqualTo(3);
        assertThat(props.planning().recommendDurationFactor()).isEqualTo(1.5);
        assertThat(props.planning().recommendDurationExtraMin()).isEqualTo(10);

        assertThat(props.cache().plannedTtlMin()).isEqualTo(360);
        assertThat(props.cache().nearTtlMin()).isEqualTo(10);
        assertThat(props.cache().nearWindowMin()).isEqualTo(120);
        assertThat(props.cache().liveTtlSec()).isEqualTo(120);
        assertThat(props.cache().replanMinIntervalSec()).isEqualTo(30);

        assertThat(props.ai().provider()).isEqualTo("mock");
        assertThat(props.ai().responseFormat()).isEqualTo("json_schema");
        assertThat(props.ai().dailyCallLimit()).isEqualTo(20);
        assertThat(props.ai().maxImageBytes()).isEqualTo(5_242_880);
        assertThat(props.ai().maxItems()).isEqualTo(60);
    }

    @Test
    void clockBeanUsesTheConfiguredZone() {
        assertThat(clock.getZone()).isEqualTo(ZoneId.of("Europe/Warsaw"));
    }

    @Test
    void apiKeysNeverAppearInToString() {
        var withKeys = new AppProperties(
                props.zone(),
                props.clock(),
                new AppProperties.Routing(
                        "google", new AppProperties.Routing.Google("secret-google-key"), 150, false, 120),
                props.planning(),
                props.cache(),
                new AppProperties.Ai("openai", "https://example.test", "secret-ai-key", "m", "json_schema", 20, 1, 1));

        assertThat(withKeys.toString()).doesNotContain("secret-google-key").doesNotContain("secret-ai-key");
    }
}
