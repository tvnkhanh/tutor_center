package ptit.tvnkhanh.tutor_center_management.services.admin.models;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;

import ptit.tvnkhanh.tutor_center_management.models.Client;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;

public class RevenueResponse {
    @SerializedName("classId")
    private String classId;
    @SerializedName("clientId")
    private Client client;
    @SerializedName("tutorId")
    private Tutor tutor;
    @SerializedName("revenue")
    private double revenue;
    @SerializedName("paymentDate")
    private Date paymentDate;

    public RevenueResponse() {
    }

    public RevenueResponse(String classId, Client client, Tutor tutor, double revenue, Date paymentDate) {
        this.classId = classId;
        this.client = client;
        this.tutor = tutor;
        this.revenue = revenue;
        this.paymentDate = paymentDate;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(paymentDate);
        return String.valueOf(calendar.get(Calendar.MONTH) + 1);
    }
}
