package ptit.tvnkhanh.tutor_center_management.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.databinding.ActivitySignUpScreenBinding;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.auth.AuthService;
import ptit.tvnkhanh.tutor_center_management.util.ClickSpan;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import ptit.tvnkhanh.tutor_center_management.view.custom.CustomToolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpScreenActivity extends AppCompatActivity implements CustomToolbar.OnBackPressListener,
        ClickSpan.OnClickListener, View.OnClickListener {

    private ActivitySignUpScreenBinding binding;
    private AuthService authService;
    private List<String> userNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpScreenBinding.inflate(LayoutInflater.from(this));
        authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
        getAllUserName();
        initUI();
        setContentView(binding.getRoot());
    }

    private void initUI() {
        binding.toolbar.setTitle(getString(R.string.sign_up_screen_title));
        binding.toolbar.setListener(this);
        ClickSpan.clickify(binding.tvLoginText, getString(R.string.sign_up_screen_login_text_clickable_text), this, ContextCompat.getColor(this, R.color.app_primary_color));
        binding.btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onTextClick() {
        Intent intent = new Intent(this, SignInScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPress() {
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSignUp) {
            if (!binding.edtUsername.getText().toString().isEmpty() && !binding.edtPassword.getText().toString().isEmpty() &&
                    !binding.edtReEnterPassword.getText().toString().isEmpty()) {
                if (binding.edtPassword.getText().toString().equals(binding.edtReEnterPassword.getText().toString())) {
                    if (!userNames.contains(binding.edtUsername.getText().toString())) {
                        Intent intent = new Intent(this, DetailScreenActivity.class);
                        intent.putExtra(Constants.SIGN_UP_USERNAME, binding.edtUsername.getText().toString());
                        intent.putExtra(Constants.SIGN_UP_PASSWORD, binding.edtPassword.getText().toString());
                        String role = getIntent().getStringExtra(Constants.SIGN_UP_ROLE);
                        if (role != null && !role.isEmpty()) {
                            intent.putExtra(Constants.SIGN_UP_ROLE, role);
                        } else {
                            intent.putExtra(Constants.SIGN_UP_ROLE, Constants.SIGN_UP_ROLE_STUDENT);
                        }
                        startActivity(intent);
                    } else {
                        binding.edtUsername.setError(getString(R.string.sign_up_screen_username_error));
                    }
                } else {
                    binding.edtReEnterPassword.setError(getString(R.string.sign_up_screen_re_enter_password_error));
                }
            } else {
                Utility.showToast(this, getString(R.string.login_screen_empty_fields));
            }
        }
    }

    private void getAllUserName() {
        authService.getUsernames().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    userNames = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable throwable) {

            }
        });
    }
}