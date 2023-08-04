CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS "bie_contention_event" (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    event VARCHAR NOT NULL,
    event_details VARCHAR NOT NULL,
    notified_at timestamp NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    PRIMARY KEY(id)
    );