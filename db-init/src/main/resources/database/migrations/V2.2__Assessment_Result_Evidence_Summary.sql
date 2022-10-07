CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS evidence_count_summary (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    assessment_result_id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    total_bp_readings INTEGER NOT NULL,
    recent_bp_readings INTEGER NOT NULL,
    medications_count INTEGER NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    PRIMARY KEY(id),
        CONSTRAINT fk_assessment_result_id
            FOREIGN KEY (assessment_result_id)
                REFERENCES assessment_result(id)
);




