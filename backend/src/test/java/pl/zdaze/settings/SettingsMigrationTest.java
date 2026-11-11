package pl.zdaze.settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SettingsMigrationTest {

    @Autowired
    JdbcClient jdbc;

    @Test
    void settingsTableHasExactlyOneRowWithDocumentedDefaults() {
        var rows = jdbc.sql("SELECT id, home_label, home_address, arrival_buffer_min, updated_at FROM settings")
                .query()
                .listOfRows();

        assertThat(rows).hasSize(1);
        Map<String, Object> row = rows.getFirst();
        assertThat(((Number) row.get("ID")).intValue()).isEqualTo(1);
        assertThat(row.get("HOME_LABEL")).isEqualTo("Dom");
        assertThat(row.get("HOME_ADDRESS")).isNull();
        assertThat(((Number) row.get("ARRIVAL_BUFFER_MIN")).intValue()).isEqualTo(5);
        assertThat(row.get("UPDATED_AT")).isNotNull();
    }

    @Test
    void databaseRejectsASecondSettingsRow() {
        assertThatThrownBy(() -> jdbc.sql("INSERT INTO settings (id, home_label, arrival_buffer_min, updated_at)"
                                + " VALUES (2, 'Drugi', 5, CURRENT_TIMESTAMP)")
                        .update())
                .isInstanceOf(DataAccessException.class);
    }
}
