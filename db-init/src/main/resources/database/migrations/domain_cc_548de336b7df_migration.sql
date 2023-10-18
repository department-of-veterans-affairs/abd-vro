BEGIN;

CREATE TABLE alembic_version (
    version_num VARCHAR(32) NOT NULL, 
    CONSTRAINT alembic_version_pkc PRIMARY KEY (version_num)
);

-- Running upgrade  -> 548de336b7df

CREATE TABLE mason_model_not_real_model_delete_me (
    id SERIAL NOT NULL, 
    vets_api_claim_id INTEGER, 
    vets_api_form526_submission_id INTEGER, 
    PRIMARY KEY (id)
);

CREATE INDEX ix_mason_model_not_real_model_delete_me_id ON mason_model_not_real_model_delete_me (id);

CREATE UNIQUE INDEX ix_mason_model_not_real_model_delete_me_vets_api_claim_id ON mason_model_not_real_model_delete_me (vets_api_claim_id);

DROP INDEX ix_contentions_id;

DROP TABLE contentions;

DROP INDEX ix_claim_id;

DROP INDEX ix_claim_vets_api_claim_id;

DROP TABLE claim;

INSERT INTO alembic_version (version_num) VALUES ('548de336b7df') RETURNING alembic_version.version_num;

COMMIT;

