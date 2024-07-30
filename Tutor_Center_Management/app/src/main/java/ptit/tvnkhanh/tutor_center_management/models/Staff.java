package ptit.tvnkhanh.tutor_center_management.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Staff {
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
    @SerializedName("taxId")
    private String taxId;
    @SerializedName("address")
    private String address;
    @SerializedName("qualification")
    private List<String> qualification;
    @SerializedName("position")
    private String position;
    @SerializedName("citizenId")
    private String citizenId;
    @SerializedName("status")
    private String status;

    public Staff() {
    }

    public Staff(String _id, String firstName, String lastName, Date dateOfBirth, String gender, String contactNumber, String email, List<String> portraitPhotos, String taxId, String address, List<String> qualification, String position, String citizenId, String status) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.email = email;
        this.portraitPhotos = portraitPhotos;
        this.taxId = taxId;
        this.address = address;
        this.qualification = qualification;
        this.position = position;
        this.citizenId = citizenId;
        this.status = status;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
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

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "_id='" + _id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", email='" + email + '\'' +
                ", portraitPhotos=" + portraitPhotos +
                ", taxId='" + taxId + '\'' +
                ", address='" + address + '\'' +
                ", qualification=" + qualification +
                ", position='" + position + '\'' +
                ", citizenId='" + citizenId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
