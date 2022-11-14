ALTER TABLE assessment_result DROP COLUMN evidence_count;

ALTER TABLE assessment_result ADD COLUMN evidence_count_summary jsonb;
