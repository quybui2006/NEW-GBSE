-- ============================================================
-- V6: Add contact-tracking columns to yeu_cau
-- These fields (ContactRequest.contacted / adminNote) were added to the
-- entity after the V1 baseline snapshot was written, so V1 is missing them.
--
-- Fresh databases (local Docker Postgres): V1 builds yeu_cau without these
--   columns, so this migration adds them.
-- Existing production (Supabase): Hibernate's old ddl-auto=update already
--   created them, so ADD COLUMN IF NOT EXISTS is a safe no-op.
-- ============================================================

ALTER TABLE yeu_cau ADD COLUMN IF NOT EXISTS contacted  BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE yeu_cau ADD COLUMN IF NOT EXISTS admin_note VARCHAR(255);
