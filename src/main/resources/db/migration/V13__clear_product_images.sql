-- ============================================================
-- V13: Clear every product image
--
-- Only two of the sixteen products had artwork (the pilot operated control
-- valve and the N550), so the catalogue rendered a mix of photos and
-- placeholders. Until a full set of images is supplied, every product falls
-- back to the shared placeholder that Product.getImage() returns for a null
-- hinh_anh.
--
-- The image files themselves are left in static/images/ so the rows can be
-- pointed back at them once the rest of the artwork arrives.
-- ============================================================

UPDATE san_pham
   SET hinh_anh = NULL
 WHERE hinh_anh IS NOT NULL;
