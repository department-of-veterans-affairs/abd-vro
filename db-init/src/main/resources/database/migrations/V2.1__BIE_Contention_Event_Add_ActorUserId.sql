ALTER TABLE "bie_contention_event"
ADD COLUMN IF NOT EXISTS "actor_user_id" VARCHAR(255) DEFAULT NULL;
