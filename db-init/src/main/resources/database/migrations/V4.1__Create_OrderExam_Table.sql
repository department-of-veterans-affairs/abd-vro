CREATE TABLE IF NOT EXISTS order_exam (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    -- request_id VARCHAR, -- otherwise, how do we keep track? Also, what name works best?
    status VARCHAR,
    PRIMARY KEY(id)
);
