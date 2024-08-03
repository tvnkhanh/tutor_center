package ptit.tvnkhanh.tutor_center_management.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Objects;

import ptit.tvnkhanh.tutor_center_management.AdminMainActivity;
import ptit.tvnkhanh.tutor_center_management.MainActivity;
import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.StartUpScreenActivity;
import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.databinding.ActivitySignInScreenBinding;
import ptit.tvnkhanh.tutor_center_management.models.Account;
import ptit.tvnkhanh.tutor_center_management.models.Client;
import ptit.tvnkhanh.tutor_center_management.models.Staff;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.admin.AdminService;
import ptit.tvnkhanh.tutor_center_management.services.auth.AuthService;
import ptit.tvnkhanh.tutor_center_management.services.auth.models.SignInRequest;
import ptit.tvnkhanh.tutor_center_management.services.common.ClientService;
import ptit.tvnkhanh.tutor_center_management.services.common.TutorService;
import ptit.tvnkhanh.tutor_center_management.util.ClickSpan;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import ptit.tvnkhanh.tutor_center_management.view.custom.CustomToolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInScreenActivity extends AppCompatActivity implements CustomToolbar.OnBackPressListener,
        ClickSpan.OnClickListener, View.OnClickListener {

    private ActivitySignInScreenBinding binding;
    private AuthService authService;
    private AdminService adminService;
    private TutorService tutorService;
    private ClientService clientService;
    private String token;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInScreenBinding.inflate(getLayoutInflater());
        userSession = UserSession.getInstance();
        initUI();
        setContentView(binding.getRoot());
    }

    private void initUI() {
        binding.toolbar.setTitle(getString(R.string.login_screen_title));
        binding.toolbar.setListener(this);
        ClickSpan.clickify(binding.tvSignUpText, getString(R.string.login_screen_sign_up_text_clickable_text), this, ContextCompat.getColor(this, R.color.app_primary_color));
        binding.btnSignIn.setOnClickListener(this);
    }

    @Override
    public void onBackPress() {
        finish();
    }

    @Override
    public void onTextClick() {
        Intent intent = new Intent(this, SignUpScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSignIn) {
            signIn();
        }
    }

    private void signIn() {
        authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
        String username = binding.edtUsername.getText().toString();
        String password = binding.edtPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Utility.showToast(this, getString(R.string.login_screen_empty_fields));
            return;
        }

        SignInRequest signInRequest = new SignInRequest(username, password);
        authService.signIn(signInRequest).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful() && response.body() != null) {
                    token = response.body().getToken();
                    Utility.showToast(SignInScreenActivity.this, getString(R.string.login_screen_sign_in_success));

                    SharedPreferencesUtility.putString(SignInScreenActivity.this, Constants.X_AUTH_TOKEN, token);

                    getUserData();
                } else {
                    Utility.showToast(SignInScreenActivity.this, getString(R.string.login_screen_wrong_info));
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable throwable) {
                Utility.showToast(SignInScreenActivity.this, getString(R.string.login_screen_sign_in_failed, throwable.getMessage()));
            }
        });
    }

    private void goToMainScreen() {
        Intent intent = new Intent(SignInScreenActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void goToAdminScreen() {
        Intent intent = new Intent(SignInScreenActivity.this, AdminMainActivity.class);
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