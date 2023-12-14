DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM information_schema.columns WHERE table_name = 'bie_contention_event' AND column_name = 'date_added') THEN
        ALTER TABLE "bie_contention_event" ADD COLUMN "date_added" TIMESTAMP;
    END IF;

    IF NOT EXISTS (SELECT FROM information_schema.columns WHERE table_name = 'bie_contention_event' AND column_name = 'date_completed') THEN
        ALTER TABLE "bie_contention_event" ADD COLUMN "date_completed" TIMESTAMP;
    END IF;

    IF NOT EXISTS (SELECT FROM information_schema.columns WHERE table_name = 'bie_contention_event' AND column_name = 'date_updated') THEN
        ALTER TABLE "bie_contention_event" ADD COLUMN "date_updated" TIMESTAMP;
    END IF;

    IF NOT EXISTS (SELECT FROM information_schema.columns WHERE table_name = 'bie_contention_event' AND column_name = 'actor_station') THEN
        ALTER TABLE "bie_contention_event" ADD COLUMN "actor_station" VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT FROM information_schema.columns WHERE table_name = 'bie_contention_event' AND column_name = 'automation_indicator') THEN
        ALTER TABLE "bie_contention_event" ADD COLUMN "automation_indicator" boolean;
    END IF;

    IF NOT EXISTS (SELECT FROM information_schema.columns WHERE table_name = 'bie_contention_event' AND column_name = 'benefit_claim_type_code') THEN
        ALTER TABLE "bie_contention_event" ADD COLUMN "benefit_claim_type_code" VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT FROM information_schema.columns WHERE table_name = 'bie_contention_event' AND column_name = 'contention_status_type_code') THEN
        ALTER TABLE "bie_contention_event" ADD COLUMN "contention_status_type_code" VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT FROM information_schema.columns WHERE table_name = 'bie_contention_event' AND column_name = 'current_lifecycle_status') THEN
        ALTER TABLE "bie_contention_event" ADD COLUMN "current_lifecycle_status" VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT FROM information_schema.columns WHERE table_name = 'bie_contention_event' AND column_name = 'clmnt_txt') THEN
        ALTER TABLE "bie_contention_event" ADD COLUMN "clmnt_txt" VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT FROM information_schema.columns WHERE table_name = 'bie_contention_event' AND column_name = 'details') THEN
        ALTER TABLE "bie_contention_event" ADD COLUMN "details" VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT FROM information_schema.columns WHERE table_name = 'bie_contention_event' AND column_name = 'journal_status_type_code') THEN
        ALTER TABLE "bie_contention_event" ADD COLUMN "journal_status_type_code" VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT FROM information_schema.columns WHERE table_name = 'bie_contention_event' AND column_name = 'veteran_participant_id') THEN
        ALTER TABLE "bie_contention_event" ADD COLUMN "veteran_participant_id" VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT FROM information_schema.columns WHERE table_name = 'bie_contention_event' AND column_name = 'event_time') THEN
        ALTER TABLE "bie_contention_event" ADD COLUMN "event_time" TIMESTAMP;
    END IF;
END $$;
