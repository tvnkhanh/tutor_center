package ptit.tvnkhanh.tutor_center_management.services.common.models;

import java.util.List;

public class TutorRequest {
    private String _id;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String contactNumber;
    private String email;
    private List<String> portraitPhotos;
    private String address;
    private List<String> qualification;
    private String experience;
    private int graduationYear;
    private String teachingTime;
    private String citizenId;
    private List<String> subjectIds;

    public TutorRequest() {
    }

    public TutorRequest(String _id, String firstName, String lastName, String dateOfBirth, String gender, String contactNumber, String email, List<String> portraitPhotos, String address, List<String> qualification, String experience, int graduationYear, String teachingTime, String citizenId, List<String> subjectIds) {
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
        this.subjectIds = subjectIds;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
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

    public List<String> getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(List<String> subjectIds) {
        this.subjectIds = subjectIds;
    }
}
