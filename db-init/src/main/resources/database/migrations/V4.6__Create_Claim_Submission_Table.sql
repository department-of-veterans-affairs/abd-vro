CREATE TABLE IF NOT EXISTS claim_submission (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    reference_id VARCHAR,
    id_type VARCHAR,
    submission_source VARCHAR,
    submission_date timestamp,
    incoming_status VARCHAR,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    PRIMARY KEY(id)
);
