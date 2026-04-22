package demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "yeu_cau")
public class YeuCau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

@Column(columnDefinition = "varchar(255)")
private String hoTen;

@Column(columnDefinition = "varchar(20)")
private String soDienThoai;

@Column(columnDefinition = "varchar(255)")
private String email;

@Column(columnDefinition = "varchar(255)")
private String sanPhamQuanTam;

@Column(columnDefinition = "varchar(1000)")
private String noiDung;

    private LocalDateTime thoiGian;

    @PrePersist
    public void prePersist() {
        thoiGian = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSanPhamQuanTam() { return sanPhamQuanTam; }
    public void setSanPhamQuanTam(String sanPhamQuanTam) { this.sanPhamQuanTam = sanPhamQuanTam; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }
}