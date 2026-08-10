-- ============================================================
-- V14: Rename the N550 and drop the Automatic Control Valve application tags
--
-- The surge anticipation valve is listed under its family name now that the
-- range no longer carries an "Anti water hammer valves" child category
-- (see V12).
--
-- The application tags on both Automatic Control Valve products are cleared
-- until the final list is supplied; getApplicationsList() returns an empty
-- list for a null column, so the pills stop rendering on the detail page.
-- ============================================================

UPDATE san_pham
   SET ten = 'Anti water hammer valves'
 WHERE ten = 'N550 - Surge Anticipation Valve'
   AND range_id = 'automatic-control-valve';

UPDATE san_pham
   SET applications = NULL
 WHERE range_id = 'automatic-control-valve';
