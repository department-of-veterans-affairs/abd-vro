CREATE TABLE IF NOT EXISTS order_exam (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    collection_id VARCHAR,
    status VARCHAR,
    PRIMARY KEY(id),
    CONSTRAINT fk_collection_id
        FOREIGN KEY (collection_id)
            REFERENCES claim(collection_id)
);
