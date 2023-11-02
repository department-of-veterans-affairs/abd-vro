ALTER TABLE "bie_contention_event"
ADD COLUMN "date_added" BIGINT,
ADD COLUMN "date_completed" BIGINT,
ADD COLUMN "date_updated" BIGINT,
ADD COLUMN "actor_station" VARCHAR(255),
ADD COLUMN "automation_indicator" boolean,
ADD COLUMN "benefi_claim_type_code" VARCHAR(255),
ADD COLUMN "contention_status_type_code" VARCHAR(255),
ADD COLUMN "current_lifecycle_status" VARCHAR(255),

ADD COLUMN "details" VARCHAR(255),
ADD COLUMN "event_time" BIGINT,
ADD COLUMN "journal_status_type_code" VARCHAR(255),
ADD COLUMN "veteran_participant_id" VARCHAR(255),
ADD COLUMN "event_details" JSON;
