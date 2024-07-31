package ptit.tvnkhanh.tutor_center_management.services.common;

import java.util.List;

import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface TutorService {
    @GET("/api/tutor/get")
    Call<Tutor> getTutor(@Header(Constants.X_AUTH_TOKEN) String token);

    @GET("/api/tutors")
    Call<List<Tutor>> getAllTutors(@Header(Constants.X_AUTH_TOKEN) String token);
}
