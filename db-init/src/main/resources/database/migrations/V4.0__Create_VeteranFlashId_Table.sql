-- create association table: a collection_id (claim) can have many flash_ids
CREATE TABLE IF NOT EXISTS veteran_flash_id (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    collecton_id VARCHAR NOT NULL,
    flash_id INTEGER NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_collecton_id
        FOREIGN KEY (collecton_id)
            REFERENCES claim(collecton_id)
);
