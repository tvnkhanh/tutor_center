package ptit.tvnkhanh.tutor_center_management.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.adapter.ItemAdapter;
import ptit.tvnkhanh.tutor_center_management.databinding.DialogClassDetailBinding;
import ptit.tvnkhanh.tutor_center_management.databinding.DialogListBinding;
import ptit.tvnkhanh.tutor_center_management.databinding.DialogTutorDetailBinding;
import ptit.tvnkhanh.tutor_center_management.databinding.DialogUpdateClassBinding;
import ptit.tvnkhanh.tutor_center_management.models.Client;
import ptit.tvnkhanh.tutor_center_management.models.Subject;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.models.TutoringClass;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.admin.AdminService;
import ptit.tvnkhanh.tutor_center_management.services.common.ClientService;
import ptit.tvnkhanh.tutor_center_management.services.common.SubjectService;
import ptit.tvnkhanh.tutor_center_management.services.common.TutorService;
import ptit.tvnkhanh.tutor_center_management.services.common.models.ReasonRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utility {

    private static final String INPUT_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
    private static final String OUTPUT_DATE_FORMAT = "dd/MM/yyyy";
    private static Dialog tutorDetailDialog;
    private static Dialog classDetailDialog;
    private static DialogListBinding dialogItemListBinding;
    private static Dialog warningDialog;
    private static final Map<String, String> reasonsCache = new HashMap<>();
    private static List<Subject> subjectsCache = new ArrayList<>();
    private static boolean isSubjectsLoaded = false;

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static float dpToPx(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return dp * displayMetrics.density;
    }

    public static SpannableString boldText(String text) {
        int startIndex = text.indexOf('#');
        int endIndex = text.indexOf('#', startIndex + 1);

        if (startIndex == -1 || endIndex == -1 || startIndex == endIndex) {
            return new SpannableString(text);
        }

        SpannableString spannableString = new SpannableString(text.replace("#", ""));
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    public static String convertDateFormat(long inputDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputDateFormat = new SimpleDateFormat(OUTPUT_DATE_FORMAT);
        Date date = new Date(inputDate);
        return outputDateFormat.format(date);
    }

    public static String formatNumber(int number) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(number).replace(",", ".");
    }

    public static String formatNumber(double number) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        return formatter.format(number);
    }

    public static void showTutorDetailDialog(Context context, Tutor tutor) {
        UserSession userSession = UserSession.getInstance();
        tutorDetailDialog = new Dialog(context, R.style.TransparentDialog);
        DialogTutorDetailBinding binding = DialogTutorDetailBinding.inflate(LayoutInflater.from(context));
        tutorDetailDialog.setContentView(binding.getRoot());
        Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar))
                .load(tutor.getPortraitPhotos().get(0))
                .into(binding.ivAvatar);
        List<String> subjects = new ArrayList<>();
        for (int i = 0; i < tutor.getSubjects().size(); i++) {
            String subjectName = tutor.getSubjects().get(i).getSubjectName();
            subjects.add(subjectName);
        }
        String subjectsString = String.join(", ", subjects);
        binding.setIsAdmin(Objects.equals(userSession.getAccount().getRoleId(), Constants.ROLE_ADMIN_ID));
        binding.tvTutorId.setText(context.getString(R.string.detail_tutor_screen_tutor_id, tutor.get_id()));
        binding.tvName.setText(context.getString(R.string.detail_tutor_screen_name, tutor.getFirstName() + " " + tutor.getLastName()));
        binding.tvGender.setText(context.getString(R.string.detail_tutor_screen_gender, tutor.getGender()));
        binding.tvDateOfBirth.setText(context.getString(R.string.detail_tutor_screen_date_of_birth, convertDateFormat(tutor.getDateOfBirth().getTime())));
        binding.tvContact.setText(context.getString(R.string.detail_tutor_screen_contact, tutor.getContactNumber()));
        binding.tvEmail.setText(context.getString(R.string.detail_tutor_screen_email, tutor.getEmail()));
        binding.tvAddress.setText(context.getString(R.string.detail_tutor_screen_address, tutor.getAddress()));
        binding.tvCid.setText(context.getString(R.string.detail_tutor_screen_cid, tutor.getCitizenId()));
        binding.tvSubjects.setText(context.getString(R.string.detail_tutor_screen_subjects, subjectsString));
        binding.tvTeachingTime.setText(context.getString(R.string.detail_tutor_screen_teaching_time, tutor.getTeachingTime()));
        binding.tvGraduationYear.setText(context.getString(R.string.detail_tutor_screen_graduation_year, String.valueOf(tutor.getGraduationYear())));
        Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_qualification)
                        .error(R.drawable.ic_qualification))
                .load(tutor.getQualification().get(0))
                .into(binding.ivQualification);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(tutorDetailDialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9);
        tutorDetailDialog.getWindow().setAttributes(layoutParams);

        tutorDetailDialog.show();
    }

    public static void hideTutorDetailDialog() {
        if (tutorDetailDialog != null) {
            if (tutorDetailDialog.isShowing()) {
                tutorDetailDialog.cancel();
                tutorDetailDialog.dismiss();
            }
            tutorDetailDialog = null;
        }
    }

    public static void showClassDetailDialog(Context context, TutoringClass tutoringClass) {
        UserSession userSession = UserSession.getInstance();
        TutorService tutorService = RetrofitClient.getRetrofitInstance().create(TutorService.class);
        ClientService clientService = RetrofitClient.getRetrofitInstance().create(ClientService.class);
        String token = SharedPreferencesUtility.getString(context, Constants.X_AUTH_TOKEN, "");

        Dialog classDetailDialog = new Dialog(context, R.style.TransparentDialog);
        DialogClassDetailBinding binding = DialogClassDetailBinding.inflate(LayoutInflater.from(context));
        classDetailDialog.setContentView(binding.getRoot());

        boolean isAdmin = Objects.equals(userSession.getAccount().getRoleId(), Constants.ROLE_ADMIN_ID);
//        binding.setIsAdmin(isAdmin);

        // Default visibility settings
        binding.tvTutorName.setVisibility(View.GONE);
        binding.ivTutorImage.setVisibility(View.GONE);
        binding.tvStudentName.setVisibility(View.GONE);

        if (token != null && !token.isEmpty()) {
            if (isAdmin) {
                if (tutoringClass.getTutorId() != null && !tutoringClass.getTutorId().isEmpty() &&
                        (Objects.equals(tutoringClass.getStatus(), Constants.CLASS_STATUS_ASSIGNED) ||
                                Objects.equals(tutoringClass.getStatus(), Constants.CLASS_STATUS_COMPLETED))) {
                    tutorService.getTutorById(token, tutoringClass.getTutorId().get(0)).enqueue(new Callback<Tutor>() {
                        @Override
                        public void onResponse(Call<Tutor> call, Response<Tutor> response) {
                            if (response.isSuccessful()) {
                                Tutor tutor = response.body();
                                if (tutor != null) {
                                    binding.tvTutorName.setText(context.getString(R.string.detail_class_screen_name, tutor.getFirstName() + " " + tutor.getLastName()));
                                    Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                                                    .placeholder(R.drawable.ic_default_avatar)
                                                    .error(R.drawable.ic_default_avatar))
                                            .load(tutor.getPortraitPhotos().get(0))
                                            .into(binding.ivTutorImage);
                                    binding.tvTutorName.setVisibility(View.VISIBLE);
                                    binding.ivTutorImage.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Tutor> call, Throwable throwable) {
                            // Handle failure
                        }
                    });
                }

                if (Objects.equals(tutoringClass.getStatus(), Constants.CLASS_STATUS_ASSIGNED) ||
                        Objects.equals(tutoringClass.getStatus(), Constants.CLASS_STATUS_COMPLETED)) {
                    clientService.getClientById(token, tutoringClass.getClientId()).enqueue(new Callback<Client>() {
                        @Override
                        public void onResponse(Call<Client> call, Response<Client> response) {
                            if (response.isSuccessful()) {
                                Client client = response.body();
                                if (client != null) {
                                    binding.tvStudentName.setText(context.getString(R.string.detail_class_screen_student_name, client.getFirstName() + " " + client.getLastName()));
                                    binding.tvStudentName.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Client> call, Throwable throwable) {
                            // Handle failure
                        }
                    });
                }
            }
        }

        // Set static data
        binding.tvClassId.setText(context.getString(R.string.detail_class_id, tutoringClass.get_id()));
        binding.tvStudentInfo.setText(context.getString(R.string.detail_class_screen_student_info, tutoringClass.getStudentInfo()));
        binding.tvSchedule.setText(context.getString(R.string.detail_class_screen_schedule, tutoringClass.getSchedule()));
        binding.tvSalary.setText(context.getString(R.string.detail_class_screen_salary, formatNumber(Integer.parseInt(tutoringClass.getSalary()))));
        binding.tvForm.setText(context.getString(R.string.detail_class_screen_form, tutoringClass.getForm()));
        binding.tvAddress.setText(context.getString(R.string.detail_class_screen_address, tutoringClass.getAddress()));

        List<String> subjectsStringList = new ArrayList<>();
        for (Subject subject : tutoringClass.getSubjectDetails()) {
            subjectsStringList.add(subject.getSubjectName());
        }
        String subjects = String.join(", ", subjectsStringList);
        binding.tvSubject.setText(context.getString(R.string.detail_class_screen_subjects, subjects));
        binding.tvRequirement.setText(context.getString(R.string.detail_class_screen_requirement, tutoringClass.getRequirement()));

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(classDetailDialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9);
        classDetailDialog.getWindow().setAttributes(layoutParams);

        classDetailDialog.show();
    }


    public static void hideClassDetailDialog() {
        if (classDetailDialog != null) {
            if (classDetailDialog.isShowing()) {
                classDetailDialog.cancel();
                classDetailDialog.dismiss();
            }
            classDetailDialog = null;
        }
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showItemDialog(Activity activity, List<String> itemList, ItemAdapter.OnItemClickListener listener, String title, int currentSelectedItem) {
        dialogItemListBinding = DialogListBinding.inflate(activity.getLayoutInflater());
        RecyclerView recyclerView = dialogItemListBinding.rvContainer;

        ItemAdapter adapter = new ItemAdapter(activity, itemList, listener, currentSelectedItem);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);

        new AlertDialog.Builder(activity)
                .setTitle("Select " + title)
                .setView(dialogItemListBinding.getRoot())
                .show();
    }

    public static void showItemDialogOptions(Activity activity, List<String> itemList, ItemAdapter.OnItemClickListener listener, String title, int currentSelectedItem, final OnItemDialogCallback callback) {
        dialogItemListBinding = DialogListBinding.inflate(activity.getLayoutInflater());
        RecyclerView recyclerView = dialogItemListBinding.rvContainer;

        ItemAdapter adapter = new ItemAdapter(activity, itemList, listener, currentSelectedItem);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);

        new AlertDialog.Builder(activity)
                .setTitle("Select " + title)
                .setView(dialogItemListBinding.getRoot())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onConfirm();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onCancel();
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public interface OnItemDialogCallback {
        void onConfirm();
        void onCancel();
    }

    public static void clearEditTextFocus(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content);
        clearEditTextFocusRecursive(rootView);
    }

    private static void clearEditTextFocusRecursive(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof EditText) {
                child.clearFocus();
            } else if (child instanceof ViewGroup) {
                clearEditTextFocusRecursive((ViewGroup) child);
            }
        }
    }

    public static File getFileFromUri(Context context, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return new File(path);
        }
        return null;
    }

    public static void showConfirmationDialog(Context context, String title, String message, String positiveButtonText, String negativeButtonText, final OnDialogWarningCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                callback.onConfirm();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onCancel();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public static void showInputReasonDialog(Context context, String title, String message, String positiveButtonText, String negativeButtonText, final InputReasonCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_input_reason, null);
        final EditText inputReason = dialogView.findViewById(R.id.input_reason);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setView(dialogView);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String reason = inputReason.getText().toString().trim();
                callback.onConfirm(reason);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onCancel();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public interface OnDialogWarningCallback {
        void onConfirm();
        void onCancel();
    }

    public interface InputReasonCallback {
        void onConfirm(String reason);
        void onCancel();
    }

    public static void fetchAllReasons() {
        AdminService adminService = RetrofitClient.getRetrofitInstance().create(AdminService.class);
        Call<List<ReasonRequest>> call = adminService.getReasons();
        call.enqueue(new Callback<List<ReasonRequest>>() {
            @Override
            public void onResponse(Call<List<ReasonRequest>> call, Response<List<ReasonRequest>> response) {
                if (response.isSuccessful()) {
                    List<ReasonRequest> reasonRequests = response.body();
                    if (reasonRequests != null) {
                        for (ReasonRequest reasonRequest : reasonRequests) {
                            reasonsCache.put(reasonRequest.getClassId(), reasonRequest.getReason());
                        }
                        Log.d("Utility", "Fetched all reasons successfully.");
                    }
                } else {
                    Log.e("Utility", "Failed to fetch reasons. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ReasonRequest>> call, Throwable t) {
                Log.e("Utility", "Failed to fetch reasons: " + t.getMessage());
            }
        });
    }

    public static String getReasonByClassId(String classId) {
        return reasonsCache.getOrDefault(classId, "Reason not found");
    }

    public static void showUpdateClassDialog(Context context, TutoringClass tutoringClass,
                                             OnUpdateClassListener listener) {

        DialogUpdateClassBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.dialog_update_class, null, false);

        List<String> subjectIds = new ArrayList<>();
        List<String> itemList = new ArrayList<>();
        List<Subject> subjects = Utility.getSubjects();
        for (Subject subject : subjects) {
            itemList.add(subject.getSubjectName());
        }
        binding.edtSubject.setOnClickListener(view -> {
            Utility.showItemDialog((Activity) context, itemList, position -> {
                subjectIds.clear();
                Utility.hideKeyboard((Activity) context);
                Utility.clearEditTextFocus((Activity) context);
                String selectedItem = itemList.get(position);
                binding.edtSubject.setText(context.getString(R.string.hire_tutor_screen_edt_hint_5, selectedItem));
                subjectIds.add(subjects.get(position).get_id());
                Log.d("showUpdateClassDialog", "subjectIds: " + subjectIds);
                Log.d("showUpdateClassDialog", "selectedItem: " + selectedItem);
            }, "subject", -1);

        });

        binding.edtForm.setOnClickListener(view -> {
            Utility.hideKeyboard((Activity) context);
            Utility.clearEditTextFocus((Activity) context);
            Utility.showItemDialog((Activity) context, Constants.CLASS_FORM_LIST, position -> {
                String selectedForm = Constants.CLASS_FORM_LIST.get(position);
                binding.edtForm.setText(selectedForm);
            }, "form", Constants.CLASS_FORM_LIST.indexOf(binding.edtForm.getText().toString()));
        });

        if (tutoringClass != null) {
            List<String> subjectsList = new ArrayList<>();
            List<String> scheduleList = new ArrayList<>();
            for (Subject subject : tutoringClass.getSubjectDetails()) {
                subjectsList.add(subject.getSubjectName());
            }
            for (String schedule : tutoringClass.getSchedule()) {
                scheduleList.add(schedule);
            }
            String subjectNames = String.join(", ", subjectsList);
            String scheduleNames = String.join(", ", scheduleList);
            binding.edtStudentInfo.setText(tutoringClass.getStudentInfo());
            binding.edtSchedule.setText(scheduleNames);
            binding.edtSalary.setText(tutoringClass.getSalary());
            binding.edtForm.setText(tutoringClass.getForm());
            binding.edtAddress.setText(tutoringClass.getAddress());
            binding.edtSubject.setText(subjectNames);
            binding.edtRequirement.setText(tutoringClass.getRequirement());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Class Information")
                .setView(binding.getRoot())
                .setPositiveButton("Update", (dialog, which) -> {
                    if (listener != null) {
                        TutoringClass updatedClass = new TutoringClass();
                        updatedClass.setStudentInfo(binding.edtStudentInfo.getText().toString());
                        updatedClass.setSchedule(parseSchedule(binding.edtSchedule.getText().toString()));
                        updatedClass.setSalary(binding.edtSalary.getText().toString());
                        updatedClass.setForm(binding.edtForm.getText().toString());
                        updatedClass.setAddress(binding.edtAddress.getText().toString());
                        assert tutoringClass != null;
                        updatedClass.setStatus(tutoringClass.getStatus());
                        if (!subjectIds.isEmpty())
                            updatedClass.setSubjects(subjectIds);
                        updatedClass.setRequirement(binding.edtRequirement.getText().toString());

                        listener.onUpdateClass(updatedClass);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private static List<String> parseSchedule(String schedule) {
        return Arrays.asList(schedule.split(","));
    }

    private static List<String> parseSubject(String subject) {
        return Arrays.asList(subject.split(","));
    }

    public interface OnUpdateClassListener {
        void onUpdateClass(TutoringClass updatedClass);
    }

    public static void loadAllSubjects(final SubjectCallback callback) {
        if (isSubjectsLoaded) {
            callback.onSuccess(subjectsCache);
            return;
        }

        SubjectService subjectService = RetrofitClient.getRetrofitInstance().create(SubjectService.class);
        Call<List<Subject>> call = subjectService.getSubjects();

        call.enqueue(new Callback<List<Subject>>() {
            @Override
            public void onResponse(Call<List<Subject>> call, Response<List<Subject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    subjectsCache = response.body();
                    isSubjectsLoaded = true;
                    callback.onSuccess(subjectsCache);
                } else {
                    callback.onFailure("Failed to retrieve subjects.");
                }
            }

            @Override
            public void onFailure(Call<List<Subject>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public static List<Subject> getSubjects() {
        return subjectsCache;
    }

    public interface SubjectCallback {
        void onSuccess(List<Subject> subjects);
        void onFailure(String errorMessage);
    }
}
