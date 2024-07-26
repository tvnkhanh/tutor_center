package ptit.tvnkhanh.tutor_center_management.view.auth;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.databinding.ActivityDetailScreenBinding;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import ptit.tvnkhanh.tutor_center_management.view.custom.CustomToolbar;

public class DetailScreenActivity extends AppCompatActivity implements CustomToolbar.OnBackPressListener, View.OnClickListener {
    private ActivityDetailScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailScreenBinding.inflate(getLayoutInflater());
        initUI();
        setContentView(binding.getRoot());
    }

    private void initUI() {
        binding.toolbar.setTitle(getString(R.string.detail_screen_title));
        binding.toolbar.setListener(this);

        String role = getIntent().getStringExtra(Constants.SIGN_UP_ROLE);
        if (role != null && !role.isEmpty()) {
            if (role.equals(Constants.SIGN_UP_ROLE_STUDENT)) {
                binding.setIsTutor(false);
            } else if (role.equals(Constants.SIGN_UP_ROLE_TUTOR)) {
                binding.setIsTutor(true);
            }
        }

        binding.btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onBackPress() {
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSubmit) {
            Utility.showToast(this, "Submit");
        }
    }
}