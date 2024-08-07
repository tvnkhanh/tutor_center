package ptit.tvnkhanh.tutor_center_management.services.auth;

import java.util.List;

import ptit.tvnkhanh.tutor_center_management.models.Account;
import ptit.tvnkhanh.tutor_center_management.services.auth.models.SignInRequest;
import ptit.tvnkhanh.tutor_center_management.services.auth.models.SignUpRequest;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthService {
    @GET("/")
    Call<Account> getAccount(@Header(Constants.X_AUTH_TOKEN) String token);

    @POST("/tokenIsValid")
    Call<Boolean> tokenIsValid(@Header(Constants.X_AUTH_TOKEN) String token);

    @POST("/api/signin")
    Call<Account> signIn(@Body SignInRequest signInRequest);

    @POST("/api/signup")
    Call<Account> signUp(@Body SignUpRequest signUpRequest);

    @GET("/api/get-usernames")
    Call<List<String>> getUsernames();
}
