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

import java.util.List;

import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.adapter.TutorAdapter;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentTutorScreenBinding;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.common.TutorService;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TutorScreenFragment extends Fragment {

    private FragmentTutorScreenBinding binding;
    private TutorAdapter adapter;
    private TutorService tutorService;
    private List<Tutor> tutors;
    private String clientId;

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

    private void initUI() {
        binding.progressBar.setVisibility(View.GONE);
        RecyclerView recyclerView = binding.rvTutorContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TutorAdapter(tutors, requireContext(), clientId != null && !clientId.isEmpty());
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
                            adapter = new TutorAdapter(tutors, requireContext(), clientId != null && !clientId.isEmpty());
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
}