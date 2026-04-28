CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS animal (
    id UUID PRIMARY KEY,
    tag_number VARCHAR(64) UNIQUE NOT NULL,
    type VARCHAR(64) NOT NULL,
    breed VARCHAR(128),
    gender VARCHAR(32),
    birth_date DATE,
    status VARCHAR(32) NOT NULL,
    farm_id UUID NOT NULL,
    current_location_id UUID,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_animal_farm_id ON animal (farm_id);
CREATE INDEX IF NOT EXISTS idx_animal_tag_number ON animal (tag_number);

CREATE TABLE IF NOT EXISTS animal_history (
    id UUID PRIMARY KEY,
    animal_id UUID NOT NULL,
    event_type VARCHAR(128) NOT NULL,
    event_data JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_animal_history_animal FOREIGN KEY (animal_id) REFERENCES animal(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_animal_history_animal_id ON animal_history (animal_id);
CREATE INDEX IF NOT EXISTS idx_animal_history_event_type ON animal_history (event_type);
