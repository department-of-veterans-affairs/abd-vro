ALTER TABLE claim DROP COLUMN claim_submission_id;
ALTER TABLE claim DROP COLUMN id_type;
ALTER TABLE claim DROP COLUMN incoming_status;
ALTER TABLE claim DROP COLUMN submission_source;
ALTER TABLE claim DROP COLUMN submission_date;
ALTER TABLE claim DROP COLUMN off_ramp_reason;
ALTER TABLE claim DROP COLUMN in_scope;
ALTER TABLE claim DROP COLUMN collection_id;
ALTER TABLE claim ADD COLUMN rfd_flag BOOLEAN;
