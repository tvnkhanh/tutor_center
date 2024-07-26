package ptit.tvnkhanh.tutor_center_management.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.databinding.ActivitySignUpScreenBinding;
import ptit.tvnkhanh.tutor_center_management.util.ClickSpan;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.view.custom.CustomToolbar;

public class SignUpScreenActivity extends AppCompatActivity implements CustomToolbar.OnBackPressListener,
        ClickSpan.OnClickListener, View.OnClickListener {

    private ActivitySignUpScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpScreenBinding.inflate(LayoutInflater.from(this));
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
    public void onClick() {
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
            Intent intent = new Intent(this, DetailScreenActivity.class);
            String role = getIntent().getStringExtra(Constants.SIGN_UP_ROLE);
            if (role != null && !role.isEmpty()) {
                intent.putExtra(Constants.SIGN_UP_ROLE, role);
            } else {
                intent.putExtra(Constants.SIGN_UP_ROLE, Constants.SIGN_UP_ROLE_STUDENT);
            }
            startActivity(intent);
        }
    }
}