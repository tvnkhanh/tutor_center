package ptit.tvnkhanh.tutor_center_management.services.admin.models;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;

public class RevenueResponse {
    @SerializedName("classId")
    private String classId;
    @SerializedName("clientId")
    private String clientId;
    @SerializedName("tutorId")
    private String tutorId;
    @SerializedName("amount")
    private double revenue;
    @SerializedName("paymentDate")
    private Date paymentDate;

    public RevenueResponse() {
    }

    public RevenueResponse(String classId, String clientId, String tutorId, double revenue, Date paymentDate) {
        this.classId = classId;
        this.clientId = clientId;
        this.tutorId = tutorId;
        this.revenue = revenue;
        this.paymentDate = paymentDate;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
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
        return String.format("%02d", calendar.get(Calendar.MONTH) + 1);
    }
}
