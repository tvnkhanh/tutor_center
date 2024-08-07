package ptit.tvnkhanh.tutor_center_management.view.admin;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import ptit.tvnkhanh.tutor_center_management.adapter.AdminClassAdapter;
import ptit.tvnkhanh.tutor_center_management.databinding.DialogSearchTutorBinding;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentAdminClassesBinding;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.models.TutoringClass;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.admin.AdminService;
import ptit.tvnkhanh.tutor_center_management.services.admin.models.StatusRequest;
import ptit.tvnkhanh.tutor_center_management.services.common.ClassService;
import ptit.tvnkhanh.tutor_center_management.services.common.TutorService;
import ptit.tvnkhanh.tutor_center_management.services.common.models.ReasonRequest;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminClassesFragment extends Fragment implements AdminClassAdapter.OnAdminClassClickListener {
    private FragmentAdminClassesBinding binding;
    private AdminClassAdapter adapter;
    private List<TutoringClass> classes;
    private AdminService adminService;
    private ClassService classService;
    private TutorService tutorService;
    private String token;
    private List<String> tutorList;
    private String selectedTutorId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminClassesBinding.inflate(getLayoutInflater());
        token = SharedPreferencesUtility.getString(requireContext(), Constants.X_AUTH_TOKEN, "");
        adminService = RetrofitClient.getRetrofitInstance().create(AdminService.class);
        tutorService = RetrofitClient.getRetrofitInstance().create(TutorService.class);
        initUI();
        getClassesData();
        return binding.getRoot();
    }

    private void initUI() {
        binding.progressBar.setVisibility(View.GONE);
        binding.spinner.setData(Constants.CLASS_STATUS_LIST_REVERSE);

        binding.spinner.setSelection(0);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedStatus = Constants.CLASS_STATUS_LIST_REVERSE.get(i);
                filterAndSortClasses(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle if needed
            }
        });

        RecyclerView recyclerView = binding.rvClassContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getClassesData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        classService = RetrofitClient.getRetrofitInstance().create(ClassService.class);
        if (token != null && !token.isEmpty()) {
            classService.getClasses(token).enqueue(new Callback<List<TutoringClass>>() {
                @Override
                public void onResponse(Call<List<TutoringClass>> call, Response<List<TutoringClass>> response) {
                    if (response.isSuccessful()) {
                        classes = response.body();
                        if (classes != null) {
                            String selectedStatus = Constants.CLASS_STATUS_LIST_REVERSE.get(binding.spinner.getSelectedItemPosition());
                            filterAndSortClasses(selectedStatus);
                        }
                    } else {
                        Log.d("AdminClassesFragment", "onResponse: " + response.code());
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<List<TutoringClass>> call, Throwable throwable) {
                    Log.d("AdminClassesFragment", "onFailure: " + throwable.getMessage());
                    binding.progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onChangeStatusClick(TutoringClass classItem) {
        Log.d("AdminClassesFragment", "onChangeStatusClick: " + classItem.get_id());

        String title;
        String message;
        if (Constants.CLASS_STATUS_PENDING.equals(classItem.getStatus())) {
            title = "Approve Class";
            message = "Are you sure you want to approve this class?";
        } else if (Constants.CLASS_STATUS_APPROVED.equals(classItem.getStatus())) {
            title = "Assign Class";
            message = "Are you sure you want to assign this class?";
        } else if (Constants.CLASS_STATUS_ASSIGNED.equals(classItem.getStatus())) {
            title = "Complete Class";
            message = "Are you sure you want to mark this class as completed?";
        } else {
            title = "Change Status";
            message = "Are you sure you want to change the status of this class?";
        }

        Utility.showConfirmationDialog(requireContext(),
                title,
                message,
                "Yes", "No",
                new Utility.OnDialogWarningCallback() {
                    @Override
                    public void onConfirm() {
                        if ((classItem.getTutorId() != null && !classItem.getTutorId().isEmpty() && Objects.equals(classItem.getStatus(), Constants.CLASS_STATUS_APPROVED))
                                || Objects.equals(classItem.getStatus(), Constants.CLASS_STATUS_PENDING)
                                || Objects.equals(classItem.getStatus(), Constants.CLASS_STATUS_ASSIGNED)) {
                            if (!classItem.getTutorId().isEmpty() && Objects.equals(classItem.getStatus(), Constants.CLASS_STATUS_APPROVED)) {
                                getTutorsByIds(classItem.getTutorId(), classItem);
                            } else {
                                changeClassStatus(classItem);
                            }
                        } else {
                            showSearchTutorDialog(classItem);
                        }
                    }

                    @Override
                    public void onCancel() {
                        // Handle cancel action if needed
                    }
                });
    }

    @Override
    public void onRejectedClick(TutoringClass classItem) {
        Log.d("AdminClassesFragment", "onRejectedClick: " + classItem.get_id());
        Utility.showConfirmationDialog(requireContext(), "Warning",
                "Are you sure you want to reject this class?",
                "Yes",
                "No",
                new Utility.OnDialogWarningCallback() {
                    @Override
                    public void onConfirm() {
                        Utility.showInputReasonDialog(requireContext(),
                                "Delete class",
                                "Please provide a reason for deleting this class",
                                "Delete",
                                "Cancel",
                                new Utility.InputReasonCallback() {
                                    @Override
                                    public void onConfirm(String reason) {
                                        binding.progressBar.setVisibility(View.VISIBLE);
                                        StatusRequest statusRequest = new StatusRequest(Constants.CLASS_STATUS_REJECTED);
                                        adminService.updateClassStatus(token, classItem.get_id(), statusRequest).enqueue(new Callback<TutoringClass>() {
                                            @Override
                                            public void onResponse(Call<TutoringClass> call, Response<TutoringClass> response) {
                                                if (response.isSuccessful()) {
                                                    Log.d("AdminClassesFragment", "onResponse: " + response.body());
                                                    getClassesData();
                                                } else {
                                                    Log.d("AdminClassesFragment", "onResponse: " + response.code());
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<TutoringClass> call, Throwable throwable) {
                                                Log.d("AdminClassesFragment", "onFailure: " + throwable.getMessage());
                                            }
                                        });
                                        adminService.createReason(new ReasonRequest(classItem.get_id(), reason)).enqueue(new Callback<ReasonRequest>() {
                                            @Override
                                            public void onResponse(Call<ReasonRequest> call, Response<ReasonRequest> response) {
                                                Log.d("AdminClassesFragment", "onResponse: " + response.body());
                                            }

                                            @Override
                                            public void onFailure(Call<ReasonRequest> call, Throwable throwable) {
                                                Log.d("AdminClassesFragment", "onFailure: " + throwable.getMessage());
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    @Override
    public void onClassItemDetailClick(TutoringClass classItem) {
        Utility.showClassDetailDialog(requireContext(), classItem);
    }

    @Override
    public void onEditClassClick(TutoringClass classItem) {

    }

    private void getTutorsByIds(List<String> ids, TutoringClass classItem) {
        tutorService.getAllTutors(token).enqueue(new Callback<List<Tutor>>() {
            @Override
            public void onResponse(Call<List<Tutor>> call, Response<List<Tutor>> response) {
                if (response.isSuccessful()) {
                    List<Tutor> tutors = response.body();
                    if (tutors != null) {
                        tutorList = new ArrayList<>();
                        for (Tutor tutor : tutors) {
                            if (ids.contains(tutor.get_id())) {
                                tutorList.add(tutor.getFirstName() + " " + tutor.getLastName() + " - " + tutor.getContactNumber());
                            }
                        }

                        Utility.showItemDialogOptions(requireActivity(), tutorList, position -> {
                            Log.d("AdminClassesFragment", "onResponse: " + tutorList.get(position));
                            Log.d("AdminClassesFragment", "onResponse: " + tutors.get(position).get_id());
                            selectedTutorId = tutors.get(position).get_id();
                        }, "Registered Tutors", -1, new Utility.OnItemDialogCallback() {
                            @Override
                            public void onConfirm() {
                                TutoringClass tutoringClass = new TutoringClass();
                                tutoringClass.setTutorId(new ArrayList<>(List.of(selectedTutorId)));
                                updateClass(classItem.get_id(), tutoringClass);
                                changeClassStatus(classItem);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Tutor>> call, Throwable throwable) {

            }
        });
    }

    private void updateClass(String classId, TutoringClass updatedClass) {
        classService.updateClass(classId, updatedClass).enqueue(new Callback<TutoringClass>() {
            @Override
            public void onResponse(Call<TutoringClass> call, Response<TutoringClass> response) {
                if (response.isSuccessful()) {
                    Log.d("AdminClassesFragment", "onResponse: " + response.body());
                } else {
                    Log.d("AdminClassesFragment", "onResponse: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TutoringClass> call, Throwable throwable) {
                Log.d("AdminClassesFragment", "onFailure: " + throwable.getMessage());
            }
        });
    }

    private void changeClassStatus(TutoringClass classItem) {
        binding.progressBar.setVisibility(View.VISIBLE);
        StatusRequest statusRequest = new StatusRequest();
        if (classItem.getStatus().equals(Constants.CLASS_STATUS_PENDING)) {
            statusRequest.setStatus(Constants.CLASS_STATUS_APPROVED);
        } else if (classItem.getStatus().equals(Constants.CLASS_STATUS_APPROVED)) {
            statusRequest.setStatus(Constants.CLASS_STATUS_ASSIGNED);
        } else if (classItem.getStatus().equals(Constants.CLASS_STATUS_ASSIGNED)) {
            statusRequest.setStatus(Constants.CLASS_STATUS_COMPLETED);
        }
        adminService.updateClassStatus(token, classItem.get_id(), statusRequest).enqueue(new Callback<TutoringClass>() {
            @Override
            public void onResponse(Call<TutoringClass> call, Response<TutoringClass> response) {
                if (response.isSuccessful()) {
                    Log.d("AdminClassesFragment", "onResponse: " + response.body());
                    getClassesData();
                } else {
                    Log.d("AdminClassesFragment", "onResponse: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TutoringClass> call, Throwable throwable) {
                Log.d("AdminClassesFragment", "onFailure: " + throwable.getMessage());
            }
        });
    }

    private void filterAndSortClasses(String selectedStatus) {
        if (classes == null) {
            Log.e("AdminClassesFragment", "Classes list is null");
            return;
        }

        List<TutoringClass> filteredClasses = new ArrayList<>();

        for (TutoringClass tutoringClass : classes) {
            if (Objects.equals(tutoringClass.getStatus(), selectedStatus)) {
                filteredClasses.add(tutoringClass);
            }
        }

        if (filteredClasses.isEmpty()) {
            Log.d("AdminClassesFragment", "No classes match the selected status: " + selectedStatus);
        }

        Collections.sort(filteredClasses, new Comparator<TutoringClass>() {
            @Override
            public int compare(TutoringClass c1, TutoringClass c2) {
                if (selectedStatus.equals(Constants.CLASS_STATUS_PENDING) ||
                        selectedStatus.equals(Constants.CLASS_STATUS_APPROVED) ||
                        selectedStatus.equals(Constants.CLASS_STATUS_ASSIGNED)) {
                    return c1.getUpdateDate().compareTo(c2.getUpdateDate());
                } else if (selectedStatus.equals(Constants.CLASS_STATUS_REJECTED) ||
                        selectedStatus.equals(Constants.CLASS_STATUS_COMPLETED)) {
                    return c2.getUpdateDate().compareTo(c1.getUpdateDate());
                }
                return 0;
            }
        });

        if (adapter == null) {
            adapter = new AdminClassAdapter(requireContext(), filteredClasses, AdminClassesFragment.this);
            RecyclerView recyclerView = binding.rvClassContainer;
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateClasses(filteredClasses);
        }
    }

    private void showSearchTutorDialog(TutoringClass classItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        DialogSearchTutorBinding binding = DialogSearchTutorBinding.inflate(inflater);
        builder.setView(binding.getRoot());

        builder.setTitle("Search tutors in Center");

        builder.setPositiveButton("Search", (dialog, which) -> {
            String keyword = binding.editTextContact.getText().toString();
            searchTutorsByContact(keyword, classItem);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void searchTutorsByContact(String contactNumber, TutoringClass classItem) {
        tutorService.searchTutorByContact(token, contactNumber).enqueue(new Callback<List<Tutor>>() {
            @Override
            public void onResponse(Call<List<Tutor>> call, Response<List<Tutor>> response) {
                if (response.isSuccessful()) {
                    List<Tutor> tutors = response.body();
                    if (tutors != null && !tutors.isEmpty()) {
                        List<Tutor> approvedTutors = new ArrayList<>();
                        for (Tutor tutor : tutors) {
                            if (Constants.TUTOR_STATUS_APPROVED.equals(tutor.getStatus())) {
                                approvedTutors.add(tutor);
                            }
                        }

                        if (!approvedTutors.isEmpty()) {
                            List<String> tutorList = new ArrayList<>();
                            for (Tutor tutor : approvedTutors) {
                                tutorList.add(tutor.getFirstName() + " " + tutor.getLastName() + " - " + tutor.getContactNumber());
                            }

                            Utility.showItemDialogOptions(requireActivity(), tutorList, position -> {
                                Log.d("AdminClassesFragment", "Selected tutor: " + tutorList.get(position));
                                selectedTutorId = approvedTutors.get(position).get_id();
                            }, "Center Tutors", -1, new Utility.OnItemDialogCallback() {
                                @Override
                                public void onConfirm() {
                                    TutoringClass tutoringClass = new TutoringClass();
                                    tutoringClass.setTutorId(new ArrayList<>(List.of(selectedTutorId)));
                                    updateClass(classItem.get_id(), tutoringClass);
                                    changeClassStatus(classItem);
                                }

                                @Override
                                public void onCancel() {
                                    // Handle cancel action if needed
                                }
                            });
                        } else {
                            Utility.showToast(requireContext(), "No approved tutors found with the provided contact number.");
                        }
                    } else {
                        Utility.showToast(requireContext(), "No tutors found with the provided contact number.");
                    }
                } else {
                    Log.d("AdminClassesFragment", "onResponse: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Tutor>> call, Throwable throwable) {
                Log.d("AdminClassesFragment", "onFailure: " + throwable.getMessage());
            }
        });
    }
}