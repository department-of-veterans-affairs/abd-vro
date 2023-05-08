ALTER TABLE evidence_summary_document DROP COLUMN evidence_count;
ALTER TABLE evidence_summary_document ADD COLUMN evidence_count jsonb;
