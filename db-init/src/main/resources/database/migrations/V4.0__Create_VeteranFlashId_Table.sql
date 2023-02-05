-- create association table: a collection_id (claim) can have many flash_ids
CREATE TABLE IF NOT EXISTS veteran_flash_id (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    collection_id VARCHAR NOT NULL,
    flash_id INTEGER NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_collection_id
        FOREIGN KEY (collection_id)
            REFERENCES claim(collection_id)
);
