package ptit.tvnkhanh.tutor_center_management.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class TutoringClass {
    @SerializedName("_id")
    private String _id;
    @SerializedName("studentInfo")
    private String studentInfo;
    @SerializedName("schedule")
    private List<String> schedule;
    @SerializedName("status")
    private String status;
    @SerializedName("salary")
    private String salary;
    @SerializedName("form")
    private String form;
    @SerializedName("requirement")
    private String requirement;
    @SerializedName("address")
    private String address;
    @SerializedName("subjectIds")
    private List<String> subjects;
    @SerializedName("tutorId")
    private List<String> tutorId;
    @SerializedName("clientId")
    private String clientId;
    @SerializedName("subjectDetails")
    private List<Subject> subjectsDetails;
    @SerializedName("updateDate")
    private Date updateDate;  // Add this field

    public TutoringClass() {
    }

    public TutoringClass(String _id, String studentInfo, List<String> schedule, String status, String salary, String form, String requirement, String address, List<String> subjects, List<String> tutorId, String clientId, List<Subject> subjectDetails, Date updateDate) {
        this._id = _id;
        this.studentInfo = studentInfo;
        this.schedule = schedule;
        this.status = status;
        this.salary = salary;
        this.form = form;
        this.requirement = requirement;
        this.address = address;
        this.subjects = subjects;
        this.tutorId = tutorId;
        this.clientId = clientId;
        this.subjectsDetails = subjectDetails;
        this.updateDate = updateDate;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getStudentInfo() {
        return studentInfo;
    }

    public void setStudentInfo(String studentInfo) {
        this.studentInfo = studentInfo;
    }

    public List<String> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<String> schedule) {
        this.schedule = schedule;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public List<String> getTutorId() {
        return tutorId;
    }

    public void setTutorId(List<String> tutorId) {
        this.tutorId = tutorId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<Subject> getSubjectDetails() {
        return subjectsDetails;
    }

    public void setSubjectDetails(List<Subject> subjectsDetails) {
        this.subjectsDetails = subjectsDetails;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "TutoringClass{" +
                "_id='" + _id + '\'' +
                ", studentInfo='" + studentInfo + '\'' +
                ", schedule=" + schedule +
                ", status='" + status + '\'' +
                ", salary='" + salary + '\'' +
                ", form='" + form + '\'' +
                ", requirement='" + requirement + '\'' +
                ", address='" + address + '\'' +
                ", subjects=" + subjects +
                ", tutorId=" + tutorId +
                ", clientId='" + clientId + '\'' +
                ", subjectsDetails=" + subjectsDetails +
                ", updateDate=" + updateDate +
                '}';
    }
}
