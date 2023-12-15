CREATE TABLE IF NOT EXISTS "merge_jobs" (
    job_id uuid DEFAULT uuid_generate_v4 () NOT NULL,
    pending_claim_id INT NOT NULL,
    ep400_claim_id INT NOT NULL,
    state text NOT NULL,
    error_state text,
    messages jsonb[],
    PRIMARY KEY(job_id)
);
CREATE INDEX IF NOT EXISTS merge_jobs_pending_claim_id_idx ON merge_jobs (pending_claim_id);
CREATE INDEX IF NOT EXISTS merge_jobs_ep400_claim_id_idx ON merge_jobs (ep400_claim_id);
CREATE INDEX IF NOT EXISTS merge_jobs_state_idx ON merge_jobs (state);