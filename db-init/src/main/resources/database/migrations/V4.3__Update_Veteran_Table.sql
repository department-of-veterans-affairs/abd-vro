ALTER TABLE veteran 
ADD COLUMN icn_timestamp timestamp NOT NULL;
-- flashIds... list of integers... relationship table??? VeteranFlashEntity?
-- otherwise, there is the integer[] column type... but it is Postgres-specific,
-- other DBMSs might not support this
