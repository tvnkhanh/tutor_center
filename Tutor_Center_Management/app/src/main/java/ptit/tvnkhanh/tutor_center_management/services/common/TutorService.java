package ptit.tvnkhanh.tutor_center_management.services.common;

import java.util.List;

import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.services.common.models.TutorRequest;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TutorService {
    @GET("/api/tutor/get")
    Call<Tutor> getTutor(@Header(Constants.X_AUTH_TOKEN) String token);

    @GET("/api/tutors")
    Call<List<Tutor>> getAllTutors(@Header(Constants.X_AUTH_TOKEN) String token);

    @GET("/api/get-tutor")
    Call<Tutor> getTutorById(@Header(Constants.X_AUTH_TOKEN) String token, @Query("tutorId") String id);

    @PUT("api/tutors/{id}")
    Call<Tutor> updateTutor(@Path("id") String id, @Body TutorRequest tutorRequest);

    @GET("/api/search-tutor")
    Call<List<Tutor>> searchTutor(@Header(Constants.X_AUTH_TOKEN) String token, @Query("q") String tutorName);

    @GET("/api/search-tutor-contact")
    Call<List<Tutor>> searchTutorByContact(@Header(Constants.X_AUTH_TOKEN) String token, @Query("q") String contactNumber);

    @POST("/api/tutor")
    Call<Tutor> createTutor(@Body TutorRequest tutor);

    @GET("/api/tutor-id")
    Call<String> getTutorId(@Query("contactNumber") String contactNumber);
}
