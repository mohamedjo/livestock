ALTER TABLE outbox_event
    ADD COLUMN IF NOT EXISTS correlation_id VARCHAR(255);
