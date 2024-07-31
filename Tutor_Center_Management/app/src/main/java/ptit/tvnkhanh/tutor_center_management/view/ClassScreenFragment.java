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
import ptit.tvnkhanh.tutor_center_management.adapter.ClassAdapter;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentClassScreenBinding;
import ptit.tvnkhanh.tutor_center_management.models.TutoringClass;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.common.ClassService;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassScreenFragment extends Fragment {

    private FragmentClassScreenBinding binding;
    private ClassAdapter adapter;
    private List<TutoringClass> classes;
    private ClassService classService;
    private String tutorId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentClassScreenBinding.inflate(getLayoutInflater());
        tutorId = UserSession.getInstance().getAccount().getTutorId();
        getClassesData();
        return binding.getRoot();
    }

    private void initUI() {
        binding.progressBar.setVisibility(View.GONE);
        RecyclerView recyclerView = binding.rvClassContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClassAdapter(classes, requireContext(), tutorId != null && !tutorId.isEmpty());
        recyclerView.setAdapter(adapter);
    }

    private void getClassesData() {
        classService = RetrofitClient.getRetrofitInstance().create(ClassService.class);
        String token = SharedPreferencesUtility.getString(requireContext(), Constants.X_AUTH_TOKEN, "");
        binding.progressBar.setVisibility(View.VISIBLE);
        if (token != null && !token.isEmpty()) {
            classService.getClasses(token).enqueue(new Callback<List<TutoringClass>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<List<TutoringClass>> call, Response<List<TutoringClass>> response) {
                    if (response.isSuccessful()) {
                        classes = response.body();
                        if (classes != null) {
                            adapter = new ClassAdapter(classes, requireContext(), tutorId != null && !tutorId.isEmpty());
                            requireActivity().runOnUiThread(() -> {
                                adapter.notifyDataSetChanged();
                                initUI();
                            });
                        } else {
                            Log.d("ClassScreenFragment", "Classes data is null");
                        }
                    } else {
                        Log.d("ClassScreenFragment", "Failure: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<TutoringClass>> call, Throwable throwable) {
                    Log.d("ClassScreenFragment", "Error: " + throwable.getMessage());
                }
            });
        }
    }
}