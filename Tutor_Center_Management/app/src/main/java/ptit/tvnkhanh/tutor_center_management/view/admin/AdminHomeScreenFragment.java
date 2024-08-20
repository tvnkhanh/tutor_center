package ptit.tvnkhanh.tutor_center_management.view.admin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

import ptit.tvnkhanh.tutor_center_management.AdminMainActivity;
import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.callback.OnNavigationListener;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentAdminHomeScreenBinding;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.Utility;

public class AdminHomeScreenFragment extends Fragment {

    private FragmentAdminHomeScreenBinding binding;
    private OnNavigationListener navigationListener;
    private UserSession userSession;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminHomeScreenBinding.inflate(getLayoutInflater());
        initUI();
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        navigationListener = (OnNavigationListener) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AdminMainActivity) requireActivity()).setHideToolbar();
    }

    private void initUI() {
        userSession = UserSession.getInstance();
        String name = "";
        String imageUrl = "";
        if (userSession.getStaff() != null) {
            name = userSession.getStaff().getFirstName() + " " + userSession.getStaff().getLastName();
            imageUrl = userSession.getStaff().getPortraitPhotos().get(0);
        }
        // Hard code for UI preview
        binding.tvName.setText(getResources().getString(R.string.home_screen_name, name));
        Glide.with(requireContext())
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar))
                .load(imageUrl)
                .into(binding.ivAvatar);
        binding.popularTeacher.setIsClient(false);
        binding.classes.tvClassId.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_class_id, "66aa484325841255f632cer")));
        binding.classes.tvStatus.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_status, Constants.CLASS_STATUS_APPROVED)));
        binding.classes.tvStudentInfo.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_student_info, "Học sinh lớp 8")));
        binding.classes.tvSubject.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_subject, "[Physics]")));
        binding.classes.tvSchedule.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_schedule, "Sáng thứ 4")));
        binding.classes.tvForm.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_form, Constants.CLASS_FORM_ONLINE)));
        binding.classes.tvAddress.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_address, "201 Than Nhan Trung, Tan Binh, HCM")));
        binding.classes.tvSalary.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_salary, Utility.formatNumber(3200000))));
        binding.classes.tvRequirement.setText(Utility.boldText(requireActivity().getString(R.string.classes_screen_requirement, "Giáo viên nam")));
        binding.classes.tvTutor.setVisibility(View.GONE);
        binding.classes.btnChangeStatus.setVisibility(View.GONE);

        binding.edtSearch.setOnClickListener(view -> {
            navigationListener.setOnNavigationDestination(Constants.NAVIGATION_SEARCH);
            ((AdminMainActivity) requireActivity()).setSelectedItemBottomBar();
        });
        binding.tvSeeAllTeachers.setOnClickListener(view -> {
            navigationListener.setOnNavigationDestination(Constants.NAVIGATION_TUTORS);
            ((AdminMainActivity) requireActivity()).setSelectedItemBottomBar();
        });
        binding.tvSeeAllClasses.setOnClickListener(view -> {
            navigationListener.setOnNavigationDestination(Constants.NAVIGATION_CLASSES);
            ((AdminMainActivity) requireActivity()).setSelectedItemBottomBar();
        });
    }
}