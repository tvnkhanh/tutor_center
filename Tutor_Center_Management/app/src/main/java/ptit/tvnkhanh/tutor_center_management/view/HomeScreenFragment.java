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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

import ptit.tvnkhanh.tutor_center_management.MainActivity;
import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.callback.OnNavigationListener;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentHomeScreenBinding;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.Utility;

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
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setHideToolbar();
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
        binding.hireTeacherOption.homeScreenOptionItem.setOnClickListener(view ->
                navigationListener.setOnNavigationDestination(Constants.NAVIGATION_HIRE_TUTOR_DETAIL)
        );
        binding.paymentOption.homeScreenOptionItem.setOnClickListener(view -> {
            navigationListener.setOnNavigationDestination(Constants.NAVIGATION_PAYMENT);
        });
        binding.edtSearch.setOnClickListener(view -> {
            navigationListener.setOnNavigationDestination(Constants.NAVIGATION_SEARCH);
        });
    }

    private void updateUI() {
        userSession = UserSession.getInstance();
        String clientId = "";
        if (userSession.getAccount() != null) {
            binding.progressBar.setVisibility(View.GONE);
            clientId = userSession.getAccount().getClientId();
        }
        String name = "";
        String imageUrl = "";
        if (userSession.getStaff() != null) {
            name = userSession.getStaff().getFirstName() + " " + userSession.getStaff().getLastName();
            imageUrl = userSession.getStaff().getPortraitPhotos().get(0);
        } else if (userSession.getTutor() != null) {
            name = userSession.getTutor().getFirstName() + " " + userSession.getTutor().getLastName();
            imageUrl = userSession.getTutor().getPortraitPhotos().get(0);
        } else if (userSession.getClient() != null) {
            name = userSession.getClient().getFirstName() + " " + userSession.getClient().getLastName();
//            imageUrl = userSession.getClient().getPortraitPhotos().get(0);
        }
        binding.tvName.setText(getResources().getString(R.string.home_screen_name, name));
        Glide.with(requireContext())
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar))
                .load(imageUrl)
                .into(binding.ivAvatar);
        binding.popularTeacher.setIsClient(clientId != null && !clientId.isEmpty());

        // Hard code for UI screenshots
        binding.classes.setIsTutor(Objects.equals(userSession.getAccount().getRoleId(), Constants.ROLE_TUTOR_ID));
        binding.classes.setIsNotActive(true);
        binding.classes.tvClassId.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_class_id, "66aa4843258475752f632c4a")));
        binding.classes.tvStatus.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_status, Constants.CLASS_STATUS_APPROVED)));
        binding.classes.tvStudentInfo.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_student_info, "Học sinh lớp 8")));
        binding.classes.tvSubject.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_subject, "[Physics]")));
        binding.classes.tvSchedule.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_schedule, "Sáng thứ 4")));
        binding.classes.tvForm.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_form, Constants.CLASS_FORM_ONLINE)));
        binding.classes.tvAddress.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_address, "201 Than Nhan Trung, Tan Binh, HCM")));
        binding.classes.tvSalary.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_salary, Utility.formatNumber(3200000))));

        binding.tvSeeAllTeachers.setOnClickListener(view -> {
            navigationListener.setOnNavigationDestination(Constants.NAVIGATION_TUTORS);
            ((MainActivity) requireActivity()).setSelectedItemBottomBar();
        });
        binding.tvSeeAllClasses.setOnClickListener(view -> {
            navigationListener.setOnNavigationDestination(Constants.NAVIGATION_CLASSES);
            ((MainActivity) requireActivity()).setSelectedItemBottomBar();
        });
    }
}