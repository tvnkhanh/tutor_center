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

import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.adapter.ClassAdapter;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentClassScreenBinding;
import ptit.tvnkhanh.tutor_center_management.models.TutoringClass;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.common.ClassService;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassScreenFragment extends Fragment implements ClassAdapter.OnRegisterClickListener {

    private FragmentClassScreenBinding binding;
    private ClassAdapter adapter;
    private List<TutoringClass> classes;
    private ClassService classService;
    private String token;
    private String tutorId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentClassScreenBinding.inflate(getLayoutInflater());
        classService = RetrofitClient.getRetrofitInstance().create(ClassService.class);
        token = SharedPreferencesUtility.getString(requireContext(), Constants.X_AUTH_TOKEN, "");
        tutorId = UserSession.getInstance().getAccount().getTutorId();
        initUI();
        getClassesData();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utility.hideClassDetailDialog();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utility.hideClassDetailDialog();
    }

    private void initUI() {
        binding.progressBar.setVisibility(View.GONE);
        binding.spinner.setData(Constants.CLASS_STATUS_LIST_USER);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAndSortClasses(binding.spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where no item is selected
            }
        });

        RecyclerView recyclerView = binding.rvClassContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClassAdapter(new ArrayList<>(), requireContext(), tutorId != null && !tutorId.isEmpty(), ClassScreenFragment.this);
        recyclerView.setAdapter(adapter);
    }

    private void getClassesData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        if (token != null && !token.isEmpty()) {
            classService.getClasses(token).enqueue(new Callback<List<TutoringClass>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<List<TutoringClass>> call, Response<List<TutoringClass>> response) {
                    binding.progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful()) {
                        classes = response.body();
                        if (classes != null) {
                            filterAndSortClasses(binding.spinner.getSelectedItem().toString());
                        } else {
                            Log.d("ClassScreenFragment", "Classes data is null");
                        }
                    } else {
                        Log.d("ClassScreenFragment", "Failure: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<TutoringClass>> call, Throwable throwable) {
                    binding.progressBar.setVisibility(View.GONE);
                    Log.d("ClassScreenFragment", "Error: " + throwable.getMessage());
                }
            });
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void filterAndSortClasses(String selectedStatus) {
        if (classes != null) {
            List<TutoringClass> filteredClasses = new ArrayList<>();
            for (TutoringClass tutoringClass : classes) {
                if (tutoringClass.getStatus().equals(selectedStatus)) {
                    filteredClasses.add(tutoringClass);
                }
            }

            filteredClasses.sort((class1, class2) -> {
                if (class1.getUpdateDate() == null) return 1;
                if (class2.getUpdateDate() == null) return -1;
                return class1.getUpdateDate().compareTo(class2.getUpdateDate());
            });

            adapter.updateData(filteredClasses);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRegisterClick(String classId) {
        Utility.showConfirmationDialog(requireContext(),
                "Register class",
                "Are you sure you want to register this class?",
                "Yes", "No",
                new Utility.OnDialogWarningCallback() {
                    @Override
                    public void onConfirm() {
                        tutorRegister(classId);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    @Override
    public void onClassItemClicked(TutoringClass tutoringClass) {
        Utility.showClassDetailDialog(requireContext(), tutoringClass);
    }

    @Override
    public void onEditClick(TutoringClass tutoringClass) {

    }

    private void tutorRegister(String classId) {
        classService.registerTutor(token, classId, tutorId).enqueue(new Callback<TutoringClass>() {
            @Override
            public void onResponse(Call<TutoringClass> call, Response<TutoringClass> response) {
                if (response.isSuccessful()) {
                    TutoringClass tutoringClass = response.body();
                    if (tutoringClass != null) {
                        Log.d("ClassScreenFragment", "Tutor registered successfully");
                        Utility.showToast(requireContext(), "Tutor register successful!");
                        getClassesData();
                    } else {
                        Log.d("ClassScreenFragment", "Tutor registration failed");
                        Utility.showToast(requireContext(), "Something went wrong!");
                    }
                } else {
                    Log.d("ClassScreenFragment", "Tutor registration failed with code: " + response.code());
                    if (response.code() == 404) {
                        Utility.showToast(requireContext(), "Class not found");
                    } else if (response.code() == 400) {
                        Utility.showToast(requireContext(), "Already register!");
                    } else if (response.code() == 200) {
                        Utility.showToast(requireContext(), "Tutor register successful!");
                    } else {
                        Utility.showToast(requireContext(), "Something went wrong!");
                    }
                }
            }

            @Override
            public void onFailure(Call<TutoringClass> call, Throwable throwable) {
                Log.d("ClassScreenFragment", "Tutor registration failed with error: " + throwable.getMessage());
            }
        });
    }
}