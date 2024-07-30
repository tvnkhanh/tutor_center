package ptit.tvnkhanh.tutor_center_management.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ptit.tvnkhanh.tutor_center_management.util.Constants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat(dateFormat)
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
