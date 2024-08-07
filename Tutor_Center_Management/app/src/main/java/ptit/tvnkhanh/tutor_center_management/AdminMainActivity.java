package ptit.tvnkhanh.tutor_center_management;

import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_HIRE_TUTOR_DETAIL;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_PROFILE;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_CLASSES;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_COURSES;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_HOME;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_SEARCH;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_TEACHER_DETAIL;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_TUTORS;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

import ptit.tvnkhanh.tutor_center_management.callback.OnNavigationListener;
import ptit.tvnkhanh.tutor_center_management.databinding.ActivityAdminMainBinding;
import ptit.tvnkhanh.tutor_center_management.databinding.DialogExitAppConfirmBinding;
import ptit.tvnkhanh.tutor_center_management.models.Subject;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import ptit.tvnkhanh.tutor_center_management.view.custom.CustomToolbar;

public class AdminMainActivity extends AppCompatActivity implements OnNavigationListener, NavigationBarView.OnItemSelectedListener,
        CustomToolbar.OnBackPressListener {

    private ActivityAdminMainBinding binding;
    private DialogExitAppConfirmBinding dialogExitAppConfirmBinding;
    private BottomNavigationView bottomNavigationView;
    private NavHostFragment navHostFragment;
    private NavController navController;

    private Dialog appExitConfirmationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        bottomNavigationView = binding.bottomNavigationView;
        bottomNavigationView.setSelectedItemId(R.id.admin_navigation_home);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.adminNavHostFragment);
        navController = navHostFragment.getNavController();

        bottomNavigationView.setOnItemSelectedListener(this);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() == R.id.adminHomeScreenFragment) {
                    showAppExitConfirmationDialog();
                } else {
                    navController.popBackStack();
                    setSelectedItemBottomBar();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);

        Utility.fetchAllReasons();
        Utility.loadAllSubjects(new Utility.SubjectCallback() {
            @Override
            public void onSuccess(List<Subject> subjects) {

            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });

        setContentView(binding.getRoot());
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
            if (id == R.id.adminHomeScreenFragment) {
                bottomNavigationView.setSelectedItemId(R.id.admin_navigation_home);
            } else if (id == R.id.adminClassesFragment) {
                bottomNavigationView.setSelectedItemId(R.id.admin_navigation_classes);
            } else if (id == R.id.adminTutorsFragment) {
                bottomNavigationView.setSelectedItemId(R.id.admin_navigation_tutors);
            } else if (id == R.id.adminRevenueFragment) {
                bottomNavigationView.setSelectedItemId(R.id.admin_navigation_revenue);
            } else if (id == R.id.adminProfileScreenFragment) {
                bottomNavigationView.setSelectedItemId(R.id.admin_navigation_profile);
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
                binding.toolbar.setTitle(getString(R.string.hire_tutor_screen_title));
            }
            if (id == R.id.searchScreenFragment) {
                binding.toolbar.setTitle(getString(R.string.search_screen_title));
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

    @Override
    protected void onResume() {
        super.onResume();
        hideAppExitConfirmationDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideAppExitConfirmationDialog();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.admin_navigation_home) {
            setOnNavigationDestination(NAVIGATION_HOME);
        } else if (id == R.id.admin_navigation_classes) {
            setOnNavigationDestination(NAVIGATION_CLASSES);
        } else if (id == R.id.admin_navigation_tutors) {
            setOnNavigationDestination(NAVIGATION_TUTORS);
        } else if (id == R.id.admin_navigation_revenue) {
            setOnNavigationDestination(NAVIGATION_COURSES);
        } else if (id == R.id.admin_navigation_profile) {
            setOnNavigationDestination(NAVIGATION_PROFILE);
        }
        return true;
    }

    @Override
    public void setOnNavigationDestination(String destination) {
        switch (destination) {
            case NAVIGATION_HOME:
                navController.navigate(R.id.adminHomeScreenFragment);
                break;
            case NAVIGATION_CLASSES:
                navController.navigate(R.id.adminClassesFragment);
                break;
            case NAVIGATION_TUTORS:
                navController.navigate(R.id.adminTutorsFragment);
                break;
            case NAVIGATION_COURSES:
                navController.navigate(R.id.adminRevenueFragment);
                break;
            case NAVIGATION_PROFILE:
                navController.navigate(R.id.adminProfileScreenFragment);
                break;
            case NAVIGATION_HIRE_TUTOR_DETAIL:
                navController.navigate(R.id.teacherScreenFragment);
                setShowToolbar();
                break;
            case NAVIGATION_SEARCH:
                navController.navigate(R.id.searchScreenFragment);
                setShowToolbar();
                break;
            default:
                break;
        }
    }
}