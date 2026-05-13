package demo.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Data {
  public static class Spec { public String label, value; public Spec(String l,String v){label=l;value=v;} }
  public static class Product {
    public String id, name, category, image, shortText, description, link;
    public List<String> features = new ArrayList<>();
    public List<String> applications = new ArrayList<>();
    public List<Spec> specs = new ArrayList<>();
    public String brand, rangeId;
  }
  public static class Range { public String id, brand, tagline; public List<Map<String,String>> categories = new ArrayList<>(); public List<Product> products = new ArrayList<>(); }
  public static class Project { public String name, image, location, investor; public List<String> products = new ArrayList<>(); }
  public static class Section { public String title; public List<Project> projects = new ArrayList<>(); }
  public static class Logo { public String src, name; public Logo(String s, String n){src=s;name=n;} }
  public static class LogoGroup { public String title; public List<Logo> logos = new ArrayList<>(); }

  public static List<Range> RANGES = new ArrayList<>();
  public static List<Section> PROJECTS = new ArrayList<>();
  public static List<LogoGroup> CUSTOMERS = new ArrayList<>();

  static Product mk(String id,String name,String cat,String img,String sh,String desc,String[] feats,String[] apps,Spec[] specs,String link){
    Product p = new Product();
    p.id=id; p.name=name; p.category=cat; p.image=img; p.shortText=sh; p.description=desc; p.link=link;
    Collections.addAll(p.features, feats); Collections.addAll(p.applications, apps);
    if(specs!=null) Collections.addAll(p.specs, specs);
    return p;
  }
  static Map<String,String> cat(String id, String label){ Map<String,String> m = new LinkedHashMap<>(); m.put("id",id); m.put("label",label); return m; }

  static {
    Range om = new Range(); om.id="omeax"; om.brand="OMEAX"; om.tagline="Bơm & Van công nghiệp";
    om.categories.add(cat("pumps","Pumps")); om.categories.add(cat("valves","Valves"));
    om.products.add(mk("om-booster","Booster Pump","pumps","/images/pump1.jpg","Bơm tăng áp biến tần",
      "Hệ tăng áp biến tần OMEAX dành cho tòa nhà cao tầng, cấp nước sinh hoạt ổn định, tiết kiệm điện năng tới 40%.",
      new String[]{"Inverter điều khiển PID","Vỏ inox 304","Áp suất tối đa 16 bar","Lưu lượng tới 80 m³/h"},
      new String[]{"Tòa nhà văn phòng","Khu chung cư","Khách sạn"},
      new Spec[]{new Spec("Lưu lượng","5 – 80 m³/h"), new Spec("Cột áp","20 – 160 m"), new Spec("Công suất","1.5 – 30 kW"), new Spec("Nguồn","3P / 380V / 50Hz")}, null));
    om.products.add(mk("om-transfer","Transfer Pump","pumps","/images/pump2.jpg","Bơm chuyển nước",
      "Bơm trục ngang công suất lớn, chuyên cấp & chuyển nước giữa các bể chứa.",
      new String[]{"Cánh đồng thau","Hiệu suất cao IE3","Vận hành êm < 60 dB"},
      new String[]{"Cấp nước sinh hoạt","Hệ thống tưới"},
      new Spec[]{new Spec("Lưu lượng","10 – 300 m³/h"), new Spec("Cột áp","10 – 100 m"), new Spec("Công suất","2.2 – 75 kW")}, null));
    om.products.add(mk("om-hvac","HVAC Pump","pumps","/images/pump3.jpg","Bơm tuần hoàn HVAC",
      "Bơm tuần hoàn nước lạnh / nóng cho hệ thống chiller và AHU.",
      new String[]{"Mặt bích DN50–DN300","Thân gang đúc","Phớt cơ khí"},
      new String[]{"Hệ chiller","Hệ sưởi sàn"},
      new Spec[]{new Spec("Cỡ kết nối","DN50 – DN300"), new Spec("Nhiệt độ","-10°C – 120°C")}, null));
    om.products.add(mk("om-drain","Drainage Pump","pumps","/images/pump4.jpg","Bơm chìm thoát nước",
      "Bơm chìm chuyên thoát nước hố ga, nước thải sinh hoạt.",
      new String[]{"IP68","Cánh xé rác","Phao tự động"},
      new String[]{"Tầng hầm","Trạm bơm thoát"},
      new Spec[]{new Spec("Cấp bảo vệ","IP68"), new Spec("Lưu lượng","5 – 50 m³/h")}, null));
    om.products.add(mk("om-ff","Fire Fighting Pump","pumps","/images/pump5.jpg","Bơm chữa cháy UL/FM",
      "Cụm bơm chữa cháy đạt tiêu chuẩn UL/FM cho công trình thương mại.",
      new String[]{"Bơm chính + bù áp","Tủ điều khiển tự động","Theo NFPA 20"},
      new String[]{"PCCC nhà xưởng","PCCC chung cư"},
      new Spec[]{new Spec("Tiêu chuẩn","UL / FM / NFPA 20")}, null));
    om.products.add(mk("om-general","General Valves","valves","/images/valve5.jpg","Van chặn đa năng",
      "Van cổng, van bi, van một chiều dùng cho hệ nước & PCCC.",
      new String[]{"DN15–DN600","Gang / inox / đồng","PN16 – PN25"},
      new String[]{"Cấp nước","PCCC","HVAC"},
      new Spec[]{new Spec("Kích cỡ","DN15 – DN600"), new Spec("Áp suất","PN16 – PN25")}, null));
    om.products.add(mk("om-hydraulic","Hydraulic Control Valve","valves","/images/valve3.jpg","Van điều khiển thủy lực",
      "Van điều khiển thủy lực giảm áp, duy trì áp suất, ngắt khi quá tải.",
      new String[]{"Pilot tự động","Không cần điện","Bền 20+ năm"},
      new String[]{"Mạng cấp nước đô thị","Trạm bơm"}, null, null));
    om.products.add(mk("om-special","Special Application Valve","valves","/images/valve1.jpg","Van ứng dụng đặc biệt",
      "Van bướm, van dao chuyên dùng cho môi trường ăn mòn, nước biển.",
      new String[]{"Body inox 316","Chống ăn mòn","Wafer / Lug"},
      new String[]{"Hóa chất","Xử lý nước thải"}, null, null));
    RANGES.add(om);

    Range gb = new Range(); gb.id="gbse"; gb.brand="GBSE"; gb.tagline="Van cân bằng · Heatpump · Trao đổi nhiệt · Đo lường";
    gb.categories.add(cat("valves","Valves")); gb.categories.add(cat("heatpump","Heatpump"));
    gb.categories.add(cat("measure","Measurement")); gb.categories.add(cat("phe","Plate Heat Exchanger"));
    gb.products.add(mk("gb-picv","PICV / Manual Balancing Valve","valves","/images/valve2.jpg","Van cân bằng PICV",
      "Van cân bằng áp suất độc lập (PICV) đảm bảo lưu lượng ổn định cho FCU/AHU.",
      new String[]{"Pre-setting lưu lượng","Range 0.05–4.0 m³/h","Đồng thau"},
      new String[]{"Hệ chiller","FCU văn phòng"}, null, null));
    gb.products.add(mk("gb-motor","Motorized Valve","valves","/images/valve4.jpg","Van điều khiển động cơ",
      "Van điều khiển modulating 0–10V tích hợp BMS.",
      new String[]{"Tín hiệu 0–10V / Modbus","Lực đóng 600N","Fail-safe"},
      new String[]{"BMS tòa nhà","Hệ chiller"}, null, null));
    gb.products.add(mk("gb-hp-aw","Air to Water Heatpump","heatpump","/images/ct1.jpg","Heatpump không khí – nước",
      "Bơm nhiệt không khí – nước tạo nước nóng tới 65°C, COP > 4.5.",
      new String[]{"Inverter DC","R32 thân thiện môi trường","Công suất 7–80 kW"},
      new String[]{"Khách sạn","Bể bơi","Spa"}, null, null));
    gb.products.add(mk("gb-hp-aio","All-in-one Heatpump","heatpump","/images/ct2.jpg","Heatpump tích hợp bồn",
      "Bơm nhiệt all-in-one tích hợp bồn 200L, gọn nhẹ cho hộ gia đình.",
      new String[]{"Bồn inox 200L","WiFi điều khiển","Plug & play"},
      new String[]{"Biệt thự","Căn hộ"}, null, null));
    gb.products.add(mk("gb-meter","Water Meter (Cold/Hot)","measure","/images/valve6.jpg","Đồng hồ nước nóng / lạnh",
      "Đồng hồ đo lưu lượng nước nóng và lạnh, cấp chính xác R160.",
      new String[]{"DN15–DN50","Chịu nhiệt 90°C","MID Class 2"},
      new String[]{"Chung cư","Tòa nhà"}, null, null));
    gb.products.add(mk("gb-btu","Ultrasonic BTU Meter","measure","/images/valve3.jpg","Đồng hồ nhiệt lượng siêu âm",
      "Đồng hồ nhiệt lượng siêu âm, không có chi tiết chuyển động, độ bền cao.",
      new String[]{"Truyền M-Bus / Modbus","Pin 10 năm","Cấp 2 EN1434"},
      new String[]{"Tính tiền BTU chung cư","District cooling"}, null, null));
    gb.products.add(mk("gb-phe","Plate Heat Exchanger","phe","/images/ct3.jpg","Trao đổi nhiệt dạng tấm",
      "PHE inox 316L, gasket NBR/EPDM, hiệu suất truyền nhiệt cao.",
      new String[]{"Công suất 10 kW – 5 MW","Inox 316L","Tháo lắp dễ dàng"},
      new String[]{"Hot water","Chiller plant","Industrial"}, null, null));
    RANGES.add(gb);

    Range pa = new Range(); pa.id="partners"; pa.brand="Partners"; pa.tagline="Hệ thống ứng dụng — liên kết hãng";
    pa.categories.add(cat("hvac","HVAC System")); pa.categories.add(cat("hot","Hot Water Supply")); pa.categories.add(cat("indus","Industrial Water"));
    pa.products.add(mk("pa-hvac","HVAC System","hvac","/images/pump3.jpg","Hệ thống điều hòa trung tâm",
      "Giải pháp HVAC hoàn chỉnh từ chiller, AHU, FCU đến hệ điều khiển BMS.",
      new String[]{"Chiller water-cooled / air-cooled","AHU & FCU","Tích hợp BMS"},
      new String[]{"Văn phòng","TTTM","Bệnh viện"}, null, "https://www.grundfos.com/vn"));
    pa.products.add(mk("pa-hot","Hot Water Supply System","hot","/images/ct1.jpg","Hệ cấp nước nóng tập trung",
      "Hệ thống cấp nước nóng dùng heatpump + bồn tích, tối ưu năng lượng cho khách sạn.",
      new String[]{"Bồn inox 1000–10000 L","Cụm bơm tuần hoàn","Điều khiển tự động"},
      new String[]{"Khách sạn","Khu nghỉ dưỡng"}, null, "https://oceancoolingtower.com/"));
    pa.products.add(mk("pa-indus","Industrial Water System","indus","/images/ct2.jpg","Hệ nước công nghiệp",
      "Hệ thống nước làm mát, nước cấp, nước xử lý cho nhà máy sản xuất.",
      new String[]{"Cooling tower","Hệ lọc đa tầng","Hóa chất xử lý"},
      new String[]{"Nhà máy thép","Nhà máy hoá chất"}, null, "https://omeax.com/"));
    RANGES.add(pa);

    for(Range r : RANGES) for(Product p : r.products){ p.brand = r.brand; p.rangeId = r.id; }

    // Projects
    PROJECTS.add(sec("High-Rise Building (Office – Complex)", new Project[]{
      proj("CAPITAL PLACE (office tower)","/images/proj_capital.png","Lieu Giai, Ba Dinh, Ha Noi.","VINGROUP","PICV, MBV, MV valves|HEX for HVAC|Water meter, BTU meter"),
      proj("TECHNOPARK TOWER (office tower)","/images/proj_techno.png","Gia Lam, Ha Noi.","VINGROUP","PICV, MBV, MV valves|HEX for HVAC|Water meter, BTU meter"),
      proj("DIAMOND CROWN PLAZA (complex)","/images/proj_diamond.png","Le Hong Phong, Ngo Quyen, Hai Phong.","DOJI GROUP","PICV, MBV, MV valves|Water meter, BTU meter|SS expansion joint"),
      proj("SUN GRAND CITY – TAY HO VIEW","/images/proj_sungrand.png","Quang An, Ha Noi.","SUNGROUP","Pumps for WS, HVAC, SW|HEX for HVAC|HEX for hot water system|Water meter"),
      proj("TIEN BO PLAZA","/images/proj_tienbo.png","Nguyen Thai Hoc, Hanoi","TDI","Pumps for WS, HVAC, SW|HEX for HVAC|HEX for hot water system|Water meter"),
      proj("GOLD MARK CITY","/images/proj_goldmark.png","Ha Noi","TNR","Pumps for WS, HVAC system|Heat exchanger for HVAC system"),
    }));
    PROJECTS.add(sec("High-Rise Building (Residential)", new Project[]{
      proj("VINHOMES GRAND PARK","/images/proj_vinhomesgrand.png","District 9, HCMC.","VINGROUP","Hydraulic control valves|PICV, MBV, MV valves|Heatpump|Water meter, BTU meter"),
      proj("VINHOMES OCEAN PARK","/images/proj_vinhomesocean.png","Ha Noi – Hung Yen","VINGROUP","Pumps & Heatpump|Hydraulic control valves|PICV, MBV, MV valves|Water meter, BTU meter"),
    }));
    PROJECTS.add(sec("Exhibition Center – Data Center", new Project[]{
      proj("THE GRAND EXPO, VIETNAM EXPOSITION CENTER","/images/proj_grandexpo.png","Dong Anh, Ha Noi.","VINGROUP","PICV, MV valves"),
      proj("CMC DATA CENTER","/images/proj_cmcdc.png","Tan Thuan, HCMC","CMC Group","PICV, MBV, MV valves"),
    }));
    PROJECTS.add(sec("Resort – Shopping Mall – Factory", new Project[]{
      proj("VINFAST – VINES (Car & Battery factories)","/images/proj_vinfast.png","Hai Phong, Ha Tinh.","VINGROUP","PICV, MBV, MV valves|Heatpump"),
      proj("ONSEN (resort)","/images/proj_onsen.png","Quang Hanh, Quang Ninh.","SUNGROUP","HEX for hot water supply|MV, CV valves"),
      proj("VINCOM CENTER (Shopping mall)","/images/proj_vincom.png","Ha Noi, HCMC, Hai Phong, Ninh Binh, ...","VINGROUP","PICV, MBV, MV valves|Hydraulic valves"),
    }));
    PROJECTS.add(sec("Hotel", new Project[]{
      proj("HYATT REGENCY HA LONG","/images/proj_hyatt.png","Ha Long – Quang Ninh","Dong Gia","Pumps for WS, HVAC"),
      proj("SHERATON HA GIANG","/images/proj_sheraton.png","Ha Giang","VINGROUP","PICV, MBV, MV valves|Heatpump|Hydraulic control valves"),
      proj("MARRIOTT BAC NINH","/images/proj_marriott.png","Bac Ninh","VINGROUP","PICV, MBV, MV valves|Heatpump|Hydraulic control valves"),
    }));
    PROJECTS.add(sec("Hospital Building", new Project[]{
      proj("VINMEC (8 x Hospitals)","/images/proj_vinmec.png","Hanoi, HCMC, Phu Quoc, Nha Trang, ...","VINGROUP","PICV, MBV, MV valves|Heatpump"),
      proj("HONG NGOC HOSPITAL (phase 1 & 2)","/images/proj_hongngoc.png","My Dinh, Ha Noi.","HONG NGOC HOSPITAL GROUP","Pumps for WS, HVAC, SW|PICV, MBV, MV valves"),
    }));
    PROJECTS.add(sec("Water Park", new Project[]{
      proj("SAM SON PARK","/images/proj_samson.png","Sam Son, Thanh Hoa","SUNGROUP","Pumps for game & show|CV valves"),
      proj("HA NAM PARK","/images/proj_hanam.png","Phu Ly, Ha Nam","SUNGROUP","Pumps for game & show|CV valves"),
      proj("CUA HOI PARK","/images/proj_cuahoi.png","Nghe An","VinGroup","Pumps for WS, game & show system"),
    }));

    // Customers
    CUSTOMERS.add(grp("Developers", new String[][]{
      {"vingroup","Vingroup"},{"sungroup","Sun Group"},{"dojiland","DojiLand"},{"ecopark","Ecopark"},
      {"cmc","CMC Corporation"},{"brg","BRG Group"},{"tnr","TNR"},{"capitaland","CapitaLand"},
      {"everland","Everland Group"},{"tt","T&T Group"},{"mik","MIK Group"},{"bitexco","Bitexco Group"},
    }));
    CUSTOMERS.add(grp("Contractors", new String[][]{
      {"ree","REE M&E"},{"coteccons","Coteccons"},{"searefico","Searefico"},{"sol","SOL E&C"},
      {"sigma","Sigma Engineering"},{"kg","KG"},{"unicons","Unicons"},{"newtecons","Newtecons"},
      {"ricons","Ricons"},{"delta","Delta E&C"},{"hoabinh","Hoabinh"},{"nsc","North Star Construction"},
    }));
    CUSTOMERS.add(grp("Designers", new String[][]{
      {"jncc","JNCC"},{"indochine","Indochine Engineering"},{"boydens","Boydens Engineering"},{"fai","FAI Asia"},
      {"cdc","CDC Construction"},{"coninco","Coninco"},{"archetype","Archetype"},{"sp","SP"},{"meinhardt","Meinhardt"},
    }));
  }

  static Project proj(String n,String img,String loc,String inv,String prods){
    Project p = new Project(); p.name=n; p.image=img; p.location=loc; p.investor=inv;
    for(String s : prods.split("\\|")) p.products.add(s);
    return p;
  }
  static Section sec(String t, Project[] ps){ Section s = new Section(); s.title=t; Collections.addAll(s.projects, ps); return s; }
  static LogoGroup grp(String t, String[][] logos){
    LogoGroup g = new LogoGroup(); g.title=t;
    for(String[] l : logos) g.logos.add(new Logo("/images/cust_"+l[0]+".png", l[1]));
    return g;
  }

  public static Map<String,Object> findProduct(String id){
    for(Range r : RANGES) for(Product p : r.products) if(p.id.equals(id)){
      Map<String,Object> m = new HashMap<>(); m.put("product",p); m.put("range",r); return m;
    }
    return null;
  }

  public static List<Product> allProducts(){
    List<Product> all = new ArrayList<>();
    for(Range r : RANGES) all.addAll(r.products);
    return all;
  }
}
