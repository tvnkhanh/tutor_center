package ptit.tvnkhanh.tutor_center_management.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.adapter.ItemAdapter;
import ptit.tvnkhanh.tutor_center_management.databinding.DialogListBinding;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentHireTutorBinding;
import ptit.tvnkhanh.tutor_center_management.models.Subject;
import ptit.tvnkhanh.tutor_center_management.models.TutoringClass;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.common.ClassService;
import ptit.tvnkhanh.tutor_center_management.services.common.SubjectService;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HireTutorFragment extends Fragment {

    private FragmentHireTutorBinding binding;
    private DialogListBinding dialogBinding;
    private SubjectService subjectService;
    private ClassService classService;
    private String token;
    private List<String> itemList = new ArrayList<>();
    private List<String> subjectId = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHireTutorBinding.inflate(getLayoutInflater());
        token = SharedPreferencesUtility.getString(requireContext(), Constants.X_AUTH_TOKEN, "");
        initUI();
        return binding.getRoot();
    }

    private void initUI() {
        binding.edtSubject.setOnClickListener(view -> {
            getSubjectData();
        });
        binding.edtForm.setOnClickListener(view -> {
            showItemDialog(Constants.CLASS_FORM_LIST, position -> {
                String selectedItem = Constants.CLASS_FORM_LIST.get(position);
                binding.edtForm.setText(selectedItem);
            }, "form");
        });
        binding.btnSubmit.setOnClickListener(view -> {
            sendHireTutorRequest();
        });
    }

    private void getSubjectData() {
        subjectService = RetrofitClient.getRetrofitInstance().create(SubjectService.class);
        if (token != null && !token.isEmpty()) {
            subjectService.getSubjects(token).enqueue(new Callback<List<Subject>>() {
                @Override
                public void onResponse(Call<List<Subject>> call, Response<List<Subject>> response) {
                    if (response.isSuccessful()) {
                        List<Subject> subjects = response.body();
                        itemList.clear();
                        if (subjects != null) {
                            for (int i = 0; i < subjects.size(); i++) {
                                String subjectName = subjects.get(i).getSubjectName() + " - " + subjects.get(i).getGrade();
                                itemList.add(subjectName);
                            }
                            showItemDialog(itemList, position -> {
                                String selectedItem = itemList.get(position);
                                binding.edtSubject.setText(getString(R.string.hire_tutor_screen_edt_hint_5, selectedItem));
                                subjectId.add(subjects.get(position).get_id());
                            }, "subject");
                        } else {
                            Log.d("getSubjectData", "subjects is null");
                        }
                    } else {
                        Log.d("getSubjectData", "onResponse: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<Subject>> call, Throwable throwable) {
                    Log.d("getSubjectData", "onFailure: " + throwable.getMessage());
                }
            });
        }
    }

    private void showItemDialog(List<String> itemList, ItemAdapter.OnItemClickListener listener, String title) {
        dialogBinding = DialogListBinding.inflate(getLayoutInflater());
        RecyclerView recyclerView = dialogBinding.rvContainer;

        ItemAdapter adapter = new ItemAdapter(requireContext(), itemList, listener);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        new AlertDialog.Builder(requireContext())
                .setTitle("Select " + title)
                .setView(dialogBinding.getRoot())
                .show();
    }

    private void sendHireTutorRequest() {
        classService = RetrofitClient.getRetrofitInstance().create(ClassService.class);
        if (token != null && !token.isEmpty()) {
            TutoringClass tutoringClass = new TutoringClass();
            if (!binding.edtStudentInfo.getText().toString().isEmpty() && !binding.edtForm.getText().toString().isEmpty()
                    && !binding.edtSubject.getText().toString().isEmpty() && !binding.edtSalary.getText().toString().isEmpty()
                && !binding.edtAddress.getText().toString().isEmpty()) {
                tutoringClass.setStudentInfo(binding.edtStudentInfo.getText().toString());
                tutoringClass.setSalary(binding.edtSalary.getText().toString());
                tutoringClass.setForm(binding.edtForm.getText().toString());
                tutoringClass.setAddress(binding.edtAddress.getText().toString());
                tutoringClass.setSubjects(subjectId);
                tutoringClass.setRequirement(binding.edtRequirement.getText().toString());

                classService.createClass(token, tutoringClass).enqueue(new Callback<TutoringClass>() {
                    @Override
                    public void onResponse(Call<TutoringClass> call, Response<TutoringClass> response) {
                        if (response.isSuccessful()) {
                            TutoringClass tutoringClass = response.body();
                            if (tutoringClass != null) {
                                Log.d("sendHireTutorRequest", "tutoringClass: " + tutoringClass.toString());
                                Utility.showToast(requireContext(), "Send hire tutor request successfully");
                            } else {
                                Log.d("sendHireTutorRequest", "tutoringClass is null");
                                Utility.showToast(requireContext(), "Send hire tutor request failed");
                            }
                        } else {
                            Log.d("sendHireTutorRequest", "onResponse: " + response.message());
                            Utility.showToast(requireContext(), "Send hire tutor request failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<TutoringClass> call, Throwable throwable) {
                        Log.d("sendHireTutorRequest", "onFailure: " + throwable.getMessage());
                    }
                });
            } else {
                Log.d("sendHireTutorRequest", "Invalid input");
                Utility.showToast(requireContext(), "Invalid input or empty field");
            }
        }
    }
}