ALTER TABLE contention
    ADD COLUMN IF NOT EXISTS claim_submission_id UUID DEFAULT uuid_generate_v4 () NOT NULL;

ALTER TABLE contention
    ADD CONSTRAINT fk_claim_submission FOREIGN KEY (claim_submission_id) REFERENCES claim_submission (id);