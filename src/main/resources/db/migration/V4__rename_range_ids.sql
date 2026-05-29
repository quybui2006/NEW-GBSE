-- Rename internal range IDs: omeax → grundfos, gbse → oceancooling
UPDATE san_pham SET range_id = 'grundfos'    WHERE range_id = 'omeax';
UPDATE san_pham SET range_id = 'oceancooling' WHERE range_id = 'gbse';

-- Update legacy brand field values to match
UPDATE san_pham SET brand = 'Grundfos'             WHERE brand = 'OMEAX';
UPDATE san_pham SET brand = 'Ocean Cooling Tower'  WHERE brand = 'GBSE';
