package demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
@Entity
@Table(name = "yeu_cau")
public class YeuCau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be 2–100 characters")
    @Column(columnDefinition = "varchar(255)")
    private String hoTen;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{9,11}$", message = "Invalid phone number (9–11 digits)")
    @Column(columnDefinition = "varchar(20)")
    private String soDienThoai;

    @Email(message = "Invalid email address")
    @Column(columnDefinition = "varchar(255)")
    private String email;

    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    @Column(columnDefinition = "varchar(1000)")
    private String noiDung;

    private LocalDateTime thoiGian;

    @Column(nullable = false)
    private boolean contacted;

    private String adminNote;

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

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }

    public boolean isContacted() { return contacted; }
    public void setContacted(boolean contacted) { this.contacted = contacted; }

    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }
}