package ptit.tvnkhanh.tutor_center_management;

import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_EDIT_PROFILE;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_PAYMENT;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_PROFILE;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_CLASSES;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_COURSES;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_HOME;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_HIRE_TUTOR_DETAIL;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_SEARCH;
import static ptit.tvnkhanh.tutor_center_management.util.Constants.NAVIGATION_TUTORS;

import android.app.Dialog;
import android.os.Bundle;
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
import ptit.tvnkhanh.tutor_center_management.databinding.ActivityMainBinding;
import ptit.tvnkhanh.tutor_center_management.databinding.DialogExitAppConfirmBinding;
import ptit.tvnkhanh.tutor_center_management.models.Subject;
import ptit.tvnkhanh.tutor_center_management.services.admin.AdminService;
import ptit.tvnkhanh.tutor_center_management.services.auth.AuthService;
import ptit.tvnkhanh.tutor_center_management.services.common.ClientService;
import ptit.tvnkhanh.tutor_center_management.services.common.TutorService;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import ptit.tvnkhanh.tutor_center_management.view.custom.CustomToolbar;

public class MainActivity extends AppCompatActivity implements OnNavigationListener, NavigationBarView.OnItemSelectedListener,
        CustomToolbar.OnBackPressListener {

    private ActivityMainBinding binding;
    private AuthService authService;
    private AdminService adminService;
    private TutorService tutorService;
    private ClientService clientService;
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

//        getUserData();

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

    @Override
    protected void onResume() {
        hideAppExitConfirmationDialog();
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
            case NAVIGATION_TUTORS:
                navController.navigate(R.id.wishlistScreenFragment);
                break;
            case NAVIGATION_COURSES:
                navController.navigate(R.id.courseScreenFragment);
                break;
            case NAVIGATION_PROFILE:
                navController.navigate(R.id.chatScreenFragment);
                break;
            case NAVIGATION_HIRE_TUTOR_DETAIL:
                navController.navigate(R.id.teacherScreenFragment);
                setShowToolbar();
                break;
            case NAVIGATION_PAYMENT:
                navController.navigate(R.id.paymentScreenFragment);
                setShowToolbar();
                break;
            case NAVIGATION_SEARCH:
                navController.navigate(R.id.searchScreenFragment2);
                setShowToolbar();
                break;
            case NAVIGATION_EDIT_PROFILE:
                navController.navigate(R.id.editProfile);
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
            setOnNavigationDestination(NAVIGATION_TUTORS);
        } else if (id == R.id.navigation_courses) {
            setOnNavigationDestination(NAVIGATION_COURSES);
        } else if (id == R.id.navigation_chat) {
            setOnNavigationDestination(NAVIGATION_PROFILE);
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

    public void setSelectedItemBottomBar() {
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
                binding.toolbar.setTitle(getString(R.string.hire_tutor_screen_title));
            }
            if (id == R.id.paymentScreenFragment) {
                binding.toolbar.setTitle(getString(R.string.payment_screen_title));
            }
            if (id == R.id.searchScreenFragment2) {
                binding.toolbar.setTitle(getString(R.string.search_screen_title));
            }
            if (id == R.id.editProfile) {
//                binding.toolbar.setTitle(getString(R.string.profile_screen_title));
                binding.toolbar.setTitle("Tutor Information");
            }
        }
    }

    public void setHideToolbar() {
        binding.toolbar.setVisibility(View.GONE);
        binding.bottomNavigationView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPress() {
        int beforeId = 0;
        int afterId = 0;
        if (navController.getCurrentDestination() != null) {
            beforeId = navController.getCurrentDestination().getId();
        }
        navController.popBackStack();
        if (navController.getCurrentDestination() != null) {
            afterId = navController.getCurrentDestination().getId();
        }
        if (!(beforeId == R.id.teacherScreenFragment && afterId == R.id.searchScreenFragment2)) {
            setHideToolbar();
        } else {
            setShowToolbar();
        }
    }
}