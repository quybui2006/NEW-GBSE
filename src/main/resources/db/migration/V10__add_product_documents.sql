-- ============================================================
-- V10: Per-product datasheet list
--
-- Most products ship with several PDFs — a main datasheet plus accessory
-- module sheets and selection guides (the DN15-50 multi-jet water meter
-- alone has four). The single `link` column cannot express that, so
-- documents get their own JSON column, following the same
-- JSON-array-as-text convention already used by applications/features/specs.
--
-- Shape: [{"label": "...", "url": "/docs/....pdf"}]
--
-- `link` reverts to unused. It is left in place rather than dropped —
-- removing it is a separate decision from adding this column.
-- ============================================================

ALTER TABLE san_pham ADD COLUMN IF NOT EXISTS documents TEXT;
