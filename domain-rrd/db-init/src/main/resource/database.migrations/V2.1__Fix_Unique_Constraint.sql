ALTER TABLE contention DROP CONSTRAINT contention_diagnostic_code_key;

CREATE UNIQUE INDEX uk_contention_claim_code ON contention (claim_id, diagnostic_code);
