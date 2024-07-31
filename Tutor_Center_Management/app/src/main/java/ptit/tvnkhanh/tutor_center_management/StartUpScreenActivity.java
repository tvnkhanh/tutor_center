package ptit.tvnkhanh.tutor_center_management;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;

import ptit.tvnkhanh.tutor_center_management.databinding.ActivityStartUpScreenBinding;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.auth.AuthService;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import ptit.tvnkhanh.tutor_center_management.view.auth.AuthScreenActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartUpScreenActivity extends AppCompatActivity {

    private ActivityStartUpScreenBinding binding;
    private Handler handler = new Handler();
    private AuthService authService;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            checkValidToken();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartUpScreenBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        handler.postDelayed(runnable, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void checkValidToken() {
        authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
        String token = SharedPreferencesUtility.getString(this, Constants.X_AUTH_TOKEN, "");

        if (token != null && !token.isEmpty()) {
            authService.tokenIsValid(token).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        Boolean isValid = response.body();
                        if (isValid != null && isValid) {
                            Intent intent = new Intent(StartUpScreenActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            goToAuthScreen();
                        }
                    } else {
                        Log.d("StartUpScreenActivity", "onResponse: isValid " + response.body());
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable throwable) {
                    Log.d("StartUpScreenActivity", "onFailure: " + throwable.getMessage());
                }
            });
        } else {
            goToAuthScreen();
        }
    }

    private void goToAuthScreen() {
        Intent intent = new Intent(StartUpScreenActivity.this, AuthScreenActivity.class);
        startActivity(intent);
        finish();
    }
}