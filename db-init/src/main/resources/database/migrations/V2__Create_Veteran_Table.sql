CREATE TABLE IF NOT EXISTS veteran (
    veteran_uuid VARCHAR(128) NOT NULL,
    PRIMARY KEY(veteran_uuid),
    icn VARCHAR(128) NOT NULL UNIQUE,
    participant_id VARCHAR NOT NULL,
    edipi VARCHAR NOT NULL,
    createdAt timestamp NOT NULL,
    updatedAt timestamp
);

CREATE TABLE IF NOT EXISTS claim (
    claim_uuid VARCHAR(128) NOT NULL,
    PRIMARY KEY(claim_uuid),
    veteran_uuid VARCHAR(128) NOT NULL,
    CONSTRAINT fk_veteran_uuid
        FOREIGN KEY (veteran_uuid)
            REFERENCES veteran(veteran_uuid),
    id VARCHAR(128) NOT NULL UNIQUE,
    id_type VARCHAR NOT NULL UNIQUE,
    incoming_status TIMESTAMP,
    createdAt timestamp NOT NULL,
    updatedAt timestamp
);

CREATE TABLE IF NOT EXISTS contention (
    contention_uuid VARCHAR(128) NOT NULL,
    claim_uuid VARCHAR(128) NOT NULL,
    PRIMARY KEY(contention_uuid),
    CONSTRAINT fk_claim_uuid
        FOREIGN KEY (claim_uuid)
            REFERENCES claim(claim_uuid),
    claim_id  VARCHAR(128) NOT NULL UNIQUE,
    diagnostic_code VARCHAR(128) NOT NULL UNIQUE,
    createdAt timestamp NOT NULL,
    updatedAt timestamp
);

CREATE TABLE IF NOT EXISTS assessment_result (
    assessmentResult_uuid VARCHAR(128) NOT NULL,
    claim_uuid VARCHAR(128) NOT NULL,
    contention_uuid VARCHAR NOT NULL,
    PRIMARY KEY(assessmentResult_uuid),
    CONSTRAINT fk_claim_uuid
        FOREIGN KEY (claim_uuid)
            REFERENCES claim(claim_uuid),
    CONSTRAINT fk_contention_uuid
        FOREIGN KEY (contention_uuid)
            REFERENCES contention(contention_uuid),
    evidence_count VARCHAR(128) NOT NULL,
    createdAt timestamp NOT NULL,
    updatedAt timestamp
);

CREATE TABLE IF NOT EXISTS evidence_summary_document (
    esd_uuid VARCHAR(128) NOT NULL,
    contention_uuid VARCHAR NOT NULL,
    claim_uuid VARCHAR(128) NOT NULL,
    PRIMARY KEY(esd_uuid),
    CONSTRAINT fk_claim_uuid
        FOREIGN KEY (claim_uuid)
            REFERENCES claim(claim_uuid),
    CONSTRAINT fk_contention_uuid
        FOREIGN KEY (contention_uuid)
            REFERENCES contention(contention_uuid),
    evidence_count VARCHAR(128) NOT NULL,
    document_name VARCHAR NOT NULL,
    createdAt timestamp NOT NULL,
    updatedAt timestamp
);




