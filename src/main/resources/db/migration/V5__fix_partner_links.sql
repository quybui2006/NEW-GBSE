-- Clear the stale omeax.com link from Industrial Water System.
-- This product is in the "partners" range so V4 did not touch its link column.
UPDATE san_pham SET link = NULL WHERE ten = 'Industrial Water System' AND range_id = 'partners';
