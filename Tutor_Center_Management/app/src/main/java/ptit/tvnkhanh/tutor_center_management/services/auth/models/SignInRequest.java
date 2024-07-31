package ptit.tvnkhanh.tutor_center_management.services.auth.models;

import com.google.gson.annotations.SerializedName;

public class SignInRequest {
    @SerializedName("username")
    private String username;
    @SerializedName("passwordHash")
    private String passwordHash;

    public SignInRequest(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
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
}
