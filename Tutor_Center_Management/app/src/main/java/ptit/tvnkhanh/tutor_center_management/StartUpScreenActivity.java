package ptit.tvnkhanh.tutor_center_management;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Objects;

import ptit.tvnkhanh.tutor_center_management.databinding.ActivityStartUpScreenBinding;
import ptit.tvnkhanh.tutor_center_management.models.Account;
import ptit.tvnkhanh.tutor_center_management.models.Client;
import ptit.tvnkhanh.tutor_center_management.models.Staff;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.admin.AdminService;
import ptit.tvnkhanh.tutor_center_management.services.auth.AuthService;
import ptit.tvnkhanh.tutor_center_management.services.common.ClientService;
import ptit.tvnkhanh.tutor_center_management.services.common.TutorService;
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
    private ClientService clientService;
    private TutorService tutorService;
    private AdminService adminService;
    private String token;
    private UserSession userSession;

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
        userSession = UserSession.getInstance();
        token = SharedPreferencesUtility.getString(this, Constants.X_AUTH_TOKEN, "");
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

        if (token != null && !token.isEmpty()) {
            authService.tokenIsValid(token).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        Boolean isValid = response.body();
                        if (isValid != null && isValid) {
                            getUserData();
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

    private void goToMainScreen() {
        Intent intent = new Intent(StartUpScreenActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void goToAdminScreen() {
        Intent intent = new Intent(StartUpScreenActivity.this, AdminMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void getUserData() {
        authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
        Log.d("getUserData", "token: " + token);
        if (token != null && !token.isEmpty()) {
            authService.getAccount(token).enqueue(new Callback<Account>() {
                @Override
                public void onResponse(Call<Account> call, Response<Account> response) {
                    Account account = response.body();
                    if (account != null) {
                        userSession.setAccount(account);
                        String roleId = userSession.getAccount().getRoleId();
                        if (Objects.equals(roleId, Constants.ROLE_ADMIN_ID)) {
                            getStaffData();
                        } else if (Objects.equals(roleId, Constants.ROLE_CLIENT_ID)) {
                            getClientData();
                        } else if (Objects.equals(roleId, Constants.ROLE_TUTOR_ID)) {
                            getTutorData();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Account> call, Throwable throwable) {
                    Log.d("getUserData", "onFailure: " + throwable.getMessage());
                }
            });
        }
    }

    private void getStaffData() {
        adminService = RetrofitClient.getRetrofitInstance().create(AdminService.class);
        Log.d("getStaffData", "token: " + token);
        if (token != null && !token.isEmpty()) {
            adminService.getStaffData(token).enqueue(new Callback<Staff>() {
                @Override
                public void onResponse(Call<Staff> call, Response<Staff> response) {
                    if (response.isSuccessful()) {
                        Staff staff = response.body();
                        if (staff != null) {
                            userSession.setStaff(staff);
                            goToAdminScreen();
                            Log.d("getStaffData", "staff: " + staff.toString());
                        } else {
                            Log.d("getStaffData", "staff is null");
                        }
                    } else {
                        Log.d("getStaffData", "onResponse: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Staff> call, Throwable throwable) {
                    Log.d("getStaffData", "onFailure: " + throwable.getMessage());
                }
            });
        }
    }

    private void getTutorData() {
        tutorService = RetrofitClient.getRetrofitInstance().create(TutorService.class);
        if (token != null && !token.isEmpty()) {
            tutorService.getTutor(token).enqueue(new Callback<Tutor>() {
                @Override
                public void onResponse(Call<Tutor> call, Response<Tutor> response) {
                    if (response.isSuccessful()) {
                        Tutor tutor = response.body();
                        if (tutor != null) {
                            userSession.setTutor(tutor);
                            goToMainScreen();
                            Log.d("getTutorData", "tutor: " + tutor.toString());
                        } else {
                            Log.d("getTutorData", "tutor is null");
                        }
                    } else {
                        Log.d("getTutorData", "onResponse: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Tutor> call, Throwable throwable) {
                    Log.d("getTutorData", "onFailure: " + throwable.getMessage());
                }
            });
        }
    }

    private void getClientData() {
        clientService = RetrofitClient.getRetrofitInstance().create(ClientService.class);
        clientService.getClient(token).enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, Response<Client> response) {
                if (response.isSuccessful()) {
                    Client client = response.body();
                    if (client != null) {
                        userSession.setClient(client);
                        goToMainScreen();
                        Log.d("getClientData", "client: " + client.toString());
                    } else {
                        Log.d("getClientData", "client is null");
                    }
                } else {
                    Log.d("getClientData", "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Client> call, Throwable throwable) {
                Log.d("getClientData", "onFailure: " + throwable.getMessage());
            }
        });
    }
}