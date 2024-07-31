package ptit.tvnkhanh.tutor_center_management.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.callback.OnNavigationListener;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentHomeScreenBinding;
import ptit.tvnkhanh.tutor_center_management.util.Constants;

public class HomeScreenFragment extends Fragment {

    private FragmentHomeScreenBinding binding;
    private OnNavigationListener navigationListener;
    private final String[] texts = {
            "Hire teacher", "Course list", "Payment", "Support"
    };
    private final int[] iconIds = {
            R.drawable.ic_hire_teacher, R.drawable.ic_course_list,
            R.drawable.ic_payment, R.drawable.ic_customer_service
    };
    private UserSession userSession;

    public BroadcastReceiver onUserDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.ACTION_USER_DATA_UPDATED:
                    updateUI();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeScreenBinding.inflate(getLayoutInflater());
        initUI();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(onUserDataReceiver, new IntentFilter(Constants.ACTION_USER_DATA_UPDATED));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        navigationListener = (OnNavigationListener) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(onUserDataReceiver);
    }

    private void initUI() {
        updateUI();
        GridLayout gridLayout = binding.glOptionsContainer;
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            ImageView icon = gridLayout.getChildAt(i).findViewById(R.id.ivIcon);
            TextView text = gridLayout.getChildAt(i).findViewById(R.id.tvTitle);

            icon.setImageResource(iconIds[i]);
            text.setText(texts[i]);
        }
        binding.hireTeacherOption.homeScreenOptionItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationListener.setOnNavigationDestination(Constants.NAVIGATION_TEACHER_DETAIL);
            }
        });

    }

    private void updateUI() {
        userSession = UserSession.getInstance();
        String clientId = "";
        if (userSession.getAccount() != null) {
            clientId = userSession.getAccount().getClientId();
        }
        String name = "";
        if (userSession.getStaff() != null) {
            name = userSession.getStaff().getFirstName() + " " + userSession.getStaff().getLastName();
        } else if (userSession.getTutor() != null) {
            name = userSession.getTutor().getFirstName() + " " + userSession.getTutor().getLastName();
        }
        binding.tvName.setText(getResources().getString(R.string.home_screen_name, name));
        binding.popularTeacher.setIsClient(clientId != null && !clientId.isEmpty());
    }
}