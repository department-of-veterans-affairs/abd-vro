-- We do not need to preserve this data since we should not have any production audit details, yet
ALTER TABLE audit_event DROP COLUMN details;
-- As such, we can just recreate the column as JSONB
ALTER TABLE audit_event ADD COLUMN details JSONB;
