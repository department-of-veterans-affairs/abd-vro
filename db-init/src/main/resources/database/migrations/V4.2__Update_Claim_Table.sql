ALTER TABLE claim 
ADD COLUMN vbms_id VARCHAR, -- uuid?
ADD COLUMN off_ramp_reason VARCHAR, 
ADD COLUMN presumptive_flag BOOLEAN, 
ADD COLUMN disability_action_type VARCHAR;
