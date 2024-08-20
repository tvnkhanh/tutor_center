package ptit.tvnkhanh.tutor_center_management.view;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import ptit.tvnkhanh.tutor_center_management.adapter.AdminClassAdapter;
import ptit.tvnkhanh.tutor_center_management.adapter.AdminTutorAdapter;
import ptit.tvnkhanh.tutor_center_management.adapter.ClassAdapter;
import ptit.tvnkhanh.tutor_center_management.adapter.TutorAdapter;
import ptit.tvnkhanh.tutor_center_management.callback.OnNavigationListener;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentSearchScreenBinding;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.models.TutoringClass;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.admin.AdminService;
import ptit.tvnkhanh.tutor_center_management.services.common.ClassService;
import ptit.tvnkhanh.tutor_center_management.services.common.TutorService;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchScreenFragment extends Fragment {
    private FragmentSearchScreenBinding binding;
    private AdminService adminService;
    private ClassService classService;
    private TutorService tutorService;
    private List<TutoringClass> classes;
    private List<Tutor> tutors;
    private UserSession userSession;
    private OnNavigationListener navigationListener;
    private String token;
    private String selectedItem = Constants.SEARCH_TYPE_CLASS;
    private Handler handler = new Handler();
    private Runnable searchRunnable = new Runnable() {
        @Override
        public void run() {
            String searchText = binding.edtSearch.getText().toString();
            search(searchText);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchScreenBinding.inflate(getLayoutInflater());
        token = SharedPreferencesUtility.getString(requireContext(), Constants.X_AUTH_TOKEN, "");
        userSession = UserSession.getInstance();
        adminService = RetrofitClient.getRetrofitInstance().create(AdminService.class);
        classService = RetrofitClient.getRetrofitInstance().create(ClassService.class);
        tutorService = RetrofitClient.getRetrofitInstance().create(TutorService.class);
        initUI();
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        navigationListener = (OnNavigationListener) context;
    }

    private void initUI() {
        binding.tvSearchHeader.setText(getString(R.string.search_screen_type, selectedItem));
        initSpinnerData();
        binding.spinner.setSelection(0);
        binding.llFilter.setOnClickListener(view -> {
            Utility.hideKeyboard(requireActivity());
            Utility.clearEditTextFocus(requireActivity());
            Utility.showItemDialog(requireActivity(), Constants.SEARCH_TYPE_LIST, position -> {
                selectedItem = Constants.SEARCH_TYPE_LIST.get(position);
                binding.tvSearchHeader.setText(getString(R.string.search_screen_type, selectedItem));
                initSpinnerData();
                Log.d("selectedItem", selectedItem);
                search(binding.edtSearch.getText().toString());
            }, "type", Constants.SEARCH_TYPE_LIST.indexOf(selectedItem));
        });

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((selectedItem.equals(Constants.SEARCH_TYPE_CLASS) && classes != null) ||
                        (selectedItem.equals(Constants.SEARCH_TYPE_TUTOR) && tutors != null)) {
                    filterResultsByStatus(binding.spinner.getSelectedItem().toString());
                } else {
                    Log.d("SearchScreenFragment", "Classes or Tutors list is null");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacks(searchRunnable);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                handler.postDelayed(searchRunnable, 1000);
            }
        });
    }

    private void initSpinnerData() {
        if (selectedItem.equals(Constants.SEARCH_TYPE_CLASS) && (Objects.equals(userSession.getAccount().getRoleId(), Constants.ROLE_ADMIN_ID))) {
            binding.spinner.setData(Constants.CLASS_STATUS_LIST_REVERSE);
        } else if (selectedItem.equals(Constants.SEARCH_TYPE_TUTOR) && (Objects.equals(userSession.getAccount().getRoleId(), Constants.ROLE_ADMIN_ID))) {
            binding.spinner.setData(Constants.TUTOR_STATUS_LIST);
        } else if (selectedItem.equals(Constants.SEARCH_TYPE_TUTOR) && !(Objects.equals(userSession.getAccount().getRoleId(), Constants.ROLE_ADMIN_ID))) {
            binding.spinner.setVisibility(View.GONE);
        } else if (selectedItem.equals(Constants.SEARCH_TYPE_CLASS) && !(Objects.equals(userSession.getAccount().getRoleId(), Constants.ROLE_ADMIN_ID))) {
            binding.spinner.setData(Constants.CLASS_STATUS_LIST_USER);
        }
    }

    private void onSearchDone() {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void updateRecyclerViewWithClasses(List<TutoringClass> filteredClasses) {
        RecyclerView recyclerView = binding.rvContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (Objects.equals(userSession.getAccount().getRoleId(), Constants.ROLE_ADMIN_ID)) {
            recyclerView.setAdapter(new AdminClassAdapter(requireContext(), filteredClasses, new AdminClassAdapter.OnAdminClassClickListener() {
                @Override
                public void onChangeStatusClick(TutoringClass classItem) {

                }

                @Override
                public void onRejectedClick(TutoringClass classItem) {

                }

                @Override
                public void onClassItemDetailClick(TutoringClass classItem) {

                }

                @Override
                public void onEditClassClick(TutoringClass classItem) {

                }
            }));
        } else {
            recyclerView.setAdapter(new ClassAdapter(filteredClasses, requireContext(), Objects.equals(userSession.getAccount().getRoleId(), Constants.ROLE_TUTOR_ID), new ClassAdapter.OnRegisterClickListener() {
                @Override
                public void onRegisterClick(String classId) {

                }

                @Override
                public void onClassItemClicked(TutoringClass tutoringClass) {

                }

                @Override
                public void onEditClick(TutoringClass tutoringClass) {

                }
            }));
        }
    }

    private void updateRecyclerViewWithTutors(List<Tutor> filteredTutors) {
        RecyclerView recyclerView = binding.rvContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (Objects.equals(userSession.getAccount().getRoleId(), Constants.ROLE_ADMIN_ID)) {
            recyclerView.setAdapter(new AdminTutorAdapter(filteredTutors, requireContext(), new AdminTutorAdapter.OnTutorItemClickListener() {
                @Override
                public void onApproveClick(Tutor tutor) {

                }

                @Override
                public void onRejectClick(Tutor tutor) {

                }

                @Override
                public void onTutorDetailClick(Tutor tutor) {

                }
            }));
        } else {
            recyclerView.setAdapter(new TutorAdapter(filteredTutors, requireContext(), Objects.equals(userSession.getAccount().getRoleId(), Constants.ROLE_CLIENT_ID), new TutorAdapter.OnTutorClickListener() {
                @Override
                public void onHireClicked() {

                }

                @Override
                public void onTutorItemClicked(Tutor tutor) {

                }
            }));
        }
    }

    private void search(String searchText) {
        binding.progressBar.setVisibility(View.VISIBLE);
        if (token != null && !token.isEmpty()) {
            if (selectedItem.equals(Constants.SEARCH_TYPE_CLASS)) {
                classService.searchClass(token, searchText).enqueue(new Callback<List<TutoringClass>>() {
                    @Override
                    public void onResponse(Call<List<TutoringClass>> call, Response<List<TutoringClass>> response) {
                        if (response.isSuccessful()) {
                            classes = response.body();
                            if (classes != null) {
                                Log.d("classes", classes.toString());
                                filterResultsByStatus(binding.spinner.getSelectedItem().toString());
                                onSearchDone();
                            }
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<List<TutoringClass>> call, Throwable throwable) {
                        binding.progressBar.setVisibility(View.GONE);
                        // Handle failure
                    }
                });
            } else if (selectedItem.equals(Constants.SEARCH_TYPE_TUTOR)) {
                tutorService.searchTutor(token, searchText).enqueue(new Callback<List<Tutor>>() {
                    @Override
                    public void onResponse(Call<List<Tutor>> call, Response<List<Tutor>> response) {
                        if (response.isSuccessful()) {
                            tutors = response.body();
                            if (tutors != null) {
                                Log.d("tutors", tutors.toString());
                                filterResultsByStatus(binding.spinner.getSelectedItem().toString());
                                onSearchDone();
                            }
                        } else {
                            Log.d("error", response.message());
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<List<Tutor>> call, Throwable throwable) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.d("error", throwable.getMessage());
                    }
                });
            }
        }
    }

    private void tutorRegister(String classId) {
        String tutorId = UserSession.getInstance().getAccount().getTutorId();
        if (tutorId != null && !tutorId.isEmpty()) {
            classService.registerTutor(token, classId, tutorId).enqueue(new Callback<TutoringClass>() {
                @Override
                public void onResponse(Call<TutoringClass> call, Response<TutoringClass> response) {
                    if (response.isSuccessful()) {
                        TutoringClass tutoringClass = response.body();
                        if (tutoringClass != null) {
                            Log.d("SearchScreenFragment", "Tutor registered successfully");
                            Utility.showToast(requireContext(), "Tutor registered successfully");
                        } else {
                            Log.d("SearchScreenFragment", "Tutor registration failed");
                            Utility.showToast(requireContext(), "Tutor registration failed");
                        }
                    } else {
                        Log.d("SearchScreenFragment", "Tutor registration failed with code: " + response.code());
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
                    Log.d("SearchScreenFragment", "Tutor registration failed with error: " + throwable.getMessage());
                }
            });
        }
    }

    private void filterResultsByStatus(String status) {
        if (selectedItem.equals(Constants.SEARCH_TYPE_CLASS) && classes != null) {
            List<TutoringClass> filteredClasses = new ArrayList<>();
            for (TutoringClass tutoringClass : classes) {
                if (tutoringClass.getStatus().equals(status)) {
                    filteredClasses.add(tutoringClass);
                }
            }
            updateRecyclerViewWithClasses(filteredClasses);
        } else if (selectedItem.equals(Constants.SEARCH_TYPE_TUTOR) && tutors != null) {
            List<Tutor> filteredTutors = new ArrayList<>();
            for (Tutor tutor : tutors) {
                if (tutor.getStatus().equals(status)) {
                    filteredTutors.add(tutor);
                }
            }
            updateRecyclerViewWithTutors(filteredTutors);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(searchRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(searchRunnable);
    }
}