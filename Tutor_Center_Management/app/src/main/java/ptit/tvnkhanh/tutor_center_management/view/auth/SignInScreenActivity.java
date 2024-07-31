package ptit.tvnkhanh.tutor_center_management.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import ptit.tvnkhanh.tutor_center_management.MainActivity;
import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.databinding.ActivitySignInScreenBinding;
import ptit.tvnkhanh.tutor_center_management.models.Account;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.auth.AuthService;
import ptit.tvnkhanh.tutor_center_management.services.auth.models.SignInRequest;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInScreenBinding.inflate(getLayoutInflater());
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
                    String token = response.body().getToken();
                    Utility.showToast(SignInScreenActivity.this, getString(R.string.login_screen_sign_in_success));

                    SharedPreferencesUtility.putString(SignInScreenActivity.this, Constants.X_AUTH_TOKEN, token);

                    Intent intent = new Intent(SignInScreenActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
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
}