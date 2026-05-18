-- ============================================================
-- V2: Performance indexes on high-traffic filter columns
-- ============================================================

-- Product list page filters by rangeId on every load
CREATE INDEX IF NOT EXISTS idx_san_pham_range_id ON san_pham(range_id);

-- Search query uses LOWER(danh_muc) LIKE
CREATE INDEX IF NOT EXISTS idx_san_pham_danh_muc ON san_pham(danh_muc);

-- Dashboard and export queries always ORDER BY / filter on thoi_gian
CREATE INDEX IF NOT EXISTS idx_yeu_cau_thoi_gian ON yeu_cau(thoi_gian DESC);
