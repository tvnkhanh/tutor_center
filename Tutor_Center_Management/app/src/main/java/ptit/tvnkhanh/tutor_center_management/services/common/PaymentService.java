package ptit.tvnkhanh.tutor_center_management.services.common;

import ptit.tvnkhanh.tutor_center_management.models.Payment;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PaymentService {
    @POST("/api/make-payment")
    Call<Payment> makePayment(@Header(Constants.X_AUTH_TOKEN) String token, @Body Payment payment);
}
