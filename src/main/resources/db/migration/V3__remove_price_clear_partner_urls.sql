-- ============================================================
-- V3: Remove price column; clear URLs from non-partner products
-- Business rule: only "partners" range may store external URLs.
-- ============================================================

-- Clear link URLs for OMEAX and GBSE products before dropping the price column.
-- This enforces the B2B business rule at the data layer.
UPDATE san_pham SET link = NULL WHERE range_id != 'partners';

-- Drop the price column entirely — this is a showcase site, not ecommerce.
ALTER TABLE san_pham DROP COLUMN IF EXISTS gia;
