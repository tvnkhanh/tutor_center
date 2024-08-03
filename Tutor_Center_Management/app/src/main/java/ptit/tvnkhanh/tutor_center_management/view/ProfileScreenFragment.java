package ptit.tvnkhanh.tutor_center_management.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentProfileScreenBinding;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import ptit.tvnkhanh.tutor_center_management.view.auth.AuthScreenActivity;

public class ProfileScreenFragment extends Fragment {

    private FragmentProfileScreenBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileScreenBinding.inflate(getLayoutInflater());
        initUI();
        setEvent();

        return binding.getRoot();
    }

    private void initUI() {
        String roleId = UserSession.getInstance().getAccount().getRoleId();
        String imageUrl = "";
        String name = "";
        String email = "";
        String address = "";
        String id = "";
        if (Objects.equals(roleId, Constants.ROLE_TUTOR_ID)) {
            imageUrl = UserSession.getInstance().getTutor().getPortraitPhotos().get(0);
            name = UserSession.getInstance().getTutor().getFirstName() + " " + UserSession.getInstance().getTutor().getLastName();
            email = UserSession.getInstance().getTutor().getEmail();
            address = UserSession.getInstance().getTutor().getAddress();
            id = UserSession.getInstance().getTutor().get_id();
        } else if (Objects.equals(roleId, Constants.ROLE_ADMIN_ID)) {
            imageUrl = UserSession.getInstance().getStaff().getPortraitPhotos().get(0);
            name = UserSession.getInstance().getStaff().getFirstName() + " " + UserSession.getInstance().getStaff().getLastName();
            email = UserSession.getInstance().getStaff().getEmail();
            address = UserSession.getInstance().getStaff().getAddress();
            id = UserSession.getInstance().getStaff().getId();
        } else if (Objects.equals(roleId, Constants.ROLE_CLIENT_ID)) {
            name = UserSession.getInstance().getClient().getFirstName() + " " + UserSession.getInstance().getClient().getLastName();
            email = UserSession.getInstance().getClient().getEmail();
            id = UserSession.getInstance().getClient().get_id();
        }
        Glide.with(requireActivity()).applyDefaultRequestOptions(new RequestOptions()
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar))
                .load(imageUrl)
                .into(binding.ivAvatar);
        binding.tvName.setText(name);
        binding.tvEmail.setText(email);
        binding.tvAddress.setText(address);
        binding.tvId.setText(id);

        binding.optionEditProfile.optionIcon.setImageResource(R.drawable.ic_edit);
        binding.optionEditProfile.optionText.setText(getString(R.string.profile_screen_item_title_1));
        binding.optionTeachersList.optionIcon.setImageResource(R.drawable.ic_list);
        binding.optionTeachersList.optionText.setText(getString(R.string.profile_screen_item_title_2));
        binding.optionHelp.optionIcon.setImageResource(R.drawable.ic_help);
        binding.optionHelp.optionText.setText(getString(R.string.profile_screen_item_title_3));
        binding.optionReferral.optionIcon.setImageResource(R.drawable.ic_referral);
        binding.optionReferral.optionText.setText(getString(R.string.profile_screen_item_title_4));
        binding.optionLogout.optionIcon.setImageResource(R.drawable.ic_logout);
        binding.optionLogout.optionText.setText(getString(R.string.profile_screen_item_title_5));
    }

    private void setEvent() {
        binding.optionLogout.llContainer.setOnClickListener(view -> {
            SharedPreferencesUtility.putString(requireActivity(), Constants.X_AUTH_TOKEN, "");
            UserSession.getInstance().clear();
            Intent intent = new Intent(requireActivity(), AuthScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }
}