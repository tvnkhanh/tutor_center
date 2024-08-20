package ptit.tvnkhanh.tutor_center_management.view;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.adapter.ItemAdapter;
import ptit.tvnkhanh.tutor_center_management.callback.OnNavigationListener;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentEditProfileBinding;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.admin.AdminService;
import ptit.tvnkhanh.tutor_center_management.services.cloudinary.ImagePickerActivity;
import ptit.tvnkhanh.tutor_center_management.services.cloudinary.UploadImageTask;
import ptit.tvnkhanh.tutor_center_management.services.common.TutorService;
import ptit.tvnkhanh.tutor_center_management.services.common.models.TutorRequest;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.PermissionUtils;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import ptit.tvnkhanh.tutor_center_management.view.auth.DetailScreenActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends Fragment {

    private FragmentEditProfileBinding binding;
    private UserSession userSession;
    private String roleId;
    private String selectedGender;
    private TutorRequest tutorRequest = new TutorRequest();
    private TutorService tutorService;
    private AdminService adminService;
    private NavController navController;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private boolean isPortrait = false;
    private boolean isQualification = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        userSession = UserSession.getInstance();
        roleId = userSession.getAccount().getRoleId();
        tutorService = RetrofitClient.getRetrofitInstance().create(TutorService.class);
        adminService = RetrofitClient.getRetrofitInstance().create(AdminService.class);
        navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
        initUI();
        setEvent();
        return binding.getRoot();
    }

    private void initUI() {
        if (Constants.ROLE_TUTOR_ID.equals(roleId)) {
            binding.setIsTutor(true);
            binding.setTutor(userSession.getTutor());
            loadImage(userSession.getTutor().getPortraitPhotos().get(0), binding.ivPortraitPhoto);
            loadImage(userSession.getTutor().getQualification().get(0), binding.ivQualification);
        }
        if (!PermissionUtils.hasPermissions(requireActivity())) {
            PermissionUtils.requestPermissions(requireActivity());
        } else {
            setupImagePickerLauncher();
        }
    }

    private void setEvent() {
        binding.edtGenderTutor.setOnClickListener(view -> showGenderSelectionDialog());
        binding.edtPortraitPhoto.setOnClickListener(view -> pickImage(true));
        binding.edtQualification.setOnClickListener(view -> pickImage(false));
        binding.btnSubmit.setOnClickListener(view -> validateAndUpdateInformation());
    }

    private void showGenderSelectionDialog() {
        Utility.showItemDialog(requireActivity(), Constants.SIGN_UP_GENDER_LIST, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Utility.hideKeyboard(requireActivity());
                Utility.clearEditTextFocus(requireActivity());
                selectedGender = Constants.SIGN_UP_GENDER_LIST.get(position);
                binding.edtGenderTutor.setText(selectedGender);
            }
        }, "gender", Constants.SIGN_UP_GENDER_LIST.indexOf(selectedGender));
    }

    private void pickImage(boolean isPortrait) {
        this.isPortrait = isPortrait;
        binding.progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(requireContext(), ImagePickerActivity.class);
        if (!PermissionUtils.hasPermissions(requireActivity())) {
            PermissionUtils.requestPermissions(requireActivity());
        } else {
            imagePickerLauncher.launch(intent);
        }
    }

    private void validateAndUpdateInformation() {
        if (areFieldsValid()) {
            setTutorRequestData();
            updateInformation();
        } else {
            Utility.showToast(requireContext(), getString(R.string.sign_up_empty_fields));
        }
    }

    private boolean areFieldsValid() {
        return !binding.edtTutorFirstName.getText().toString().isEmpty() &&
                !binding.edtTutorLastName.getText().toString().isEmpty() &&
                !binding.edtDateOfBirthTutor.getText().toString().isEmpty() &&
                !binding.edtEmailTutor.getText().toString().isEmpty() &&
                !binding.edtContactNumberTutor.getText().toString().isEmpty() &&
                !binding.edtTutorAddress.getText().toString().isEmpty() &&
                !binding.edtTutorExperience.getText().toString().isEmpty() &&
                !binding.edtTutorGraduationYear.getText().toString().isEmpty() &&
                !binding.edtTutorTeachingTime.getText().toString().isEmpty() &&
                !binding.edtTutorCitizenId.getText().toString().isEmpty() &&
                !binding.edtQualification.getText().toString().isEmpty() &&
                !binding.edtPortraitPhoto.getText().toString().isEmpty() &&
                !binding.edtGenderTutor.getText().toString().isEmpty();
    }

    private void setTutorRequestData() {
        if (Constants.TUTOR_STATUS_REJECTED.equals(userSession.getTutor().getStatus())) {
            tutorRequest.setStatus(Constants.TUTOR_STATUS_PENDING);
        }
        tutorRequest.setFirstName(binding.edtTutorFirstName.getText().toString());
        tutorRequest.setLastName(binding.edtTutorLastName.getText().toString());
        tutorRequest.setDateOfBirth(binding.edtDateOfBirthTutor.getText().toString());
        tutorRequest.setGender(selectedGender);
        tutorRequest.setContactNumber(binding.edtContactNumberTutor.getText().toString());
        tutorRequest.setEmail(binding.edtEmailTutor.getText().toString());
        tutorRequest.setAddress(binding.edtTutorAddress.getText().toString());
        tutorRequest.setExperience(binding.edtTutorExperience.getText().toString());
        tutorRequest.setGraduationYear(Integer.parseInt(binding.edtTutorGraduationYear.getText().toString()));
        tutorRequest.setTeachingTime(binding.edtTutorTeachingTime.getText().toString());
        tutorRequest.setCitizenId(binding.edtTutorCitizenId.getText().toString());
        tutorRequest.setPortraitPhotos(Arrays.asList(binding.edtPortraitPhoto.getText().toString()));
        tutorRequest.setQualification(Arrays.asList(binding.edtQualification.getText().toString()));
    }

    private void updateInformation() {
        binding.progressBar.setVisibility(View.VISIBLE);
        tutorService.updateTutor(userSession.getTutor().get_id(), tutorRequest).enqueue(new Callback<Tutor>() {
            @Override
            public void onResponse(Call<Tutor> call, Response<Tutor> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Log.d("EditProfile", "Update information successfully");
                    Tutor tutor = response.body();
                    if (tutor != null) {
                        userSession.setTutor(tutor);
                        Utility.showToast(requireContext(), "Send request successfully");
                        navController.popBackStack();
                    }
                } else {
                    Log.d("EditProfile", "Update information failed");
                    Utility.showToast(requireContext(), "Send request failed");
                }
            }

            @Override
            public void onFailure(Call<Tutor> call, Throwable throwable) {
                binding.progressBar.setVisibility(View.GONE);
                Log.d("EditProfile", "Update information failed");
                Utility.showToast(requireContext(), "Send request failed");
            }
        });
        adminService.deleteReason(null, userSession.getTutor().get_id()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("EditProfile", "Delete reason successful!");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.d("EditProfile", "Delete reason fail");
            }
        });
    }

    private void setupImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                Uri imageUri = result.getData().getData();
                if (PermissionUtils.hasPermissions(requireActivity())) {
                    File imageFile = Utility.getFileFromUri(requireActivity(), imageUri);
                    if (imageFile != null) {
                        uploadImage(imageFile);
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        isPortrait = false;
                        isQualification = false;
                        Utility.showToast(requireActivity(), "Permissions are required to access photos.");
                    }
                }
            }
        });
    }

    private void uploadImage(File imageFile) {
        UploadImageTask uploadImageTask = new UploadImageTask();
        uploadImageTask.uploadImage(imageFile, new UploadImageTask.UploadCallback() {
            @Override
            public void onSuccess(String url) {
                Log.d("EditProfile", "Image uploaded successfully. URL: " + url);
                binding.progressBar.setVisibility(View.GONE);
                if (isPortrait) {
                    binding.edtPortraitPhoto.setText(url);
                    loadImage(url, binding.ivPortraitPhoto);
                    tutorRequest.setPortraitPhotos(Arrays.asList(url));
                } else if (isQualification) {
                    binding.edtQualification.setText(url);
                    loadImage(url, binding.ivQualification);
                    tutorRequest.setQualification(Arrays.asList(url));
                }
                isPortrait = false;
                isQualification = false;
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("EditProfile", "Image upload failed: " + e);
                binding.progressBar.setVisibility(View.GONE);
                Utility.showToast(requireActivity(), "Image upload failed");
            }
        });
    }

    private void loadImage(String url, ImageView imageView) {
        Glide.with(requireContext())
                .load(url)
                .into(imageView);
    }
}
