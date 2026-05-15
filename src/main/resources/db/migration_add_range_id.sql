-- ============================================================
-- Run this on your Supabase PostgreSQL database BEFORE
-- deploying the new application version.
--
-- This adds the range_id column that the SanPham entity now
-- expects. The column is nullable so existing rows are safe.
-- ============================================================

ALTER TABLE san_pham ADD COLUMN IF NOT EXISTS range_id VARCHAR(50);

-- After the app has seeded the data (first startup with empty table),
-- you can optionally add an index for faster range-group queries:
-- CREATE INDEX IF NOT EXISTS idx_san_pham_range_id ON san_pham(range_id);
