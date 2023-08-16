-- Drop the old table
DROP TABLE IF EXISTS "bie_contention_event";

-- Create the new table
CREATE TABLE IF NOT EXISTS "bie_contention_event"
(
    id                             uuid DEFAULT uuid_generate_v4() NOT NULL,
    notified_at                    TIMESTAMP NOT NULL,
    occurred_at                    TIMESTAMP NOT NULL,
    event_type                     VARCHAR(255),
    claim_id                       BIGINT,
    contention_id                  BIGINT,
    diagnostic_type_code           VARCHAR(255),
    contention_classification_code VARCHAR(255),
    created_at                     TIMESTAMP NOT NULL,
    updated_at                     TIMESTAMP NOT NULL,
    PRIMARY KEY(id)
);
