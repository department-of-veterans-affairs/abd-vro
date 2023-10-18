/*
Programmatically generated using
alembic upgrade 20aeb06ba5b7 --sql
See domain-cc/cc-app/alembic/versions/
*/

BEGIN;

CREATE TABLE alembic_version (
    version_num VARCHAR(32) NOT NULL,
    CONSTRAINT alembic_version_pkc PRIMARY KEY (version_num)
);

-- Running upgrade  -> 20aeb06ba5b7

CREATE TABLE claim (
    id SERIAL NOT NULL,
    vets_api_claim_id INTEGER,
    vets_api_form526_submission_id INTEGER,
    PRIMARY KEY (id)
);

CREATE INDEX ix_claim_id ON claim (id);

CREATE UNIQUE INDEX ix_claim_vets_api_claim_id ON claim (vets_api_claim_id);

CREATE TABLE contentions (
    id SERIAL NOT NULL,
    diagnostic_code INTEGER,
    classification_code INTEGER,
    claim_id INTEGER,
    PRIMARY KEY (id),
    FOREIGN KEY(claim_id) REFERENCES claim (id)
);

CREATE INDEX ix_contentions_id ON contentions (id);

INSERT INTO alembic_version (version_num) VALUES ('20aeb06ba5b7') RETURNING alembic_version.version_num;

COMMIT;
