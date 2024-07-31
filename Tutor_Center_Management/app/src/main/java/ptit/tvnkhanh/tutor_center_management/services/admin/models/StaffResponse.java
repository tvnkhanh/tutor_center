package ptit.tvnkhanh.tutor_center_management.services.admin.models;

import ptit.tvnkhanh.tutor_center_management.models.Staff;

public class StaffResponse {
    private String message;
    private Staff staff;

    public StaffResponse() {
    }

    public StaffResponse(String message, Staff staff) {
        this.message = message;
        this.staff = staff;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
