ALTER TABLE claim_submission
    ADD COLUMN IF NOT EXISTS in_scope BOOLEAN;
