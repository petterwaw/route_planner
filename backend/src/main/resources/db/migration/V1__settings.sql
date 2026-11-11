-- Application settings: always exactly one row with id = 1 (docs/data-model.md, "settings").
CREATE TABLE settings (
    id                 SMALLINT                 NOT NULL,
    home_label         VARCHAR(60)              DEFAULT 'Home' NOT NULL,
    home_address       VARCHAR(255),
    arrival_buffer_min INT                      DEFAULT 5 NOT NULL,
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_settings PRIMARY KEY (id),
    CONSTRAINT ck_settings_single_row CHECK (id = 1),
    CONSTRAINT ck_settings_arrival_buffer CHECK (arrival_buffer_min BETWEEN 0 AND 60)
);

INSERT INTO settings (id, home_label, home_address, arrival_buffer_min, updated_at)
VALUES (1, 'Home', NULL, 5, CURRENT_TIMESTAMP);
