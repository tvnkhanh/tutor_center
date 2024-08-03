package ptit.tvnkhanh.tutor_center_management.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Tutor {
    @SerializedName("_id")
    private String _id;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("dateOfBirth")
    private Date dateOfBirth;
    @SerializedName("gender")
    private String gender;
    @SerializedName("contactNumber")
    private String contactNumber;
    @SerializedName("email")
    private String email;
    @SerializedName("portraitPhotos")
    private List<String> portraitPhotos;
    @SerializedName("address")
    private String address;
    @SerializedName("qualification")
    private List<String> qualification;
    @SerializedName("experience")
    private String experience;
    @SerializedName("graduationYear")
    private int graduationYear;
    @SerializedName("teachingTime")
    private String teachingTime;
    @SerializedName("citizenId")
    private String citizenId;
    @SerializedName("subjects")
    private List<Subject> subjects;
    @SerializedName("subjectIds")
    private List<String> subjectIds;
    @SerializedName("status")
    private String status;
    @SerializedName("registerDate")
    private Date registerDate;

    public Tutor() {
    }

    public Tutor(String _id, String firstName, String lastName, Date dateOfBirth, String gender, String contactNumber, String email, List<String> portraitPhotos, String address, List<String> qualification, String experience, int graduationYear, String teachingTime, String citizenId, List<Subject> subjects, List<String> subjectIds, String status, Date registerDate) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.email = email;
        this.portraitPhotos = portraitPhotos;
        this.address = address;
        this.qualification = qualification;
        this.experience = experience;
        this.graduationYear = graduationYear;
        this.teachingTime = teachingTime;
        this.citizenId = citizenId;
        this.subjects = subjects;
        this.subjectIds = subjectIds;
        this.status = status;
        this.registerDate = registerDate;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getPortraitPhotos() {
        return portraitPhotos;
    }

    public void setPortraitPhotos(List<String> portraitPhotos) {
        this.portraitPhotos = portraitPhotos;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getQualification() {
        return qualification;
    }

    public void setQualification(List<String> qualification) {
        this.qualification = qualification;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public int getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(int graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getTeachingTime() {
        return teachingTime;
    }

    public void setTeachingTime(String teachingTime) {
        this.teachingTime = teachingTime;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public List<String> getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(List<String> subjectIds) {
        this.subjectIds = subjectIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    @Override
    public String toString() {
        return "Tutor{" +
                "_id='" + _id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", email='" + email + '\'' +
                ", portraitPhotos=" + portraitPhotos +
                ", address='" + address + '\'' +
                ", qualification=" + qualification +
                ", experience='" + experience + '\'' +
                ", graduationYear=" + graduationYear +
                ", teachingTime='" + teachingTime + '\'' +
                ", citizenId='" + citizenId + '\'' +
                ", subjects=" + subjects +
                '}';
    }
}
