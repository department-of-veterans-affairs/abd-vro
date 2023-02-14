ALTER TABLE exam_order
    ADD COLUMN IF NOT EXISTS claim_submission_id UUID;

ALTER TABLE exam_order
    ADD CONSTRAINT fk_claim_submission_id FOREIGN KEY (claim_submission_id) REFERENCES claim_submission (id);