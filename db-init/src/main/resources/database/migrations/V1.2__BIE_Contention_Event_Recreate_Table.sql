-- Drop the old table
DROP TABLE IF EXISTS "bie_contention_event";

CREATE TABLE IF NOT EXISTS "bie_contention_event"
(
    -- Unique identifier for each contention event
    id uuid DEFAULT uuid_generate_v4() NOT NULL,

    -- Timestamp when the event was notified
    notified_at TIMESTAMP NOT NULL,

    -- Timestamp when the event actually occurred
    occurred_at TIMESTAMP NOT NULL,

    -- Type of the Contention event
    -- Reference (abd-vro/shared/lib-bie-kafka/src/main/java/gov/va/vro/model/biekafka/ContentionEvent.java)
    event_type VARCHAR(255),

    -- Reference to the claim associated with the contention
    claim_id BIGINT,

    -- Reference to the specific contention
    contention_id BIGINT,

    -- Code representing the type of diagnostic related to the contention
    diagnostic_type_code VARCHAR(255),

    -- Code classifying the contention
    contention_type_code VARCHAR(255),

    -- Timestamp when the record was created in the database
    created_at TIMESTAMP NOT NULL,

    -- Timestamp when the record was last updated in the database
    updated_at TIMESTAMP NOT NULL,

    PRIMARY KEY(id)
);