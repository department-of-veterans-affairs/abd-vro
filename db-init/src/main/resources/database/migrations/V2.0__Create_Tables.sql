CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS veteran (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    icn VARCHAR NOT NULL UNIQUE,
    participant_id VARCHAR,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS claim (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    claim_submission_id VARCHAR NOT NULL UNIQUE,
    veteran_id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    id_type VARCHAR NOT NULL,
    incoming_status VARCHAR,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_veteran_id
        FOREIGN KEY (veteran_id)
            REFERENCES veteran(id)
);

CREATE TABLE IF NOT EXISTS contention(
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    claim_id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    diagnostic_code VARCHAR NOT NULL UNIQUE,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT null,
    PRIMARY KEY(id),
    CONSTRAINT fk_claim_id
        FOREIGN KEY (claim_id)
            REFERENCES claim(id)
);

CREATE TABLE IF NOT EXISTS assessment_result (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    contention_id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    evidence_count INTEGER NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_contention_id
        FOREIGN KEY (contention_id)
            REFERENCES contention(id)
);

CREATE TABLE IF NOT EXISTS evidence_summary_document (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    contention_id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    evidence_count INTEGER NOT NULL,
    document_name VARCHAR NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_contention_id
        FOREIGN KEY (contention_id)
            REFERENCES contention(id)
);
