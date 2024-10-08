CREATE TABLE IF NOT EXISTS "tracked_claims" (
    id uuid NOT NULL,
	claim_id INT NOT NULL,
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	established_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	feature_name VARCHAR NOT NULL,
	feature_enabled BOOLEAN NOT NULL,
	PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS tracked_claims_id_index ON tracked_claims (id);
CREATE INDEX IF NOT EXISTS tracked_claims_claim_id_index ON tracked_claims (claim_id);
CREATE INDEX IF NOT EXISTS tracked_claims_feature_name_index ON tracked_claims (feature_name);
