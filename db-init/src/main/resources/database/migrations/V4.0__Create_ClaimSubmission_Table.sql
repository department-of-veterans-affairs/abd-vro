CREATE TABLE IF NOT EXISTS claim_submission (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    reference_id VARCHAR,
    type_id VARCHAR,
    event_id VARCHAR NOT NULL,
    route_id VARCHAR,
    payload_type VARCHAR NOT NULL,
    message VARCHAR,
    throwable TEXT,
    -- details TEXT, -- no longer needed?
    event_time timestamp NOT NULL,
    request_type ENUM ('CLAIM_SUBMISSION', 'AUTOMATED_CLAIM', 'OTHER') NOT NULL,
    status ENUM ('PROCESSED', 'REJECTED', 'ERROR') NOT NULL,
    rfd_flag BOOLEAN,
    PRIMARY KEY(id)
);

CREATE INDEX claim_submission_id_idx on claim_submission(event_id);
