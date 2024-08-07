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
import ptit.tvnkhanh.tutor_center_management.services.admin.models.StatusRequest;
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
        binding.llFilter.setOnClickListener(view -> {
            Utility.hideKeyboard(requireActivity());
            Utility.clearEditTextFocus(requireActivity());
            Utility.showItemDialog(requireActivity(), Constants.SEARCH_TYPE_LIST, position -> {
                selectedItem = Constants.SEARCH_TYPE_LIST.get(position);
                binding.tvSearchHeader.setText(getString(R.string.search_screen_type, selectedItem));
                Log.d("selectedItem", selectedItem);
            }, "type", Constants.SEARCH_TYPE_LIST.indexOf(selectedItem));
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

    private void initRecyclerView() {
        binding.progressBar.setVisibility(View.GONE);
        RecyclerView recyclerView = binding.rvContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (selectedItem.equals(Constants.SEARCH_TYPE_CLASS)) {
            classes.sort((o1, o2) -> {
                int index1 = Constants.CLASS_STATUS_LIST_REVERSE.indexOf(o1.getStatus());
                int index2 = Constants.CLASS_STATUS_LIST_REVERSE.indexOf(o2.getStatus());
                return Integer.compare(index1, index2);
            });
            classes.removeIf(tutoringClass -> tutoringClass.getStatus().equals(Constants.CLASS_STATUS_COMPLETED) || tutoringClass.getStatus().equals(Constants.CLASS_STATUS_REJECTED));
            if (!Objects.equals(userSession.getAccount().getRoleId(), Constants.ROLE_ADMIN_ID)) {
                recyclerView.setAdapter(new ClassAdapter(classes, requireContext(), Objects.equals(userSession.getAccount().getRoleId(), Constants.ROLE_TUTOR_ID), new ClassAdapter.OnRegisterClickListener() {
                    @Override
                    public void onRegisterClick(String classId) {
                        Utility.showConfirmationDialog(requireContext(),
                                "Register class",
                                "Are you sure you want to register this class?",
                                "Register", "Cancel",
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
                }));
            } else {
                recyclerView.setAdapter(new AdminClassAdapter(requireContext(), classes, new AdminClassAdapter.OnAdminClassClickListener() {
                    @Override
                    public void onChangeStatusClick(TutoringClass classItem) {
                        Utility.showConfirmationDialog(requireContext(),
                                "Change status",
                                "Are you sure you want to change status of this class?",
                                "Yes", "No",
                                new Utility.OnDialogWarningCallback() {
                                    @Override
                                    public void onConfirm() {
                                        if (!classItem.getTutorId().isEmpty()) {
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
                                                        
                                                    } else {
                                                        Log.d("AdminClassesFragment", "onResponse: " + response.code());
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<TutoringClass> call, Throwable throwable) {
                                                    Log.d("AdminClassesFragment", "onFailure: " + throwable.getMessage());
                                                }
                                            });
                                        } else {
                                            Utility.showToast(requireContext(), "This class is not assigned to any tutor");
                                        }
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });
                    }

                    @Override
                    public void onRejectedClick(TutoringClass classItem) {

                    }

                    @Override
                    public void onClassItemDetailClick(TutoringClass classItem) {
                        Utility.showClassDetailDialog(requireContext(), classItem);
                    }

                    @Override
                    public void onEditClassClick(TutoringClass classItem) {

                    }
                }));
            }
        } else {
            if (!Objects.equals(userSession.getAccount().getRoleId(), Constants.ROLE_ADMIN_ID)) {
                tutors.removeIf(tutor -> tutor.getStatus().equals(Constants.TUTOR_STATUS_PENDING));
                recyclerView.setAdapter(new TutorAdapter(tutors, requireContext(), Objects.equals(userSession.getAccount().getRoleId(), Constants.ROLE_CLIENT_ID), new TutorAdapter.OnTutorClickListener() {
                    @Override
                    public void onHireClicked() {
                        navigationListener.setOnNavigationDestination(Constants.NAVIGATION_HIRE_TUTOR_DETAIL);
                    }

                    @Override
                    public void onTutorItemClicked(Tutor tutor) {
                        Utility.showTutorDetailDialog(requireContext(), tutor);
                    }
                }));
            } else {
                tutors.removeIf(tutor -> tutor.getStatus().equals(Constants.TUTOR_STATUS_APPROVED));
                recyclerView.setAdapter(new AdminTutorAdapter(tutors, requireContext(), new AdminTutorAdapter.OnTutorItemClickListener() {
                    @Override
                    public void onApproveClick(Tutor tutor) {
                        Utility.showConfirmationDialog(requireContext(),
                                "Approve tutor",
                                "Are you sure you want to approve this tutor?",
                                "Approve", "Cancel",
                                new Utility.OnDialogWarningCallback() {
                                    @Override
                                    public void onConfirm() {
                                        adminService.updateTutorStatus(token, tutor.get_id(), new StatusRequest(Constants.TUTOR_STATUS_APPROVED)).enqueue(new Callback<Tutor>() {
                                            @Override
                                            public void onResponse(Call<Tutor> call, Response<Tutor> response) {
                                                if (response.isSuccessful()) {
                                                    Log.d("AdminTutorsFragment", "onResponse: " + response.body());
                                                    tutors.remove(tutor);
                                                    recyclerView.getAdapter().notifyDataSetChanged();
                                                    Utility.showToast(requireContext(), "Approve successfully!");
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
                                    public void onCancel() {

                                    }
                                });
                    }

                    @Override
                    public void onRejectClick(Tutor tutor) {
                        Utility.showConfirmationDialog(requireContext(),
                                "Reject tutor",
                                "Are you sure you want to reject this tutor?",
                                "Reject", "Cancel",
                                new Utility.OnDialogWarningCallback() {
                                    @Override
                                    public void onConfirm() {
                                        adminService.updateTutorStatus(token, tutor.get_id(), new StatusRequest(Constants.TUTOR_STATUS_REJECTED)).enqueue(new Callback<Tutor>() {
                                            @Override
                                            public void onResponse(Call<Tutor> call, Response<Tutor> response) {
                                                if (response.isSuccessful()) {
                                                    Log.d("AdminTutorsFragment", "onResponse: " + response.body());
                                                    recyclerView.getAdapter().notifyDataSetChanged();
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
                                    public void onCancel() {

                                    }
                                });
                    }

                    @Override
                    public void onTutorDetailClick(Tutor tutor) {
                        Utility.showTutorDetailDialog(requireContext(), tutor);
                    }
                }));
            }
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
                                initRecyclerView();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TutoringClass>> call, Throwable throwable) {

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
                                initRecyclerView();
                            } else {
                                Log.d("tutors", "null");
                            }
                        } else {
                            Log.d("error", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Tutor>> call, Throwable throwable) {
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