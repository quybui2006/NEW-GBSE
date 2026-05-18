package demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Form-binding object for the public contact form.
 * Decouples HTTP form input from the YeuCau JPA entity so that
 * adding internal fields to the entity (status, processedAt, etc.)
 * never exposes them to mass-assignment from crafted POST requests.
 */
public class YeuCauFormDto {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be 2–100 characters")
    private String hoTen;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{9,11}$", message = "Invalid phone number (9–11 digits)")
    private String soDienThoai;

    @Email(message = "Invalid email address")
    private String email;

    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    private String noiDung;

    // Getters and setters

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
}
