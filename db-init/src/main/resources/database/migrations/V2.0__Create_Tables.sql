CREATE TABLE IF NOT EXISTS veteran (
    veteran_id VARCHAR NOT NULL,
    icn VARCHAR NOT NULL UNIQUE,
    participant_id VARCHAR NOT NULL,
    createdAt timestamp NOT NULL,
    updatedAt timestamp NOT NULL,
    PRIMARY KEY(veteran_id)
);

CREATE TABLE IF NOT EXISTS claim (
    claim_id VARCHAR NOT NULL,
    veteran_id VARCHAR NOT NULL,
    id_type VARCHAR NOT NULL UNIQUE,
    incoming_status VARCHAR,
    createdAt timestamp NOT NULL,
    updatedAt timestamp NOT NULL,
    CONSTRAINT fk_veteran_id
        FOREIGN KEY (veteran_id)
            REFERENCES veteran(veteran_id),
    PRIMARY KEY(claim_id)
);

CREATE TABLE IF NOT EXISTS contention(
    contention_id VARCHAR NOT NULL,
    claim_id VARCHAR NOT NULL,
    diagnostic_code VARCHAR NOT NULL UNIQUE,
    createdAt timestamp NOT NULL,
    updatedAt timestamp NOT null,
    PRIMARY KEY(contention_id),
    CONSTRAINT fk_claim_id
        FOREIGN KEY (claim_id)
            REFERENCES claim(claim_id)
);

CREATE TABLE IF NOT EXISTS assessment_result (
    assessmentResult_id VARCHAR NOT NULL,
    contention_id VARCHAR NOT NULL,
    evidence_count INTEGER NOT NULL,
    createdAt timestamp NOT NULL,
    updatedAt timestamp NOT NULL,
    PRIMARY KEY(assessmentResult_id),
    CONSTRAINT fk_contention_id
        FOREIGN KEY (contention_id)
            REFERENCES contention(contention_id)
);

CREATE TABLE IF NOT EXISTS evidence_summary_document (
    esd_id VARCHAR NOT NULL,
    contention_id VARCHAR NOT NULL,
    evidence_count INTEGER NOT NULL,
    document_name VARCHAR NOT NULL,
    createdAt timestamp NOT NULL,
    updatedAt timestamp NOT NULL,
    PRIMARY KEY(esd_id),
    CONSTRAINT fk_contention_id
        FOREIGN KEY (contention_id)
            REFERENCES contention(contention_id)
);