CREATE TABLE IF NOT EXISTS exam_order (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    collection_id VARCHAR,
    status VARCHAR,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    PRIMARY KEY(id)
);