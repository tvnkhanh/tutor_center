package ptit.tvnkhanh.tutor_center_management.view.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.databinding.ActivitySignInScreenBinding;
import ptit.tvnkhanh.tutor_center_management.util.ClickSpan;
import ptit.tvnkhanh.tutor_center_management.view.custom.CustomToolbar;

public class SignInScreenActivity extends AppCompatActivity implements CustomToolbar.OnBackPressListener, ClickSpan.OnClickListener {

    private ActivitySignInScreenBinding binding;

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
    }

    @Override
    public void onBackPress() {
        finish();
    }

    @Override
    public void onClick() {
        Intent intent = new Intent(this, SignUpScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}