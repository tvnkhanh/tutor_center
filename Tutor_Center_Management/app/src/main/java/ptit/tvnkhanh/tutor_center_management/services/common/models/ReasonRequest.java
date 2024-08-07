package ptit.tvnkhanh.tutor_center_management.services.common.models;

public class ReasonRequest {
    private String classId;
    private String reason;

    public ReasonRequest() {
    }

    public ReasonRequest(String classId, String reason) {
        this.classId = classId;
        this.reason = reason;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
