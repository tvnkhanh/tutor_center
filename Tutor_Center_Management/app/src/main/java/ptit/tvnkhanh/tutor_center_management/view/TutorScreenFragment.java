package ptit.tvnkhanh.tutor_center_management.view;

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

import java.util.List;

import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.adapter.TutorAdapter;
import ptit.tvnkhanh.tutor_center_management.callback.OnNavigationListener;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentTutorScreenBinding;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.common.TutorService;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TutorScreenFragment extends Fragment implements TutorAdapter.OnTutorClickListener {

    private FragmentTutorScreenBinding binding;
    private TutorAdapter adapter;
    private TutorService tutorService;
    private List<Tutor> tutors;
    private String clientId;
    private OnNavigationListener navigationListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTutorScreenBinding.inflate(getLayoutInflater());
        clientId = UserSession.getInstance().getAccount().getClientId();
        getTutorsData();
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

    private void getTutorsData() {
        tutorService = RetrofitClient.getRetrofitInstance().create(TutorService.class);
        String token = SharedPreferencesUtility.getString(requireContext(), Constants.X_AUTH_TOKEN, "");
        binding.progressBar.setVisibility(View.VISIBLE);
        if (token != null && !token.isEmpty()) {
            tutorService.getAllTutors(token).enqueue(new Callback<List<Tutor>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<List<Tutor>> call, Response<List<Tutor>> response) {
                    if (response.isSuccessful()) {
                        tutors = response.body();
                        if (tutors != null) {
                            tutors.removeIf(tutor -> !tutor.getStatus().equals(Constants.TUTOR_STATUS_APPROVED));
                            adapter = new TutorAdapter(tutors, requireContext(), clientId != null && !clientId.isEmpty(), TutorScreenFragment.this);
                            requireActivity().runOnUiThread(() -> {
                                adapter.notifyDataSetChanged();
                                initUI();
                            });
                        } else {
                            Log.d("TutorScreenFragment", "Tutors data is null");
                        }
                    } else {
                        Log.d("TutorScreenFragment", "Failure: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<Tutor>> call, Throwable throwable) {
                    Log.d("TutorScreenFragment", "Error: " + throwable.getMessage());
                }
            });
        }
    }

    @Override
    public void onHireClicked() {
        navigationListener.setOnNavigationDestination(Constants.NAVIGATION_HIRE_TUTOR_DETAIL);
    }

    @Override
    public void onTutorItemClicked(Tutor tutor) {
        Utility.showTutorDetailDialog(requireContext(), tutor);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utility.hideTutorDetailDialog();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.hideTutorDetailDialog();
    }
}