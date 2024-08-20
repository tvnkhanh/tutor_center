package ptit.tvnkhanh.tutor_center_management.services.admin.models;

public class ReasonRequest {
    private String classId;
    private String tutorId;
    private String reason;

    public ReasonRequest() {
    }

    public ReasonRequest(String classId, String tutorId, String reason) {
        this.classId = classId;
        this.tutorId = tutorId;
        this.reason = reason;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}