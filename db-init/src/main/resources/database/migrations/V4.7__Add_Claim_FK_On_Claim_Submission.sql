ALTER TABLE claim_submission
    ADD COLUMN IF NOT EXISTS claim_id UUID DEFAULT uuid_generate_v4 () NOT NULL;

ALTER TABLE claim_submission
    ADD COLUMN IF NOT EXISTS off_ramp_reason VARCHAR;

ALTER TABLE claim_submission
    ADD CONSTRAINT fk_claim_id FOREIGN KEY (claim_id) REFERENCES claim (id);