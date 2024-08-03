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

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.callback.OnNavigationListener;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentAdminHomeScreenBinding;
import ptit.tvnkhanh.tutor_center_management.util.Constants;

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

    private void initUI() {
        userSession = UserSession.getInstance();
        String name = "";
        String imageUrl = "";
        if (userSession.getStaff() != null) {
            name = userSession.getStaff().getFirstName() + " " + userSession.getStaff().getLastName();
            imageUrl = userSession.getStaff().getPortraitPhotos().get(0);
        }
        binding.tvName.setText(getResources().getString(R.string.home_screen_name, name));
        Glide.with(requireContext())
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar))
                .load(imageUrl)
                .into(binding.ivAvatar);
        binding.popularTeacher.setIsClient(false);
        binding.edtSearch.setOnClickListener(view -> {
            navigationListener.setOnNavigationDestination(Constants.NAVIGATION_SEARCH);
        });
    }
}