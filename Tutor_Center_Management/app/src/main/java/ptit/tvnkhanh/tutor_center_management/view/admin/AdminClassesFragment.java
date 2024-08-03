package ptit.tvnkhanh.tutor_center_management.view.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.adapter.AdminClassAdapter;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentAdminClassesBinding;
import ptit.tvnkhanh.tutor_center_management.models.TutoringClass;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.admin.AdminService;
import ptit.tvnkhanh.tutor_center_management.services.admin.models.StatusRequest;
import ptit.tvnkhanh.tutor_center_management.services.common.ClassService;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminClassesFragment extends Fragment implements AdminClassAdapter.OnAdminClassClickListener {
    private FragmentAdminClassesBinding binding;
    private AdminClassAdapter adapter;
    private List<TutoringClass> classes;
    private AdminService adminService;
    private ClassService classService;
    private String token;

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
        getClassesData();
        return binding.getRoot();
    }

    private void initUI() {
        binding.progressBar.setVisibility(View.GONE);
        RecyclerView recyclerView = binding.rvClassContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void getClassesData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        classService = RetrofitClient.getRetrofitInstance().create(ClassService.class);
        if (token != null && !token.isEmpty()) {
            classService.getClasses(token).enqueue(new Callback<List<TutoringClass>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<List<TutoringClass>> call, Response<List<TutoringClass>> response) {
                    if (response.isSuccessful()) {
                        classes = response.body();
                        if (classes != null) {
                            classes.removeIf(tutoringClass -> tutoringClass.getStatus().equals(Constants.CLASS_STATUS_COMPLETED));
                            adapter = new AdminClassAdapter(requireContext(), classes, AdminClassesFragment.this);
                            requireActivity().runOnUiThread(() -> {
                                adapter.notifyDataSetChanged();
                                initUI();
                            });
                        } else {
                            Log.d("AdminClassesFragment", "onResponse: " + response.code());
                        }
                    } else {
                        Log.d("AdminClassesFragment", "onResponse: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<TutoringClass>> call, Throwable throwable) {
                    Log.d("AdminClassesFragment", "onFailure: " + throwable.getMessage());
                }
            });
        }
    }

    @Override
    public void onChangeStatusClick(TutoringClass classItem) {
        Log.d("AdminClassesFragment", "onChangeStatusClick: " + classItem.get_id());
        binding.progressBar.setVisibility(View.VISIBLE);
        StatusRequest statusRequest = new StatusRequest();
        if (classItem.getStatus().equals(Constants.CLASS_STATUS_PENDING)) {
            statusRequest.setStatus(Constants.CLASS_STATUS_INACTIVE);
        } else if (classItem.getStatus().equals(Constants.CLASS_STATUS_INACTIVE)) {
            statusRequest.setStatus(Constants.CLASS_STATUS_ACTIVE);
        } else if (classItem.getStatus().equals(Constants.CLASS_STATUS_ACTIVE)) {
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

    @Override
    public void onRejectedClick(TutoringClass classItem) {
        Log.d("AdminClassesFragment", "onRejectedClick: " + classItem.get_id());
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
    }
}