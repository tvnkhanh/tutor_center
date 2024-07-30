package ptit.tvnkhanh.tutor_center_management;

import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_CHAT;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_CLASSES;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_COURSES;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_HOME;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_TEACHER_DETAIL;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_WISHLIST;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import ptit.tvnkhanh.tutor_center_management.callback.OnNavigationListener;
import ptit.tvnkhanh.tutor_center_management.databinding.ActivityMainBinding;
import ptit.tvnkhanh.tutor_center_management.databinding.DialogExitAppConfirmBinding;
import ptit.tvnkhanh.tutor_center_management.models.Account;
import ptit.tvnkhanh.tutor_center_management.models.Staff;
import ptit.tvnkhanh.tutor_center_management.service.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.service.admin.AdminService;
import ptit.tvnkhanh.tutor_center_management.service.auth.AuthService;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import ptit.tvnkhanh.tutor_center_management.view.custom.CustomToolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnNavigationListener, NavigationBarView.OnItemSelectedListener,
        CustomToolbar.OnBackPressListener {

    private ActivityMainBinding binding;
    private AuthService authService;
    private AdminService adminService;
    private String token;
    private UserSession userSession;
    private DialogExitAppConfirmBinding dialogExitAppConfirmBinding;
    private BottomNavigationView bottomNavigationView;
    private NavHostFragment navHostFragment;
    private NavController navController;

    private Dialog appExitConfirmationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));

        userSession = UserSession.getInstance();
        token = SharedPreferencesUtility.getString(this, Constants.X_AUTH_TOKEN, "");
        bottomNavigationView = binding.bottomNavigationView;
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setVisibility(View.GONE);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        navController = navHostFragment.getNavController();

        bottomNavigationView.setOnItemSelectedListener(this);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() == R.id.homeScreenFragment) {
                    showAppExitConfirmationDialog();
                } else {
                    navController.popBackStack();
                    setSelectedItemBottomBar();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);

        getUserData();

        setContentView(binding.getRoot());
    }

    @Override
    protected void onResume() {
        hideAppExitConfirmationDialog();
        binding.toolbar.setVisibility(View.GONE);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        hideAppExitConfirmationDialog();
        super.onDestroy();
    }

    @Override
    public void setOnNavigationDestination(String destination) {
        switch (destination) {
            case NAVIGATION_HOME:
                navController.navigate(R.id.homeScreenFragment);
                break;
            case NAVIGATION_CLASSES:
                navController.navigate(R.id.classScreenFragment);
                break;
            case NAVIGATION_WISHLIST:
                navController.navigate(R.id.wishlistScreenFragment);
                break;
            case NAVIGATION_COURSES:
                navController.navigate(R.id.courseScreenFragment);
                break;
            case NAVIGATION_CHAT:
                navController.navigate(R.id.chatScreenFragment);
                break;
            case NAVIGATION_TEACHER_DETAIL:
                navController.navigate(R.id.teacherScreenFragment);
                setShowToolbar();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.navigation_home) {
            setOnNavigationDestination(NAVIGATION_HOME);
        } else if (id == R.id.navigation_classes) {
            setOnNavigationDestination(NAVIGATION_CLASSES);
        } else if (id == R.id.navigation_wishlist) {
            setOnNavigationDestination(NAVIGATION_WISHLIST);
        } else if (id == R.id.navigation_courses) {
            setOnNavigationDestination(NAVIGATION_COURSES);
        } else if (id == R.id.navigation_chat) {
            setOnNavigationDestination(NAVIGATION_CHAT);
        }
        return true;
    }

    private void showAppExitConfirmationDialog() {
        appExitConfirmationDialog = new Dialog(this, R.style.TransparentDialog);

        dialogExitAppConfirmBinding = DialogExitAppConfirmBinding.inflate(LayoutInflater.from(this));
        appExitConfirmationDialog.setContentView(dialogExitAppConfirmBinding.getRoot());

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(appExitConfirmationDialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);

        dialogExitAppConfirmBinding.buttonNo.setOnClickListener(view -> {
            appExitConfirmationDialog.cancel();
            appExitConfirmationDialog.dismiss();
        });

        dialogExitAppConfirmBinding.buttonYes.setOnClickListener(view -> {
            finishAffinity();
            System.exit(0);
        });

        appExitConfirmationDialog.show();
        appExitConfirmationDialog.getWindow().setAttributes(layoutParams);
    }

    private void hideAppExitConfirmationDialog() {
        if (appExitConfirmationDialog != null) {
            if (appExitConfirmationDialog.isShowing()) {
                appExitConfirmationDialog.cancel();
                appExitConfirmationDialog.dismiss();
            }
            appExitConfirmationDialog = null;
        }
    }

    private void setSelectedItemBottomBar() {
        if (navController.getCurrentDestination() != null) {
            int id = navController.getCurrentDestination().getId();
            if (id == R.id.homeScreenFragment) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            } else if (id == R.id.classScreenFragment) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_classes);
            } else if (id == R.id.wishlistScreenFragment) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_wishlist);
            } else if (id == R.id.courseScreenFragment) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_courses);
            } else if (id == R.id.chatScreenFragment) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_chat);
            }
        }
    }

    private void setShowToolbar() {
        binding.toolbar.setVisibility(View.VISIBLE);
        binding.bottomNavigationView.setVisibility(View.GONE);

        if (navController.getCurrentDestination() != null) {
            int id = navController.getCurrentDestination().getId();
            binding.toolbar.setListener(this);
            if (id == R.id.teacherScreenFragment) {
                binding.toolbar.setTitle(getString(R.string.teacher_detail_screen_title));
            }
        }
    }

    private void setHideToolbar() {
        binding.toolbar.setVisibility(View.GONE);
        binding.bottomNavigationView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPress() {
        navController.popBackStack();
        setHideToolbar();
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
                        if (account.getStaffId() != null && !account.getStaffId().isEmpty()) {
                            getStaffData();
                        }
                        if (account.getClientId() != null && !account.getClientId().isEmpty()) {
                            //TODO: get client data
                            getClientData();
                        }
                        if (account.getTutorId() != null && !account.getTutorId().isEmpty()) {
                            //TODO: get tutor data
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
                            Intent intent = new Intent(Constants.ACTION_USER_DATA_UPDATED);
                            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
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

    }

    private void getClientData() {

    }
}