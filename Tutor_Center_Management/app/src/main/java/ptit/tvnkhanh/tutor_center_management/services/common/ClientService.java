package ptit.tvnkhanh.tutor_center_management.services.common;

import ptit.tvnkhanh.tutor_center_management.models.Client;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ClientService {
    @GET("/api/client/get")
    Call<Client> getClient(@Header(Constants.X_AUTH_TOKEN) String token);

    @GET("/api/get-client")
    Call<Client> getClientById(@Header(Constants.X_AUTH_TOKEN) String token, @Query("clientId") String id);

    @POST("/api/client")
    Call<Client> createClient(@Body Client client);

    @GET("/api/client-id")
    Call<String> getClientId(@Query("contactNumber") String contactNumber);
}
