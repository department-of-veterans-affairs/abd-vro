ALTER TABLE claim 
ADD COLUMN vbms_id VARCHAR,
ADD COLUMN off_ramp_reason VARCHAR, 
ADD COLUMN presumptive_flag BOOLEAN, 
ADD COLUMN disability_action_type VARCHAR,
ADD COLUMN in_scope BOOLEAN,
ADD COLUMN submission_source VARCHAR,
ADD COLUMN submission_date timestamp;
