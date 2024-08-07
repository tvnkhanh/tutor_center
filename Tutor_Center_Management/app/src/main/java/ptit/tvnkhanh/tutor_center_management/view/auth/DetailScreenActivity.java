package ptit.tvnkhanh.tutor_center_management.view.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Arrays;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.adapter.ItemAdapter;
import ptit.tvnkhanh.tutor_center_management.databinding.ActivityDetailScreenBinding;
import ptit.tvnkhanh.tutor_center_management.models.Account;
import ptit.tvnkhanh.tutor_center_management.models.Client;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.auth.AuthService;
import ptit.tvnkhanh.tutor_center_management.services.auth.models.SignUpRequest;
import ptit.tvnkhanh.tutor_center_management.services.cloudinary.ImagePickerActivity;
import ptit.tvnkhanh.tutor_center_management.services.cloudinary.UploadImageTask;
import ptit.tvnkhanh.tutor_center_management.services.common.ClientService;
import ptit.tvnkhanh.tutor_center_management.services.common.TutorService;
import ptit.tvnkhanh.tutor_center_management.services.common.models.TutorRequest;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.PermissionUtils;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import ptit.tvnkhanh.tutor_center_management.view.custom.CustomToolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailScreenActivity extends AppCompatActivity implements CustomToolbar.OnBackPressListener, View.OnClickListener {
    private ActivityDetailScreenBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private boolean isPortrait = false;
    private boolean isQualification = false;
    private String role = Constants.SIGN_UP_ROLE_STUDENT;
    private String selectedGender = "Male";
    private SignUpRequest account = new SignUpRequest();
    private TutorRequest tutorRequest = new TutorRequest();
    private Client client = new Client();
    private AuthService authService;
    private ClientService clientService;
    private TutorService tutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailScreenBinding.inflate(getLayoutInflater());
        authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
        clientService = RetrofitClient.getRetrofitInstance().create(ClientService.class);
        tutorService = RetrofitClient.getRetrofitInstance().create(TutorService.class);
        initUI();
        setContentView(binding.getRoot());
    }

    private void initUI() {
        account.setUsername(getIntent().getStringExtra(Constants.SIGN_UP_USERNAME));
        account.setPasswordHash(getIntent().getStringExtra(Constants.SIGN_UP_PASSWORD));

        binding.toolbar.setTitle(getString(R.string.detail_screen_title));
        binding.toolbar.setListener(this);

        role = getIntent().getStringExtra(Constants.SIGN_UP_ROLE);
        if (role != null && !role.isEmpty()) {
            if (role.equals(Constants.SIGN_UP_ROLE_STUDENT)) {
                binding.setIsTutor(false);
            } else if (role.equals(Constants.SIGN_UP_ROLE_TUTOR)) {
                binding.setIsTutor(true);
            }
        }

        if (!PermissionUtils.hasPermissions(this)) {
            PermissionUtils.requestPermissions(this);
        } else {
            setupImagePickerLauncher();
        }

        binding.edtGenderTutor.setOnClickListener(view -> {
            showGenderPicker();
        });

        binding.edtPortraitPhoto.setOnClickListener(view -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, ImagePickerActivity.class);
            if (!PermissionUtils.hasPermissions(this)) {
                PermissionUtils.requestPermissions(this);
            }
            isPortrait = true;
            imagePickerLauncher.launch(intent);
        });

        binding.edtQualification.setOnClickListener(view -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, ImagePickerActivity.class);
            if (!PermissionUtils.hasPermissions(this)) {
                PermissionUtils.requestPermissions(this);
            }
            isQualification = true;
            imagePickerLauncher.launch(intent);
        });

        binding.btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onBackPress() {
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSubmit) {
            signUp();
        }
    }

    private void setupImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                Uri imageUri = result.getData().getData();
                if (PermissionUtils.hasPermissions(this)) {
                    File imageFile = Utility.getFileFromUri(this, imageUri);
                    if (imageFile != null) {
                        UploadImageTask uploadImageTask = new UploadImageTask();
                        uploadImageTask.uploadImage(imageFile, new UploadImageTask.UploadCallback() {
                            @Override
                            public void onSuccess(String url) {
                                Log.d("DetailScreenActivity", "Image uploaded successfully. URL: " + url);
                                binding.progressBar.setVisibility(View.GONE);
                                if (isPortrait) {
                                    binding.edtPortraitPhoto.setText(url);
                                    Glide.with(DetailScreenActivity.this).load(url).into(binding.ivPortraitPhoto);
                                    tutorRequest.setPortraitPhotos(Arrays.asList(url));
                                } else if (isQualification) {
                                    binding.edtQualification.setText(url);
                                    Glide.with(DetailScreenActivity.this).load(url).into(binding.ivQualification);
                                    tutorRequest.setQualification(Arrays.asList(url));
                                }
                                isPortrait = false;
                                isQualification = false;
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e("DetailScreenActivity", "Image upload failed: " + e.getMessage());
                                binding.progressBar.setVisibility(View.GONE);
                                isPortrait = false;
                                isQualification = false;
                            }
                        });
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        isPortrait = false;
                        isQualification = false;
                        Utility.showToast(this, "Permissions are required to access photos.");
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.handlePermissionsResult(this, requestCode, permissions, grantResults, granted -> {
            if (granted) {
                setupImagePickerLauncher();
            } else {
                Utility.showToast(this, "Permissions are required to access photos.");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.progressBar.setVisibility(View.GONE);
    }

    private void showGenderPicker() {
        Utility.showItemDialog(this, Constants.SIGN_UP_GENDER_LIST, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Utility.hideKeyboard(DetailScreenActivity.this);
                Utility.clearEditTextFocus(DetailScreenActivity.this);
                selectedGender = Constants.SIGN_UP_GENDER_LIST.get(position);
                binding.edtGenderTutor.setText(selectedGender);
            }
        }, "gender", Constants.SIGN_UP_GENDER_LIST.indexOf(selectedGender));
    }

    private void signUp() {
        if (role.equals(Constants.SIGN_UP_ROLE_STUDENT)) {
            if (!binding.edtFirstNameUser.getText().toString().isEmpty() && !binding.edtLastNameUser.getText().toString().isEmpty() &&
                    !binding.edtEmailUser.getText().toString().isEmpty() &&
                    !binding.edtContactNumberUser.getText().toString().isEmpty()) {

                client.setFirstName(binding.edtFirstNameUser.getText().toString());
                client.setLastName(binding.edtLastNameUser.getText().toString());
                client.setContactNumber(binding.edtContactNumberUser.getText().toString());
                client.setEmail(binding.edtEmailUser.getText().toString());

                signUpProcess();
            } else {
                Utility.showToast(this, getString(R.string.sign_up_empty_fields));
            }
        } else if (role.equals(Constants.SIGN_UP_ROLE_TUTOR)) {
            if (!binding.edtTutorFirstName.getText().toString().isEmpty() && !binding.edtTutorLastName.getText().toString().isEmpty() &&
                    !binding.edtDateOfBirthTutor.getText().toString().isEmpty() && !binding.edtEmailTutor.getText().toString().isEmpty() &&
                    !binding.edtContactNumberTutor.getText().toString().isEmpty() && !binding.edtTutorAddress.getText().toString().isEmpty() &&
                    !binding.edtTutorExperience.getText().toString().isEmpty() && !binding.edtTutorGraduationYear.getText().toString().isEmpty() &&
                    !binding.edtTutorTeachingTime.getText().toString().isEmpty() && !binding.edtTutorCitizenId.getText().toString().isEmpty() &&
                    !binding.edtQualification.getText().toString().isEmpty() && !binding.edtPortraitPhoto.getText().toString().isEmpty() &&
                    !binding.edtGenderTutor.getText().toString().isEmpty()) {

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

                signUpProcess();
            } else {
                Utility.showToast(this, getString(R.string.sign_up_empty_fields));
            }
        }
    }

    private void signUpProcess() {
        if (role.equals(Constants.SIGN_UP_ROLE_STUDENT)) {
            clientService.createClient(client).enqueue(new Callback<Client>() {
                @Override
                public void onResponse(Call<Client> call, Response<Client> response) {
                    if (response.isSuccessful()) {
                        getId();
                    }
                }

                @Override
                public void onFailure(Call<Client> call, Throwable throwable) {

                }
            });
        } else if (role.equals(Constants.SIGN_UP_ROLE_TUTOR)) {
            tutorService.createTutor(tutorRequest).enqueue(new Callback<Tutor>() {
                @Override
                public void onResponse(Call<Tutor> call, Response<Tutor> response) {
                    if (response.isSuccessful()) {
                        getId();
                    }
                }

                @Override
                public void onFailure(Call<Tutor> call, Throwable throwable) {

                }
            });
        }
    }

    private void getId() {
        if (role.equals(Constants.SIGN_UP_ROLE_STUDENT)) {
            account.setRoleId(Constants.ROLE_CLIENT_ID);
            clientService.getClientId(client.getContactNumber()).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        String clientId = response.body();
                        account.setClientId(clientId);
                        createAccount();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable throwable) {

                }
            });
        } else if (role.equals(Constants.SIGN_UP_ROLE_TUTOR)) {
            account.setRoleId(Constants.ROLE_TUTOR_ID);
            tutorService.getTutorId(tutorRequest.getContactNumber()).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        String tutorId = response.body();
                        account.setTutorId(tutorId);
                        createAccount();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable throwable) {

                }
            });
        }
    }

    private void createAccount() {
        authService.signUp(account).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                Utility.showToast(DetailScreenActivity.this, getString(R.string.sign_up_screen_successful));
                Intent intent = new Intent(DetailScreenActivity.this, SignInScreenActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Account> call, Throwable throwable) {

            }
        });
    }
}