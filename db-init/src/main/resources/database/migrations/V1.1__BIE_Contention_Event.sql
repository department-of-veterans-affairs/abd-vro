CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS "bie_contention_event"
(
    id                             uuid DEFAULT uuid_generate_v4() NOT NULL,
    created_at                     TIMESTAMP NOT NULL,
    updated_at                     TIMESTAMP NOT NULL,
    notified_at                    TIMESTAMP NOT NULL,
    occurred_at                    TIMESTAMP NOT NULL,
    event_type                     VARCHAR(255),
    claim_id                       BIGINT,
    contention_id                  BIGINT,
    diagnostic_type_code           VARCHAR(255),
    contention_classification_name VARCHAR(255),
    event_details                  JSONB,
    PRIMARY KEY(id)
);