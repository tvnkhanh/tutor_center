package ptit.tvnkhanh.tutor_center_management.services.admin;

import androidx.annotation.Nullable;

import java.util.List;

import ptit.tvnkhanh.tutor_center_management.models.Staff;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.models.TutoringClass;
import ptit.tvnkhanh.tutor_center_management.services.admin.models.RevenueResponse;
import ptit.tvnkhanh.tutor_center_management.services.admin.models.StatusRequest;
import ptit.tvnkhanh.tutor_center_management.services.admin.models.StaffResponse;
import ptit.tvnkhanh.tutor_center_management.services.admin.models.ReasonRequest;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminService {
    @POST("/admin/staff")
    Call<StaffResponse> createAndUpdateStaff(@Header(Constants.X_AUTH_TOKEN) String token, @Body Staff staff);

    @GET("/admin/staff/get")
    Call<Staff> getStaffData(@Header(Constants.X_AUTH_TOKEN) String token);

    @PATCH("/admin/classes/{id}/status")
    Call<TutoringClass> updateClassStatus(@Header(Constants.X_AUTH_TOKEN) String token, @Path("id") String classId,
                                          @Body StatusRequest statusRequest);

    @PATCH("/admin/tutors/{id}/status")
    Call<Tutor> updateTutorStatus(@Header(Constants.X_AUTH_TOKEN) String token, @Path("id") String tutorId, @Body StatusRequest statusRequest);

    @GET("/admin/revenue-statistics")
    Call<List<RevenueResponse>> getRevenueStatistics(@Header(Constants.X_AUTH_TOKEN) String token, @Query("startDate") String startDate, @Query("endDate") String endDate);

    @POST("/api/create-reason")
    Call<ReasonRequest> createReason(@Body ReasonRequest reasonRequest);

    @DELETE("/api/delete-reason")
    Call<Void> deleteReason(@Query("classId") String classId, @Query("tutorId") String tutorId);

    @GET("/api/get-reason")
    Call<List<ReasonRequest>> getReasons(
            @Query("classId") @Nullable String classId,
            @Query("tutorId") @Nullable String tutorId
    );
}
