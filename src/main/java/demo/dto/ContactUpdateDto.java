package demo.dto;

import jakarta.validation.constraints.Size;

public class ContactUpdateDto {

    private boolean contacted;

    @Size(max = 1000, message = "Note must be 1000 characters or fewer")
    private String adminNote;

    public boolean isContacted() { return contacted; }
    public void setContacted(boolean contacted) { this.contacted = contacted; }

    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }
}
