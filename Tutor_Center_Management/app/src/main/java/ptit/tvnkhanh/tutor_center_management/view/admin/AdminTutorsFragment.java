package ptit.tvnkhanh.tutor_center_management.view.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.adapter.AdminTutorAdapter;
import ptit.tvnkhanh.tutor_center_management.callback.OnNavigationListener;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentAdminTutorsBinding;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.admin.AdminService;
import ptit.tvnkhanh.tutor_center_management.services.admin.models.StatusRequest;
import ptit.tvnkhanh.tutor_center_management.services.common.TutorService;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTutorsFragment extends Fragment implements AdminTutorAdapter.OnTutorItemClickListener {
    private FragmentAdminTutorsBinding binding;
    private List<Tutor> tutors;
    private AdminService adminService;
    private TutorService tutorService;
    private AdminTutorAdapter adapter;
    private String token;
    private OnNavigationListener navigationListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminTutorsBinding.inflate(getLayoutInflater());
        token = SharedPreferencesUtility.getString(requireContext(), Constants.X_AUTH_TOKEN, "");
        adminService = RetrofitClient.getRetrofitInstance().create(AdminService.class);
        getTutors();
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        navigationListener = (OnNavigationListener) context;
    }

    private void initUI() {
        binding.progressBar.setVisibility(View.GONE);
        RecyclerView recyclerView = binding.rvTutorContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void getTutors() {
        binding.progressBar.setVisibility(View.VISIBLE);
        tutorService = RetrofitClient.getRetrofitInstance().create(TutorService.class);
        if (token != null && !token.isEmpty()) {
            tutorService.getAllTutors(token).enqueue(new Callback<List<Tutor>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<List<Tutor>> call, Response<List<Tutor>> response) {
                    if (response.isSuccessful()) {
                        tutors = response.body();
                        if (tutors != null) {
                            tutors.removeIf(tutor -> !tutor.getStatus().equals(Constants.TUTOR_STATUS_PENDING));
                            adapter = new AdminTutorAdapter(tutors, requireContext(), AdminTutorsFragment.this);
                            requireActivity().runOnUiThread(() -> {
                                adapter.notifyDataSetChanged();
                                initUI();
                            });
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Tutor>> call, Throwable throwable) {
                    Log.d("AdminTutorsFragment", "onFailure: " + throwable.getMessage());
                }
            });
        }
    }

    @Override
    public void onApproveClick(Tutor tutor) {
        Log.d("AdminTutorsFragment", "onTutorItemClick: " + tutor.get_id());
        adminService.updateTutorStatus(token, tutor.get_id(), new StatusRequest(Constants.TUTOR_STATUS_APPROVED)).enqueue(new Callback<Tutor>() {
            @Override
            public void onResponse(Call<Tutor> call, Response<Tutor> response) {
                if (response.isSuccessful()) {
                    Log.d("AdminTutorsFragment", "onResponse: " + response.body());
                    getTutors();
                } else {
                    Log.d("AdminTutorsFragment", "onResponse: " + response.code());
                    Log.d("AdminTutorsFragment", "onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Tutor> call, Throwable throwable) {
                Log.d("AdminTutorsFragment", "onFailure: " + throwable.getMessage());
            }
        });
    }

    @Override
    public void onRejectClick(Tutor tutor) {
        adminService.updateTutorStatus(token, tutor.get_id(), new StatusRequest(Constants.TUTOR_STATUS_REJECTED)).enqueue(new Callback<Tutor>() {
            @Override
            public void onResponse(Call<Tutor> call, Response<Tutor> response) {
                if (response.isSuccessful()) {
                    Log.d("AdminTutorsFragment", "onResponse: " + response.body());
                    getTutors();
                } else {
                    Log.d("AdminTutorsFragment", "onResponse: " + response.code());
                    Log.d("AdminTutorsFragment", "onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Tutor> call, Throwable throwable) {
                Log.d("AdminTutorsFragment", "onFailure: " + throwable.getMessage());
            }
        });
    }
}