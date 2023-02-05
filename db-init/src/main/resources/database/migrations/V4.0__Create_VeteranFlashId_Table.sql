-- create association table: a veteran can have many flash_ids
CREATE TABLE IF NOT EXISTS veteran_flash_id (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    veteran_icn VARCHAR NOT NULL,
    flash_id INTEGER NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_veteran_icn
    FOREIGN KEY (veteran_icn)
        REFERENCES veteran(icn)
);
