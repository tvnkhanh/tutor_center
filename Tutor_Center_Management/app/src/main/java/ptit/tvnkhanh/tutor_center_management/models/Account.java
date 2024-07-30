package ptit.tvnkhanh.tutor_center_management.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Account {
    @SerializedName("_id")
    private String _id;
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
    @SerializedName("createdAt")
    private Date createdDate;
    @SerializedName("updatedAt")
    private Date updatedDate;
    @SerializedName("status")
    private String status;
    @SerializedName("token")
    private String token;
    @SerializedName("__v")
    private int version;

    public Account() {
    }

    public Account(String _id, String username, String passwordHash, String tutorId, String clientId, String staffId, String roleId, Date createdDate, Date updatedDate, String status, String token, int version) {
        this._id = _id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.tutorId = tutorId;
        this.clientId = clientId;
        this.staffId = staffId;
        this.roleId = roleId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.status = status;
        this.token = token;
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Account{" +
                "_id='" + _id + '\'' +
                ", username='" + username + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", tutorId='" + tutorId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", staffId='" + staffId + '\'' +
                ", roleId='" + roleId + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", status='" + status + '\'' +
                ", token='" + token + '\'' +
                ", version=" + version +
                '}';
    }
}
