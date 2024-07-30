package ptit.tvnkhanh.tutor_center_management.service.admin;

import ptit.tvnkhanh.tutor_center_management.models.Staff;
import ptit.tvnkhanh.tutor_center_management.service.admin.models.StaffResponse;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AdminService {
    @POST("/admin/staff")
    Call<StaffResponse> createAndUpdateStaff(@Header(Constants.X_AUTH_TOKEN) String token, @Body Staff staff);

    @GET("/admin/staff/get")
    Call<Staff> getStaffData(@Header(Constants.X_AUTH_TOKEN) String token);

}
