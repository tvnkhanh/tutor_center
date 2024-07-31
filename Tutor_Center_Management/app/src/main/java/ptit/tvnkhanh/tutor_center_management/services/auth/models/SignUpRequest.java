package ptit.tvnkhanh.tutor_center_management.services.auth.models;

import com.google.gson.annotations.SerializedName;

public class SignUpRequest {
    @SerializedName("username")
    private String username;
    @SerializedName("passwordHash")
    private String passwordHash;
    @SerializedName("roleId")
    private String roleId;

    public SignUpRequest(String username, String passwordHash, String roleId) {
        this.username = username;
        this.passwordHash = passwordHash;
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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
