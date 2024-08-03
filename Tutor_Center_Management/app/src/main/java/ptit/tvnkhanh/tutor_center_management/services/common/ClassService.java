package ptit.tvnkhanh.tutor_center_management.services.common;

import java.util.List;

import ptit.tvnkhanh.tutor_center_management.models.TutoringClass;
import ptit.tvnkhanh.tutor_center_management.services.common.models.PaymentInfoDataResponse;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ClassService {
    @GET("/api/classes")
    Call<List<TutoringClass>> getClasses(@Header(Constants.X_AUTH_TOKEN) String token);

    @PUT("/api/{classId}/tutor-register/{tutorId}")
    Call<TutoringClass> registerTutor(@Header(Constants.X_AUTH_TOKEN) String token, @Path("classId") String classId, @Path("tutorId") String tutorId);

    @POST("/api/create-class")
    Call<TutoringClass> createClass(@Header(Constants.X_AUTH_TOKEN) String token, @Body TutoringClass tutoringClass);

    @GET("/api/classes/{id}")
    Call<List<TutoringClass>> getClassById(@Header(Constants.X_AUTH_TOKEN) String token, @Path("id") String id);

    @GET("/api/get-payment-info/tutor")
    Call<List<PaymentInfoDataResponse>> getClassesPaymentInfoByTutorId(@Header(Constants.X_AUTH_TOKEN) String token, @Query("tutorId") String tutorId);

    @GET("/api/get-payment-info/client")
    Call<List<PaymentInfoDataResponse>> getClassesPaymentInfoByClientId(@Header(Constants.X_AUTH_TOKEN) String token, @Query("clientId") String clientId);
}
