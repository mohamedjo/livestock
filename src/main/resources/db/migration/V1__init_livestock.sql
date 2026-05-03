CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS animal (
    id UUID PRIMARY KEY,
    tag_number VARCHAR(64) UNIQUE,
    type VARCHAR(64) NOT NULL,
    breed VARCHAR(128),
    gender VARCHAR(32),
    birth_date DATE,
    status VARCHAR(32) NOT NULL,
    farm_id UUID NOT NULL,
    current_location_id UUID,
    created_at TIMESTAMP NOT NULL,
    mother_animal_id UUID REFERENCES animal (id) ON DELETE SET NULL,
    shed_id UUID,
    batch_id UUID,
    assign_date DATE,
    method_acquired VARCHAR(64),
    labels_keywords VARCHAR(512),
    internal_id VARCHAR(64),
    coloring VARCHAR(128),
    additional_tag_numbers VARCHAR(512),
    electronic_id VARCHAR(128),
    marking_left VARCHAR(255),
    marking_right VARCHAR(255),
    description TEXT
);

CREATE INDEX IF NOT EXISTS idx_animal_farm_id ON animal (farm_id);
CREATE INDEX IF NOT EXISTS idx_animal_tag_number ON animal (tag_number);
CREATE INDEX IF NOT EXISTS idx_animal_mother_animal_id ON animal (mother_animal_id);
CREATE INDEX IF NOT EXISTS idx_animal_shed_id ON animal (shed_id);
CREATE INDEX IF NOT EXISTS idx_animal_batch_id ON animal (batch_id);

CREATE TABLE IF NOT EXISTS animal_feed_type (
    animal_id UUID NOT NULL REFERENCES animal (id) ON DELETE CASCADE,
    feed_type VARCHAR(128) NOT NULL,
    PRIMARY KEY (animal_id, feed_type)
);

CREATE INDEX IF NOT EXISTS idx_animal_feed_type_animal_id ON animal_feed_type (animal_id);

CREATE TABLE IF NOT EXISTS animal_history (
    id UUID PRIMARY KEY,
    animal_id UUID NOT NULL,
    event_type VARCHAR(128) NOT NULL,
    event_data JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_animal_history_animal FOREIGN KEY (animal_id) REFERENCES animal (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_animal_history_animal_id ON animal_history (animal_id);
CREATE INDEX IF NOT EXISTS idx_animal_history_event_type ON animal_history (event_type);
