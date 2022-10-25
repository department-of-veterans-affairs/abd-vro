CREATE TABLE IF NOT EXISTS audit_event (
    id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    event_id VARCHAR NOT NULL UNIQUE,
    route_id VARCHAR,
    payload_type VARCHAR,
    exception VARCHAR,
    message VARCHAR,
    event_time timestamp NOT NULL,
    PRIMARY KEY(id)
);

CREATE INDEX audit_event_id_idx on audit_event(event_id);
