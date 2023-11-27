BEGIN;

CREATE TABLE alembic_version (
    version_num VARCHAR(32) NOT NULL,
    CONSTRAINT alembic_version_pkc PRIMARY KEY (version_num)
);

-- Running upgrade  -> 78da658f9506

CREATE TABLE mason_model_not_real_model_delete_me (
    id SERIAL NOT NULL,
    vets_api_claim_id INTEGER,
    vets_api_form526_submission_id INTEGER,
    vbms_submitted_claim_id INTEGER,
    email_address VARCHAR,
    PRIMARY KEY (id)
);

CREATE INDEX ix_mason_model_not_real_model_delete_me_id ON mason_model_not_real_model_delete_me (id);

CREATE UNIQUE INDEX ix_mason_model_not_real_model_delete_me_vets_api_claim_id ON mason_model_not_real_model_delete_me (vets_api_claim_id);

INSERT INTO alembic_version (version_num) VALUES ('78da658f9506') RETURNING alembic_version.version_num;

COMMIT;
