ALTER TABLE evidence_summary_document ADD COLUMN folder_id UUID;

ALTER TABLE evidence_summary_document DROP COLUMN location;
