package ptit.tvnkhanh.tutor_center_management.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.adapter.ClassAdapter;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentCourseScreenBinding;
import ptit.tvnkhanh.tutor_center_management.models.TutoringClass;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.admin.AdminService;
import ptit.tvnkhanh.tutor_center_management.services.common.ClassService;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseScreenFragment extends Fragment implements ClassAdapter.OnRegisterClickListener {

    private FragmentCourseScreenBinding binding;
    private ClassAdapter classAdapter;
    private List<TutoringClass> classes;
    private ClassService classService;
    private AdminService adminService;
    private String token;
    private String tutorId;
    private String roleId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCourseScreenBinding.inflate(getLayoutInflater());
        adminService = RetrofitClient.getRetrofitInstance().create(AdminService.class);
        classService = RetrofitClient.getRetrofitInstance().create(ClassService.class);
        roleId = UserSession.getInstance().getAccount().getRoleId();
        token = SharedPreferencesUtility.getString(requireContext(), Constants.X_AUTH_TOKEN, "");
        getClassesDataByAccount();
        return binding.getRoot();
    }

    private void initUI() {
        binding.progressBar.setVisibility(View.GONE);

        if (Objects.equals(roleId, Constants.ROLE_CLIENT_ID)) {
            binding.spinner.setData(Constants.CLASS_STATUS_LIST_REVERSE);
        } else if (Objects.equals(roleId, Constants.ROLE_TUTOR_ID)) {
            binding.spinner.setData(Constants.CLASS_STATUS_LIST_TUTOR);
        }

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = (String) parent.getItemAtPosition(position);
                filterAndSortClasses(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where no status is selected
            }
        });

        RecyclerView recyclerView = binding.rvCourseContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        classAdapter = new ClassAdapter(new ArrayList<>(), requireContext(), tutorId != null && !tutorId.isEmpty(), CourseScreenFragment.this);
        recyclerView.setAdapter(classAdapter);
    }


    private void getClassesDataByAccount() {
        binding.progressBar.setVisibility(View.VISIBLE);
        if (token != null && !token.isEmpty()) {
            String id = "";
            if (Objects.equals(roleId, Constants.ROLE_CLIENT_ID))
                id = UserSession.getInstance().getClient().get_id();
            else if (Objects.equals(roleId, Constants.ROLE_TUTOR_ID))
                id = UserSession.getInstance().getTutor().get_id();
            classService.getClassById(token, id).enqueue(new Callback<List<TutoringClass>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<List<TutoringClass>> call, Response<List<TutoringClass>> response) {
                    if (response.isSuccessful()) {
                        classes = response.body();
                        if (classes != null) {
                            Log.d("CourseScreenFragment", "classes: " + classes.toString());
                            classes.sort((o1, o2) -> {
                                int index1 = Constants.CLASS_STATUS_LIST_REVERSE.indexOf(o1.getStatus());
                                int index2 = Constants.CLASS_STATUS_LIST_REVERSE.indexOf(o2.getStatus());
                                return Integer.compare(index1, index2);
                            });
                            classAdapter = new ClassAdapter(classes, requireContext(), tutorId != null && !tutorId.isEmpty(), CourseScreenFragment.this);
                            requireActivity().runOnUiThread(() -> {
                                classAdapter.notifyDataSetChanged();
                                initUI();
                            });
                        } else {
                            Log.d("CourseScreenFragment", "Classes data is null");
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<TutoringClass>> call, Throwable throwable) {
                    Log.d("CourseScreenFragment", "Error: " + throwable.getMessage());
                }
            });
        }
    }

    @Override
    public void onRegisterClick(String classId) {

    }

    @Override
    public void onClassItemClicked(TutoringClass tutoringClass) {
        Utility.showClassDetailDialog(requireContext(), tutoringClass);
    }

    @Override
    public void onEditClick(TutoringClass tutoringClass) {
        Utility.showUpdateClassDialog(requireContext(), tutoringClass, new Utility.OnUpdateClassListener() {
            @Override
            public void onUpdateClass(TutoringClass updatedClass) {
                Log.d("CourseScreenFragment", "Updated class: " + updatedClass.toString());
                updateClass(tutoringClass, updatedClass);
            }
        });
    }

    private void updateClass(TutoringClass tutoringClass, TutoringClass updatedClass) {
        classService.updateClass(tutoringClass.get_id(), updatedClass).enqueue(new Callback<TutoringClass>() {
            @Override
            public void onResponse(Call<TutoringClass> call, Response<TutoringClass> response) {
                if (response.isSuccessful()) {
                    TutoringClass updatedTutoringClass = response.body();
                    if (updatedTutoringClass != null) {
                        Log.d("CourseScreenFragment", "Class updated successfully");
                        getClassesDataByAccount();
                    }
                }
            }

            @Override
            public void onFailure(Call<TutoringClass> call, Throwable throwable) {
                Log.d("CourseScreenFragment", "Error: " + throwable.getMessage());
            }
        });
        adminService.deleteReason(tutoringClass.get_id()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("CourseScreenFragment", "Reason deleted successfully");
                } else {
                    Log.d("CourseScreenFragment", "Failed to delete reason");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.d("CourseScreenFragment", "Error: " + throwable.getMessage());
            }
        });
    }

    private void filterAndSortClasses(String selectedStatus) {
        if (classes != null) {
            List<TutoringClass> filteredClasses = new ArrayList<>();

            if (Objects.equals(selectedStatus, Constants.CLASS_STATUS_REGISTERED)) {
                selectedStatus = Constants.CLASS_STATUS_APPROVED;
            }

            for (TutoringClass tutoringClass : classes) {
                if (Objects.equals(tutoringClass.getStatus(), selectedStatus)) {
                    filteredClasses.add(tutoringClass);
                }
            }

            filteredClasses.sort((o1, o2) -> {
                if (o1.getUpdateDate() == null || o2.getUpdateDate() == null) {
                    return 0;
                }
                return o2.getUpdateDate().compareTo(o1.getUpdateDate());
            });

            classAdapter.updateData(filteredClasses);
        }
    }

}