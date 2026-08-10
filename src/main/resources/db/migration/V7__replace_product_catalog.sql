-- ============================================================
-- V7: Replace the product catalogue
--
-- The old catalogue (Grundfos Pumps / Ocean Cooling Tower / Partners) is
-- replaced by the new five-group catalogue. Structure mapping:
--
--   range_id  = top-level group     (drives the /products accordion + navbar)
--   danh_muc  = child category      (NULL when the product sits directly
--                                    under its top-level group)
--   ten       = catalogue leaf      (owns /products/{id})
--
-- Descriptions, images, features and specs are deliberately left NULL —
-- that content has not been supplied yet.
--
-- DataMigrationService.buildSeedData() seeds the same 16 rows on an empty
-- database. Keep the two in sync.
-- ============================================================

DELETE FROM san_pham;

INSERT INTO san_pham (ten, danh_muc, range_id) VALUES
    -- Automatic Control Valve
    ('Anti water hammer valves',      NULL,                  'automatic-control-valve'),
    ('Pilot operated control valves', NULL,                  'automatic-control-valve'),

    -- Control valve HVAC
    ('Motorized Butterfly Valve',                 NULL,      'control-valve-hvac'),
    ('Motorized Globe Valve',                     NULL,      'control-valve-hvac'),
    ('Pressure Independent Control Valve (PICV)', NULL,      'control-valve-hvac'),

    -- Heatpump
    ('Heatpump Air Source', NULL,                  'heatpump'),
    ('CAHP-1.5HP',          'Heatpump all in one', 'heatpump'),
    ('HPI Series',          'Heatpump all in one', 'heatpump'),

    -- Metering and Measuring
    ('DN15-32',            'Energy Meter', 'metering'),
    ('DN15-100',           'Energy Meter', 'metering'),
    ('DN125-800',          'Energy Meter', 'metering'),
    ('DN15-2000',          'Flowmeter',    'metering'),
    ('Single Jet DN15-20', 'Water Meter',  'metering'),
    ('Multi Jet DN15-50',  'Water Meter',  'metering'),
    ('DN50-200',           'Water Meter',  'metering'),

    -- Plate Heat Exchanger
    ('Plate Heat Exchanger', NULL, 'phe');
