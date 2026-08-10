-- ============================================================
-- V11: Catalogue content for all 16 products
--
-- Sourced from the per-product spec sheets (.xlsx) and datasheets (.pdf)
-- supplied in the GBSE product folder; the folder tree maps 1:1 onto the
-- rows created by V7, so every statement here is an UPDATE.
--
-- Model names, brands and variants are taken from the PDF datasheets, which
-- carry detail the .xlsx summaries omit: the HVAC valves are Danfoss (VFH2,
-- VRB2/3, VF2/3, AB-QM 4.0), DN15-32 covers SonoSelect 10 *and* SonoSafe 10,
-- the flowmeter has a separate-electronics MAG-S variant, and the heat pump
-- capacities are per model (19.8 / 42 / 84 kW).
--
-- Remote-reading modules (RFM-*, IWM-*) are accessories for their parent
-- meter, not standalone products; their document labels say so.
--
-- Mapping used:
--   "Label: value" lines  -> specs      (JSON [{label,value}])
--   benefit bullets       -> features   (JSON [string])
--   prose sentences       -> mo_ta
--   sectors served        -> applications
--   every PDF in a folder -> documents  (JSON [{label,url}], see V10)
--
-- `link` is cleared: datasheets now live in `documents`.
--
-- Values are transcribed from the source sheets. Obvious typos are
-- corrected ("Electromagnectic" -> "Electromagnetic", "Temperature senso"
-- -> "Temperature sensor", "DN65:250" -> "DN65-250") and European decimal
-- separators are rendered in the site's English number format.
--
-- Images: only the pilot operated control valve and the N550 have artwork.
-- The other 14 keep hinh_anh NULL and render the shared placeholder.
-- ============================================================

-- N550 - Surge Anticipation Valve
UPDATE san_pham
   SET
       documents    = '[{"label": "N550 surge anticipation valve - datasheet (EN)", "url": "/docs/n550-en.pdf"}]',
       link         = NULL
     WHERE ten = 'N550 - Surge Anticipation Valve'
       AND range_id = 'automatic-control-valve'
       AND danh_muc = 'Anti water hammer valves';

-- Pilot operated control valves
UPDATE san_pham
   SET
       brand        = 'Neptune',
       hinh_anh     = '/images/acv_site.png',
       short_text   = 'Neptune pilot operated automatic control valves for pressure, flow and level control, DN40 to DN1600.',
       applications = '["Water", "Pumping station", "Building", "Fire protection", "Irrigation", "Water treatment", "Fuel", "Sea water", "Clear water"]',
       specs        = '[{"label": "Nominal diameter", "value": "DN40 - DN1600"}, {"label": "Media temperature", "value": "Water: 0°C to 70°C\nFuel: -40°C to 70°C"}, {"label": "Pressure range", "value": "PN10 / PN16 / PN25\nPN40, PN63 and UL/FM listed as coming soon"}, {"label": "Connection", "value": "PN10 / PN16 / PN25"}, {"label": "Application", "value": "Pressure, flow and level control; smart control in municipality"}, {"label": "Flange standard", "value": "EN 1092-2\nISO 7005-2\nANSI or JIS\nKS2129 or KS4087"}, {"label": "Design standard", "value": "EN 1074-5"}, {"label": "Test standard", "value": "ISO 5208 and EN 12266-1"}]',
       documents    = '[{"label": "Neptune automatic control valves - brochure (EN)", "url": "/docs/brochure-neptune-en.pdf"}]',
       link         = NULL
     WHERE ten = 'Pilot operated control valves'
       AND range_id = 'automatic-control-valve'
       AND danh_muc IS NULL;

-- Motorized Butterfly Valve
UPDATE san_pham
   SET
       brand        = 'Danfoss',
       short_text   = 'Danfoss VFH2 wafer type motorized butterfly valve, DN32 to DN600, with on/off or modulating actuator.',
       specs        = '[{"label": "Model", "value": "VFH2"}, {"label": "Valve body", "value": "Wafer type"}, {"label": "Nominal diameter", "value": "DN32 - DN600"}, {"label": "Max. working pressure", "value": "16 bar"}, {"label": "Body material", "value": "GGG40 nodular cast iron"}, {"label": "Disc material", "value": "Wafer type DN32: 304SS\nWafer type DN40-600: GGG40 + Nylon\nLug / flange: 304SS"}, {"label": "Actuator", "value": "On/Off or modulating"}, {"label": "Power supply", "value": "230 VAC"}, {"label": "Protection class", "value": "IP67"}]',
       documents    = '[{"label": "Butterfly valve VFH2 DN32-600 - datasheet", "url": "/docs/butterfly-valve-vfh2-dn32-600.pdf"}]',
       link         = NULL
     WHERE ten = 'Motorized Butterfly Valve'
       AND range_id = 'control-valve-hvac'
       AND danh_muc IS NULL;

-- Motorized Globe Valve
UPDATE san_pham
   SET
       brand        = 'Danfoss',
       short_text   = 'Danfoss two-way and three-way motorized globe valves, DN15 to DN150, with modulating actuator.',
       specs        = '[{"label": "Configuration", "value": "2-way and 3-way"}, {"label": "Model", "value": "DN15-50: VRB2 (2-way) / VRB3 (3-way), threaded\nDN65-150: VF2 (2-way) / VF3 (3-way), flanged"}, {"label": "Body material", "value": "DN15-50: Red bronze CuSn5Zn5Pb5 (Rg5)\nDN65-150: EN-GJL-250 (GG-25) / EN-GJS-400-18-LT"}, {"label": "Working pressure", "value": "PN16"}, {"label": "Actuator", "value": "Modulating control, 24 V\nCompatible with AMV(E) 335 / AMV(E) 435"}, {"label": "Input signal", "value": "0(2)-10 V / 0(4)-20 mA"}]',
       documents    = '[{"label": "VRB2 / VRB3 (DN15-50) - datasheet", "url": "/docs/vrb2-vrb3.pdf"}, {"label": "VF2 / VF3 (DN65-150) - datasheet", "url": "/docs/vf2-plus-vf3.pdf"}]',
       link         = NULL
     WHERE ten = 'Motorized Globe Valve'
       AND range_id = 'control-valve-hvac'
       AND danh_muc IS NULL;

-- Pressure Independent Control Valve (PICV)
UPDATE san_pham
   SET
       brand        = 'Danfoss',
       short_text   = 'Danfoss AB-QM 4.0 pressure independent control valve, DN15 to DN250, combining balancing and control in a single valve.',
       specs        = '[{"label": "Model", "value": "AB-QM 4.0 (DN15 - DN250)\nAB-QM 4.0 Flexo 80 (DN15 - DN20, PN25, pre-assembled,\nleft-hand and right-hand versions)"}, {"label": "Size", "value": "DN15 - DN250"}, {"label": "Working pressure", "value": "DN15-32: 25 bar\nDN40-250: 16 bar"}, {"label": "Differential pressure range", "value": "16 - 600 kPa"}, {"label": "Connection", "value": "DN15-50: Threaded (ISO 228-1)\nDN65-250: Flanged (EN 1092-1, -2)"}, {"label": "Flow rate range", "value": "DN15: 65 - 650 l/h\nDN20: 110 - 1,100 l/h\nDN25: 220 - 2,200 l/h\nDN32: 360 - 3,600 l/h\nDN40: 3,000 - 7,500 l/h\nDN50: 5,000 - 12,500 l/h\nDN65: 8,000 - 20,000 l/h\nDN80: 11,200 - 28,000 l/h\nDN100: 15,200 - 38,000 l/h\nDN125: 36,000 - 99,000 l/h\nDN150: 58,000 - 159,500 l/h\nDN200: 80,000 - 220,000 l/h\nDN250: 120,000 - 330,000 l/h"}, {"label": "Actuator control", "value": "On/Off or modulating"}, {"label": "Actuator type", "value": "Thermal / gear / digital"}, {"label": "Power supply", "value": "230 V / 24 V"}]',
       documents    = '[{"label": "AB-QM 4.0 - datasheet", "url": "/docs/abqm-4-0.pdf"}, {"label": "AB-QM 4.0 Flexo 80 - datasheet", "url": "/docs/ab-qm-4-0-flexo-80.pdf"}, {"label": "Actuator selection for PICV - selection guide", "url": "/docs/actuator-selection-for-picv.pdf"}]',
       link         = NULL
     WHERE ten = 'Pressure Independent Control Valve (PICV)'
       AND range_id = 'control-valve-hvac'
       AND danh_muc IS NULL;

-- Heatpump Air Source
UPDATE san_pham
   SET
       short_text   = 'Air-to-water heat pumps from 19 to 84 kW with a COP above 4.',
       specs        = '[{"label": "Type", "value": "Air source heat pump (air to water)"}, {"label": "Models", "value": "CAHP-MC-19: 19.8 kW, COP 4.0, DN32 water connection\nCAHP-HC-42E: 42 kW, COP 4, DN40 water connection\nCAHP-HC-84E: 84 kW, COP 4.39, DN50 water connection"}, {"label": "Voltage / phase", "value": "380 V / 3N~ / 50 Hz (±10%)"}, {"label": "Max. output water temperature", "value": "55°C"}, {"label": "Refrigerant", "value": "R410A"}]',
       documents    = '[{"label": "CAHP-MC-19 - specification", "url": "/docs/cahp-mc-19-specification.pdf"}, {"label": "CAHP-HC-42E - datasheet", "url": "/docs/cahp-hc-42e.pdf"}, {"label": "CAHP-HC-84E - datasheet", "url": "/docs/cahp-hc-84e.pdf"}]',
       link         = NULL
     WHERE ten = 'Heatpump Air Source'
       AND range_id = 'heatpump'
       AND danh_muc IS NULL;

-- CAHP-1.5HP
UPDATE san_pham
   SET
       short_text   = 'All-in-one heat pump water heater with 300 or 450 litre storage and water temperature up to 65°C.',
       features     = '["Unique heat exchanger design, heat pump water temperature to 65°C", "Energy saving, fast heating, electric back-up and standard modes to choose from", "Smart control to avoid cold water mixing, with LCD display", "Integrated design for outdoor installation"]',
       specs        = '[{"label": "Max. water temperature", "value": "65°C"}, {"label": "Storage volume", "value": "300 / 450 litre"}, {"label": "Refrigerant", "value": "R134a"}, {"label": "Installation", "value": "Outdoor"}]',
       documents    = '[{"label": "CAHP-1.5HP - datasheet", "url": "/docs/cahp-1-5hp.pdf"}]',
       link         = NULL
     WHERE ten = 'CAHP-1.5HP'
       AND range_id = 'heatpump'
       AND danh_muc = 'Heatpump all in one';

-- HPI Series
UPDATE san_pham
   SET
       short_text   = 'All-in-one heat pump water heater, 150 or 180 litres, with COP 3.8 and AES energy saving.',
       features     = '["Two back-up elements for quick recovery", "Patented AES energy saving system", "Timer setting", "Remote control", "PS safety system"]',
       specs        = '[{"label": "Heat pump water temperature", "value": "55°C"}, {"label": "Max. water temperature", "value": "75°C"}, {"label": "Heat pump input", "value": "460 W"}, {"label": "COP", "value": "3.8"}, {"label": "Refrigerant", "value": "R134a"}, {"label": "Volume", "value": "150 / 180 litre"}]',
       documents    = '[{"label": "HPI Series - datasheet", "url": "/docs/hpi-series.pdf"}]',
       link         = NULL
     WHERE ten = 'HPI Series'
       AND range_id = 'heatpump'
       AND danh_muc = 'Heatpump all in one';

-- DN15-32
UPDATE san_pham
   SET
       brand        = 'Danfoss',
       short_text   = 'SonoSelect 10 / SonoSafe 10 ultrasonic compact energy meter, DN15 to DN32, threaded connection.',
       mo_ta        = 'The energy meters consist of an ultrasonic flow sensor, a pair of Pt1000 temperature sensors and a calculator with integrated circuits for temperature measurement, flow calculation and energy calculation.',
       specs        = '[{"label": "Model", "value": "SonoSelect 10 (PUR cable) / SonoSafe 10 (PVC cable)"}, {"label": "Type", "value": "Ultrasonic compact energy meter"}, {"label": "Nominal diameter", "value": "DN15 - DN32"}, {"label": "Connection", "value": "Thread"}, {"label": "Power supply", "value": "230 V / 24 V / battery (16+1 years)"}, {"label": "Approvals", "value": "EN 1434 class 2 and 3\nMID (DK-0200-MI004-034)\nCPA according to JJG225-2010"}, {"label": "Communication", "value": "Wired M-Bus"}, {"label": "Working pressure", "value": "PN25"}, {"label": "Protection class", "value": "IP65"}, {"label": "Temperature sensor", "value": "Pt1000"}, {"label": "Flow sensor", "value": "Ultrasonic, accuracy ±2%"}]',
       documents    = '[{"label": "SonoSelect 10 and SonoSafe 10 - datasheet", "url": "/docs/sonoselect-10.pdf"}]',
       link         = NULL
     WHERE ten = 'DN15-32'
       AND range_id = 'metering'
       AND danh_muc = 'Energy Meter';

-- DN15-100
UPDATE san_pham
   SET
       brand        = 'Danfoss',
       short_text   = 'SonoMeter 40 ultrasonic compact energy meter, DN15 to DN100, threaded or flanged.',
       mo_ta        = 'SonoMeter 40 energy meters consist of an ultrasonic flow sensor, a pair of Pt500 temperature sensors and a calculator with integrated circuits for temperature measurement, flow calculation and energy calculation.',
       specs        = '[{"label": "Type", "value": "Ultrasonic compact energy meter"}, {"label": "Nominal diameter", "value": "DN15 - DN100"}, {"label": "Connection", "value": "Thread (G3/4 to G2) or flange (DN20 to DN100)"}, {"label": "Power supply", "value": "230 V / 24 V / battery (15+1 years)"}, {"label": "Approvals", "value": "EN 1434 class 2 and 3\nMID (DK-0200-MI004-034)\nCPA according to JJG225-2010"}, {"label": "Communication", "value": "Wired M-Bus / Modbus / BACnet / LoRaWAN / pulse output"}, {"label": "Working pressure", "value": "PN25"}, {"label": "Protection class", "value": "IP65"}, {"label": "Temperature sensor", "value": "Pt500 (Class B, EN 60751)"}, {"label": "Flow sensor", "value": "Ultrasonic, accuracy ±2%"}]',
       documents    = '[{"label": "SonoMeter 40 - datasheet", "url": "/docs/sonometer-40.pdf"}]',
       link         = NULL
     WHERE ten = 'DN15-100'
       AND range_id = 'metering'
       AND danh_muc = 'Energy Meter';

-- DN125-800
UPDATE san_pham
   SET
       brand        = 'Danfoss',
       short_text   = 'Ultrasonic compact energy meter, DN125 to DN800, flange connection.',
       mo_ta        = 'Supercal 5 and Sono3500CT are energy meters consisting of an ultrasonic flow sensor, a pair of Pt500 temperature sensors and a calculator with integrated circuits for temperature measurement, flow calculation and energy calculation.',
       specs        = '[{"label": "Type", "value": "Ultrasonic compact energy meter"}, {"label": "Configuration", "value": "Sono3500CT ultrasonic flow sensor paired with\nSupercal 5 energy calculator"}, {"label": "Nominal diameter", "value": "DN125 - DN800"}, {"label": "Connection", "value": "Flange"}, {"label": "Power supply", "value": "230 V / battery (12+1 years)"}, {"label": "Communication", "value": "Wired M-Bus / Modbus / BACnet / LoRaWAN"}, {"label": "Working pressure", "value": "PN16 / PN25"}, {"label": "Protection class", "value": "IP65"}, {"label": "Temperature sensor", "value": "Pt500 (Class B, EN 60751)"}, {"label": "Flow sensor", "value": "Ultrasonic, accuracy ±2%"}]',
       documents    = '[{"label": "Sono3500CT ultrasonic flow sensor - datasheet", "url": "/docs/sono3500ct.pdf"}, {"label": "Supercal 5 energy calculator - datasheet", "url": "/docs/supercal-5.pdf"}]',
       link         = NULL
     WHERE ten = 'DN125-800'
       AND range_id = 'metering'
       AND danh_muc = 'Energy Meter';

-- DN15-2000
UPDATE san_pham
   SET
       brand        = 'BMETERS',
       short_text   = 'MAG-C electromagnetic flow meter, DN15 to DN2000, with IP68 protection.',
       features     = '["Anti-magnetic fraud protection"]',
       specs        = '[{"label": "Type", "value": "Electromagnetic flow meter"}, {"label": "Model", "value": "MAG-C (integrated electronics)\nMAG-S (separate electronics version)"}, {"label": "Nominal diameter", "value": "DN15 - DN2000"}, {"label": "Temperature range", "value": "-40 - 80 °C"}, {"label": "Working pressure", "value": "10 / 16 / 25 / 40 bar"}, {"label": "Available flanges", "value": "UNI EN 1092, ANSI 150, ANSI 300, DIN 2501, BS 45404, AWWA"}, {"label": "Protection class", "value": "IP68"}, {"label": "Available linings", "value": "PTFE, EBANITE"}, {"label": "Pipe material", "value": "AISI 304 (AISI 316L on request)"}, {"label": "Electrode material", "value": "Hastelloy C"}, {"label": "Display", "value": "LCD graphic, 128x64 pixels"}, {"label": "Output signal", "value": "4-20 mA, pulse, frequency\nModbus RTU RS485, HART protocol"}]',
       documents    = '[{"label": "MAG-C - datasheet", "url": "/docs/mag-c.pdf"}]',
       link         = NULL
     WHERE ten = 'DN15-2000'
       AND range_id = 'metering'
       AND danh_muc = 'Flowmeter';

-- Single Jet DN15-20
UPDATE san_pham
   SET
       brand        = 'BMETERS',
       short_text   = 'GSD8-RFM single jet water meter, DN15 to DN20, for cold and hot water.',
       features     = '["Anti-magnetic fraud protection"]',
       specs        = '[{"label": "Model", "value": "GSD8-RFM"}, {"label": "Nominal diameter", "value": "DN15 - DN20"}, {"label": "Type", "value": "Single jet"}, {"label": "Cold water", "value": "0.1 - 50 °C"}, {"label": "Hot water", "value": "30 - 90 °C"}, {"label": "Working pressure", "value": "16 bar"}, {"label": "Reading", "value": "Direct reading on 8 numeric rolls"}, {"label": "Threading", "value": "EN ISO 228-1:2003"}, {"label": "Compatible modules", "value": "RFM-MB1, RFM-TX1"}, {"label": "Certification", "value": "MID, ACS, WRAS, ICIM"}]',
       documents    = '[{"label": "GSD8-RFM water meter DN15-20 - datasheet", "url": "/docs/water-meter-dn15-20-gsd8-rfm.pdf"}, {"label": "Accessory: RFM-MB1 wired M-Bus module", "url": "/docs/rfm-mb1.pdf"}, {"label": "Accessory: RFM-TX1 wireless M-Bus module", "url": "/docs/rfm-tx1.pdf"}]',
       link         = NULL
     WHERE ten = 'Single Jet DN15-20'
       AND range_id = 'metering'
       AND danh_muc = 'Water Meter';

-- Multi Jet DN15-50
UPDATE san_pham
   SET
       brand        = 'BMETERS',
       short_text   = 'GMDM-I multi jet water meter, DN15 to DN50, for cold and hot water.',
       features     = '["Anti-magnetic fraud protection"]',
       specs        = '[{"label": "Model", "value": "GMDM-I"}, {"label": "Nominal diameter", "value": "DN15 - DN50"}, {"label": "Type", "value": "Multi jet"}, {"label": "Cold water", "value": "0.1 - 50 °C"}, {"label": "Hot water", "value": "30 - 90 °C"}, {"label": "Working pressure", "value": "16 bar"}, {"label": "Reading", "value": "Direct reading on 5 numeric rolls"}, {"label": "Threading", "value": "EN ISO 228-1:2003"}, {"label": "Related meter bodies", "value": "GMB-I, GMB-RP-I, CPR-M3-I"}, {"label": "Compatible modules", "value": "IWM-TX3, IWM-LR3, IWM-MB3, IWM-PL3"}, {"label": "Certification", "value": "MID, ACS, WRAS, ICIM"}]',
       documents    = '[{"label": "Accessory: IWM-TX3 wireless M-Bus module", "url": "/docs/iwm-tx3.pdf"}, {"label": "Accessory: IWM-LR3 LoRaWAN radio module", "url": "/docs/iwm-lr3.pdf"}, {"label": "Accessory: IWM-MB3 wired M-Bus module", "url": "/docs/iwm-mb3.pdf"}, {"label": "Accessory: IWM-PL3 pulse output module", "url": "/docs/iwm-pl3.pdf"}]',
       link         = NULL
     WHERE ten = 'Multi Jet DN15-50'
       AND range_id = 'metering'
       AND danh_muc = 'Water Meter';

-- DN50-200
UPDATE san_pham
   SET
       brand        = 'BMETERS',
       short_text   = 'WDE-K50 Woltmann water meter, DN50 to DN200, flanged connection.',
       features     = '["Anti-magnetic fraud protection"]',
       specs        = '[{"label": "Model", "value": "WDE-K50"}, {"label": "Nominal diameter", "value": "DN50 - DN200"}, {"label": "Type", "value": "Woltmann"}, {"label": "Cold water", "value": "0.1 - 50 °C"}, {"label": "Hot water", "value": "30 - 90 °C"}, {"label": "Working pressure", "value": "16 bar"}, {"label": "Reading", "value": "Direct reading on 7 numeric rolls"}, {"label": "Flange", "value": "ISO 7005-2 / EN 1092-2 PN16"}, {"label": "Compatible modules", "value": "IWM-TX4, IWM-LR4, IWM-MB4, IWM-PL4"}, {"label": "Certification", "value": "MID, ACS, WRAS, ICIM"}]',
       documents    = '[{"label": "WDE-K50 water meter DN50-200 - datasheet", "url": "/docs/water-meter-dn50-200-wde-k50.pdf"}, {"label": "Accessory: IWM-TX4 wireless M-Bus OMS module", "url": "/docs/iwm-tx4.pdf"}, {"label": "Accessory: IWM-MB4 wired M-Bus module", "url": "/docs/iwm-mb4.pdf"}, {"label": "Accessory: IWM-PL4 pulse output module", "url": "/docs/iwm-pl4.pdf"}]',
       link         = NULL
     WHERE ten = 'DN50-200'
       AND range_id = 'metering'
       AND danh_muc = 'Water Meter';

-- Plate Heat Exchanger
UPDATE san_pham
   SET
       brand        = 'Danfoss Sondex',
       short_text   = 'Danfoss Sondex plate heat exchangers for hot water, swimming pool and HVAC systems.',
       applications = '["Hot water system", "Swimming pool", "HVAC system (pressure breaker)"]',
       specs        = '[{"label": "Plate material", "value": "SS304, SS316L, Titanium"}, {"label": "Gasket material", "value": "EPDM, NBRH"}, {"label": "Working pressure", "value": "10 / 16 / 25 bar"}, {"label": "Standards & approvals", "value": "ISO 14001, ISO 9001, IATF 16949\nPED, CRN, UL, ABS, BV, ASME, KRAIA, WRAS, DNV-GL, NSW, LR, RINA, WaterMark, AHRI"}]',
       documents    = '[{"label": "Plate heat exchanger - brochure", "url": "/docs/brochure-hex.pdf"}]',
       link         = NULL
     WHERE ten = 'Plate Heat Exchanger'
       AND range_id = 'phe'
       AND danh_muc IS NULL;
