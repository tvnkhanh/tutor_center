package ptit.tvnkhanh.tutor_center_management.services.common;

import java.util.List;

import ptit.tvnkhanh.tutor_center_management.models.Subject;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface SubjectService {
    @GET("/api/subjects")
    Call<List<Subject>> getSubjects(@Header(Constants.X_AUTH_TOKEN) String token);
}
