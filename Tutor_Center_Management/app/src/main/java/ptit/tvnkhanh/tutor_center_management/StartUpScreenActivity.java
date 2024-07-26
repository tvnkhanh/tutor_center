package ptit.tvnkhanh.tutor_center_management;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;

import ptit.tvnkhanh.tutor_center_management.databinding.ActivityStartUpScreenBinding;
import ptit.tvnkhanh.tutor_center_management.view.auth.AuthScreenActivity;

public class StartUpScreenActivity extends AppCompatActivity {

    private ActivityStartUpScreenBinding binding;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(StartUpScreenActivity.this, AuthScreenActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartUpScreenBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        handler.postDelayed(runnable, 2000);
    }
}