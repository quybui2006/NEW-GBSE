-- ============================================================
-- V12: Regroup the Automatic Control Valve and Heatpump ranges
--
-- Automatic Control Valve: the pilot operated valve sat directly under the
-- range while the N550 sat under an "Anti water hammer valves" child
-- category, so the range rendered as two separate blocks. Clearing the
-- child category puts both products in a single grid under the range
-- heading.
--
-- Heatpump: "Heatpump all in one" already names its own block, but the air
-- source heat pump had no child category and so rendered with no heading
-- above it. Giving it one makes both blocks in the range consistent.
-- ============================================================

UPDATE san_pham
   SET danh_muc = NULL
 WHERE range_id = 'automatic-control-valve'
   AND danh_muc = 'Anti water hammer valves';

UPDATE san_pham
   SET danh_muc = 'Heatpump Air Source'
 WHERE range_id = 'heatpump'
   AND ten = 'Heatpump Air Source'
   AND danh_muc IS NULL;
