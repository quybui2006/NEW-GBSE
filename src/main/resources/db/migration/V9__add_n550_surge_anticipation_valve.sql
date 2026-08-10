-- ============================================================
-- V9: Add the N550 surge anticipation valve
--
-- Content taken from the Omeax N550 datasheet (rev. 16/08/25).
--
-- "Anti water hammer valves" stops being a catalogue leaf and becomes a
-- child category, matching how the metering range already nests model
-- leaves ("DN15-32") under a family category ("Energy Meter"). N550 is the
-- first model in that family; further anti-water-hammer models are added
-- as sibling rows sharing the same danh_muc.
--
-- The dimension and nomenclature tables on the datasheet are not modelled
-- as columns — the full PDF is linked from the product page instead.
-- ============================================================

-- The family name is now carried by danh_muc, so the placeholder leaf goes.
DELETE FROM san_pham
 WHERE ten = 'Anti water hammer valves'
   AND range_id = 'automatic-control-valve'
   AND danh_muc IS NULL;

INSERT INTO san_pham (ten, danh_muc, range_id, brand, hinh_anh, link, short_text, mo_ta, applications, features, specs)
VALUES (
    'N550 - Surge Anticipation Valve',
    'Anti water hammer valves',
    'automatic-control-valve',
    'OMEAX',
    '/images/n550.webp',
    '/docs/N550_EN.pdf',
    'Surge anticipation valve that protects pumps, pumping equipment and pipelines from the pressure surges caused by sudden pump shutdown.',
    'Surge Anticipation Valve is indispensable for protecting pumps, pumping equipment and all applicable pipelines from dangerous pressure surges caused by rapid changes of flow velocity within a pipeline. When pumping systems are started and stopped gradually, harmful surges do not occur. However, should a power failure take place, the abrupt stopping of the pump can cause dangerous surges in the system which could result in severe equipment damage. Power failure to a pump will usually result in a down surge in pressure, followed by an up surge in pressure. The surge control valve opens on the initial low pressure wave, diverting the returning high pressure wave from the system. In effect, the valve has anticipated the returning high pressure wave and is open to dissipate the damage causing surge. The valve will then close slowly without generating any further pressure surges.',
    '["Water","Pumping station","Building","Fire protection","Irrigation","Water treatment","Fuel","Sea water","Clear water"]',
    '["Full bore (FB) body: large flow capacity, low head loss and sealing at zero flow rate","Reduced bore (RB) body: cavitation resistance and sealing at zero flow rate, suited to building services and pressure reduction","Body material options: SS304, SS316, SS316L, Duplex, carbon steel, bronze and aluminium","Pilot material options: SS304, SS316, SS316L, brass and bronze","Stainless steel 304 or 316 pilot circuit and valves","Robust design with a stainless steel seat","High flow capacity thanks to a larger seat diameter","Stable operation even when flow is close to zero","High performance nylon reinforced diaphragm","Customisable functions and colour, with operation and maintenance carried out without removing the valve from the pipeline"]',
    '[{"label":"DN (mm)","value":"DN 40 - DN 1600"},{"label":"DN (inch)","value":"1-1/2\" - 64\""},{"label":"Temperature","value":"Water: 0°C - 70°C\nFuel: -40°C - 70°C"},{"label":"Type of connection","value":"Threaded and flange"},{"label":"Application","value":"Pressure, flow and level control; smart control in municipality"},{"label":"Pressure range","value":"ISO PN10 / PN16 / PN25\nANSI Class 125 / 150 / 300\nJIS 10K / 16K\nKS Table D/E, KS4087 PN16"},{"label":"Flange standard","value":"EN 1092-2\nISO 7005-2\nANSI or JIS\nKS2129 or KS4087"},{"label":"Design standard","value":"EN 1074-5"},{"label":"Test standard","value":"ISO 5208 and EN 12266-1"}]'
);
