package ptit.tvnkhanh.tutor_center_management.services.admin.models;

public class StatusRequest {
    private String status;

    public StatusRequest() {
    }

    public StatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
