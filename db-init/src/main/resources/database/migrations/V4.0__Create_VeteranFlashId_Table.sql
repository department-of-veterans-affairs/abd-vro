-- create association table: a veteran can have many flash_ids
CREATE TABLE IF NOT EXISTS veteran_flash_id (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    -- on other schemas, we appear to have been doing something like this to our UUID FKs:
    -- `veteran_id uuid DEFAULT uuid_generate_v4 () NOT NULL,`
    -- this statement says "if no data for this field, then generate a new UUID"
    -- but i don't think this is necessary... for many of our use-cases
    -- here, `veteran_id uuid` should be sufficient, since it should already exist
    veteran_id uuid NOT NULL,
    flash_id INTEGER NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_veteran_id
    FOREIGN KEY (veteran_id)
        REFERENCES veteran(id)
);
