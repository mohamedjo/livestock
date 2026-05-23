CREATE TABLE IF NOT EXISTS outbox_event (
    id UUID PRIMARY KEY,
    topic VARCHAR(255) NOT NULL,
    message_key VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    published_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_outbox_event_unpublished ON outbox_event (created_at)
    WHERE published_at IS NULL;
