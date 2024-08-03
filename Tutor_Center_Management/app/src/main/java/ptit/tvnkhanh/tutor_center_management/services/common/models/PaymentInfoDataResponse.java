package ptit.tvnkhanh.tutor_center_management.services.common.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ptit.tvnkhanh.tutor_center_management.models.Client;
import ptit.tvnkhanh.tutor_center_management.models.Payment;
import ptit.tvnkhanh.tutor_center_management.models.Subject;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;

public class PaymentInfoDataResponse {
    private String _id;
    private String studentInfo;
    private List<String> schedule;
    private String status;
    private String salary;
    @SerializedName("tutorId")
    private List<Tutor> tutors;
    @SerializedName("clientId")
    private Client client;
    private String form;
    private String requirement;
    private String address;
    private List<Subject> subjects;
    private String paymentStatus;
    private Payment payment;

    public PaymentInfoDataResponse() {
    }

    public PaymentInfoDataResponse(String _id, String studentInfo, List<String> schedule, String status, String salary, List<Tutor> tutors, Client client, String form, String requirement, String address, List<Subject> subjects, String paymentStatus, Payment payment) {
        this._id = _id;
        this.studentInfo = studentInfo;
        this.schedule = schedule;
        this.status = status;
        this.salary = salary;
        this.tutors = tutors;
        this.client = client;
        this.form = form;
        this.requirement = requirement;
        this.address = address;
        this.subjects = subjects;
        this.paymentStatus = paymentStatus;
        this.payment = payment;
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

    public List<Tutor> getTutors() {
        return tutors;
    }

    public void setTutors(List<Tutor> tutors) {
        this.tutors = tutors;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
