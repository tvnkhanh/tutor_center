package ptit.tvnkhanh.tutor_center_management.view.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import ptit.tvnkhanh.tutor_center_management.adapter.AdminTutorAdapter;
import ptit.tvnkhanh.tutor_center_management.callback.OnNavigationListener;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentAdminTutorsBinding;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.admin.AdminService;
import ptit.tvnkhanh.tutor_center_management.services.admin.models.ReasonRequest;
import ptit.tvnkhanh.tutor_center_management.services.admin.models.StatusRequest;
import ptit.tvnkhanh.tutor_center_management.services.common.TutorService;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTutorsFragment extends Fragment implements AdminTutorAdapter.OnTutorItemClickListener {
    private FragmentAdminTutorsBinding binding;
    private List<Tutor> tutors = new ArrayList<>();
    private AdminService adminService;
    private TutorService tutorService;
    private AdminTutorAdapter adapter;
    private String token;
    private OnNavigationListener navigationListener;
    private String selectedStatus = "";

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
        tutorService = RetrofitClient.getRetrofitInstance().create(TutorService.class);

        initUI();
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
        binding.spinner.setData(Constants.TUTOR_STATUS_LIST);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedStatus = Constants.TUTOR_STATUS_LIST.get(i);
                filterAndUpdateTutors();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle case when nothing is selected if necessary
            }
        });
        RecyclerView recyclerView = binding.rvTutorContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminTutorAdapter(new ArrayList<>(), requireContext(), AdminTutorsFragment.this);
        recyclerView.setAdapter(adapter);
    }

    private void getTutors() {
        binding.progressBar.setVisibility(View.VISIBLE);
        if (token != null && !token.isEmpty()) {
            tutorService.getAllTutors(token).enqueue(new Callback<List<Tutor>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<List<Tutor>> call, Response<List<Tutor>> response) {
                    if (response.isSuccessful()) {
                        tutors = response.body();
                        filterAndUpdateTutors();
                    }
                }

                @Override
                public void onFailure(Call<List<Tutor>> call, Throwable throwable) {
                    Log.d("AdminTutorsFragment", "onFailure: " + throwable.getMessage());
                }
            });
        }
    }

    private void filterAndUpdateTutors() {
        List<Tutor> filteredTutors = new ArrayList<>();

        for (Tutor tutor : tutors) {
            if (Objects.equals(tutor.getStatus(), selectedStatus) || selectedStatus.isEmpty()) {
                filteredTutors.add(tutor);
            }
        }

        Collections.sort(filteredTutors, new Comparator<Tutor>() {
            @Override
            public int compare(Tutor t1, Tutor t2) {
                return t2.getRegisterDate().compareTo(t1.getRegisterDate());
            }
        });

        adapter = new AdminTutorAdapter(filteredTutors, requireContext(), AdminTutorsFragment.this);
        RecyclerView recyclerView = binding.rvTutorContainer;
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onApproveClick(Tutor tutor) {
        Log.d("AdminTutorsFragment", "onApproveClick: " + tutor.get_id());
        Utility.showConfirmationDialog(requireContext(),
                "Approve Tutor",
                "Are you sure you want to approve this tutor?",
                "Yes", "No",
                new Utility.OnDialogWarningCallback() {
                    @Override
                    public void onConfirm() {
                        adminService.updateTutorStatus(token, tutor.get_id(), new StatusRequest(Constants.TUTOR_STATUS_APPROVED)).enqueue(new Callback<Tutor>() {
                            @Override
                            public void onResponse(Call<Tutor> call, Response<Tutor> response) {
                                if (response.isSuccessful()) {
                                    Log.d("AdminTutorsFragment", "onResponse: " + response.body());
                                    Utility.showToast(requireContext(), "Tutor approved successfully");
                                    getTutors();
                                } else {
                                    Log.d("AdminTutorsFragment", "onResponse: " + response.code());
                                    Log.d("AdminTutorsFragment", "onResponse: " + response.errorBody());
                                    Utility.showToast(requireContext(), "Tutor approved failed");
                                }
                            }

                            @Override
                            public void onFailure(Call<Tutor> call, Throwable throwable) {
                                Log.d("AdminTutorsFragment", "onFailure: " + throwable.getMessage());
                                Utility.showToast(requireContext(), "Tutor approved failed");
                            }
                        });
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    @Override
    public void onRejectClick(Tutor tutor) {
        Utility.showConfirmationDialog(requireContext(),
                "Reject Tutor",
                "Are you sure you want to reject this tutor?",
                "Yes", "No",
                new Utility.OnDialogWarningCallback() {
                    @Override
                    public void onConfirm() {
                        Utility.showInputReasonDialog(requireContext(),
                                "Reject Tutor",
                                "Please provide a reason for rejecting this tutor",
                                "Reject", "Cancel",
                                new Utility.InputReasonCallback() {
                                    @Override
                                    public void onConfirm(String reason) {
                                        binding.progressBar.setVisibility(View.VISIBLE);
                                        ReasonRequest reasonRequest = new ReasonRequest(null, tutor.get_id(), reason);
                                        handleRejectTutor(tutor);
                                        createReason(reasonRequest);
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
    public void onTutorDetailClick(Tutor tutor) {
        Utility.showTutorDetailDialog(requireContext(), tutor);
    }

    private void handleRejectTutor(Tutor tutor) {
        adminService.updateTutorStatus(token, tutor.get_id(), new StatusRequest(Constants.TUTOR_STATUS_REJECTED)).enqueue(new Callback<Tutor>() {
            @Override
            public void onResponse(Call<Tutor> call, Response<Tutor> response) {
                if (response.isSuccessful()) {
                    Log.d("AdminTutorsFragment", "onResponse: " + response.body());
                    Utility.showToast(requireContext(), "Tutor rejected successfully");
                    getTutors();
                } else {
                    Log.d("AdminTutorsFragment", "onResponse: " + response.code());
                    Log.d("AdminTutorsFragment", "onResponse: " + response.errorBody());
                    Utility.showToast(requireContext(), "Tutor rejected failed");
                }
            }

            @Override
            public void onFailure(Call<Tutor> call, Throwable throwable) {
                Log.d("AdminTutorsFragment", "onFailure: " + throwable.getMessage());
                Utility.showToast(requireContext(), "Tutor rejected failed");
            }
        });
    }

    private void createReason(ReasonRequest reasonRequest) {
        adminService.createReason(reasonRequest).enqueue(new Callback<ReasonRequest>() {
            @Override
            public void onResponse(Call<ReasonRequest> call, Response<ReasonRequest> response) {
                if (response.isSuccessful()) {
                    Log.d("AdminTutorsFragment", "onResponse: " + response.body());
                } else {
                    Log.d("AdminTutorsFragment", "onResponse: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ReasonRequest> call, Throwable throwable) {
                Log.d("AdminTutorsFragment", "onFailure: " + throwable.getMessage());
            }
        });
    }
}
