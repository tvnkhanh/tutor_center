package ptit.tvnkhanh.tutor_center_management.services.auth.models;

import com.google.gson.annotations.SerializedName;

public class SignUpRequest {
    @SerializedName("username")
    private String username;
    @SerializedName("passwordHash")
    private String passwordHash;
    @SerializedName("tutorId")
    private String tutorId;
    @SerializedName("clientId")
    private String clientId;
    @SerializedName("staffId")
    private String staffId;
    @SerializedName("roleId")
    private String roleId;

    public SignUpRequest() {
    }

    public SignUpRequest(String username, String passwordHash, String tutorId, String clientId, String staffId, String roleId) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.tutorId = tutorId;
        this.clientId = clientId;
        this.staffId = staffId;
        this.roleId = roleId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
