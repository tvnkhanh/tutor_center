package ptit.tvnkhanh.tutor_center_management.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.databinding.ActivityAuthScreenBinding;
import ptit.tvnkhanh.tutor_center_management.util.ClickSpan;
import ptit.tvnkhanh.tutor_center_management.util.Constants;

public class AuthScreenActivity extends AppCompatActivity implements ClickSpan.OnClickListener, View.OnClickListener {

    private ActivityAuthScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthScreenBinding.inflate(LayoutInflater.from(this));
        initUI();
        setContentView(binding.getRoot());
    }

    private void initUI() {
        ClickSpan.clickify(binding.tvLogin, getString(R.string.auth_screen_login_text_clickable_text), this, getColor(R.color.app_primary_color));
        binding.btnStudent.setOnClickListener(this);
        binding.btnTutor.setOnClickListener(this);
    }

    @Override
    public void onClick() {
        Intent intent = new Intent(this, SignInScreenActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnStudent) {
            Intent intent = new Intent(this, SignUpScreenActivity.class);
            intent.putExtra(Constants.SIGN_UP_ROLE, Constants.SIGN_UP_ROLE_STUDENT);
            startActivity(intent);
        } else if (id == R.id.btnTutor) {
            Intent intent = new Intent(this, SignUpScreenActivity.class);
            intent.putExtra(Constants.SIGN_UP_ROLE, Constants.SIGN_UP_ROLE_TUTOR);
            startActivity(intent);
        }
    }
}