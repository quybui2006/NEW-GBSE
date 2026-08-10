-- ============================================================
-- V8: Widen the product description column
--
-- mo_ta was VARCHAR(500), which is too small for the description
-- paragraphs supplied on manufacturer datasheets (the N550 sheet alone
-- runs to ~890 characters). TEXT removes the ceiling for every future
-- product without changing how the column is read or written.
-- ============================================================

ALTER TABLE san_pham ALTER COLUMN mo_ta TYPE TEXT;
