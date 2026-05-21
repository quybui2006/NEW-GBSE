package demo.dto;

import jakarta.validation.constraints.Size;

public class AdminYeuCauUpdateDto {
    private boolean contacted;
    @Size(
        max=1000,
        message="100 characters"
    )
    private String adminNote;
    public boolean isContacted() { return contacted; }
    public void setContacted(boolean contacted) { this.contacted = contacted; }
    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }

}
