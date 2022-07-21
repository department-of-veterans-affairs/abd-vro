CREATE TABLE veteran (
    veteran_uuid VARCHAR(128) NOT NULL PRIMARY KEY,
    icn VARCHAR(128) NOT NULL UNIQUE,
    participant_id VARCHAR NOT NULL,
    edipi VARCHAR NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME
);

CREATE TABLE claim (
    claim_uuid VARCHAR(128) NOT NULL PRIMARY KEY,
    FOREIGN KEY (veteran_uuid) REFERENCES veteran(veteran_uuid),cd
    id VARCHAR(128) NOT NULL UNIQUE,
    id_type VARCHAR NOT NULL UNIQUE,
    incoming_status TIMESTAMP,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME
);

CREATE TABLE contention (
    contention_uuid VARCHAR(128) NOT NULL PRIMARY KEY,
    FOREIGN KEY (claim_uuid) REFERENCES claim(claim_uuid),
    claim_id  VARCHAR(128) NOT NULL UNIQUE,
    diagnostic_code VARCHAR(128) NOT NULL UNIQUE,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME
);

CREATE TABLE assessment_result (
    assessmentResult_uuid VARCHAR(128) NOT NULL PRIMARY KEY,
    FOREIGN KEY (claim_uuid) REFERENCES claim(claim_uuid),
    FOREIGN KEY (contention_uuid) REFERENCES contention(contention_uuid),
    contention_uuid VARCHAR NOT NULL,
    evidence_count VARCHAR(128) NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME
);

CREATE TABLE evidence_summary_document (
    esd_uuid VARCHAR(128) NOT NULL PRIMARY KEY,
    FOREIGN KEY (claim_uuid) REFERENCES claim(claim_uuid),
    FOREIGN KEY (contention_uuid) REFERENCES contention(contention_uuid),
    evidence_count VARCHAR(128) NOT NULL,
    document_name VARCHAR NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME
);




