package ptit.tvnkhanh.tutor_center_management.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.databinding.DialogClassDetailBinding;
import ptit.tvnkhanh.tutor_center_management.databinding.DialogTutorDetailBinding;
import ptit.tvnkhanh.tutor_center_management.models.Client;
import ptit.tvnkhanh.tutor_center_management.models.Subject;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.models.TutoringClass;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.common.ClassService;
import ptit.tvnkhanh.tutor_center_management.services.common.ClientService;
import ptit.tvnkhanh.tutor_center_management.services.common.TutorService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utility {

    private static final String INPUT_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
    private static final String OUTPUT_DATE_FORMAT = "dd/MM/yyyy";
    private static Dialog tutorDetailDialog;
    private static Dialog classDetailDialog;

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

    public static void showTutorDetailDialog(Context context, Tutor tutor) {
        tutorDetailDialog = new Dialog(context, R.style.TransparentDialog);
        DialogTutorDetailBinding binding = DialogTutorDetailBinding.inflate(LayoutInflater.from(context));
        tutorDetailDialog.setContentView(binding.getRoot());
        Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar))
                .load(tutor.getPortraitPhotos().get(0))
                .into(binding.ivAvatar);
        binding.tvName.setText(context.getString(R.string.detail_tutor_screen_name, tutor.getFirstName() + " " + tutor.getLastName()));
        binding.tvGender.setText(context.getString(R.string.detail_tutor_screen_gender, tutor.getGender()));
        binding.tvDateOfBirth.setText(context.getString(R.string.detail_tutor_screen_date_of_birth, convertDateFormat(tutor.getDateOfBirth().getTime())));
        binding.tvContact.setText(context.getString(R.string.detail_tutor_screen_contact, tutor.getContactNumber()));
        binding.tvEmail.setText(context.getString(R.string.detail_tutor_screen_email, tutor.getEmail()));
        binding.tvAddress.setText(context.getString(R.string.detail_tutor_screen_address, tutor.getAddress()));
        binding.tvCid.setText(context.getString(R.string.detail_tutor_screen_cid, tutor.getCitizenId()));
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
        TutorService tutorService = RetrofitClient.getRetrofitInstance().create(TutorService.class);
        ClientService clientService = RetrofitClient.getRetrofitInstance().create(ClientService.class);
        String token = SharedPreferencesUtility.getString(context, Constants.X_AUTH_TOKEN, "");

        classDetailDialog = new Dialog(context, R.style.TransparentDialog);
        DialogClassDetailBinding binding = DialogClassDetailBinding.inflate(LayoutInflater.from(context));
        classDetailDialog.setContentView(binding.getRoot());

        if (token != null && !token.isEmpty()) {
            if (tutoringClass.getTutorId() != null && !tutoringClass.getTutorId().isEmpty()) {
                tutorService.getTutorById(token, tutoringClass.getTutorId().get(0)).enqueue(new Callback<Tutor>() {
                    @Override
                    public void onResponse(Call<Tutor> call, Response<Tutor> response) {
                        if (response.isSuccessful()) {
                            Tutor tutor = response.body();
                            Log.d("ClassDetailDialog", "Tutor: " + tutor);
                            if (tutor != null) {
                                binding.tvTutorName.setText(context.getString(R.string.detail_class_screen_name, tutor.getFirstName() + " " + tutor.getLastName()));
                                Glide.with(context).applyDefaultRequestOptions(new RequestOptions()
                                        .placeholder(R.drawable.ic_default_avatar)
                                        .error(R.drawable.ic_default_avatar))
                                        .load(tutor.getPortraitPhotos().get(0))
                                        .into(binding.ivTutorImage);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Tutor> call, Throwable throwable) {

                    }
                });
            } else {
                binding.tvTutorName.setVisibility(View.GONE);
            }

            clientService.getClientById(token, tutoringClass.getClientId()).enqueue(new Callback<Client>() {
                @Override
                public void onResponse(Call<Client> call, Response<Client> response) {
                    if (response.isSuccessful()) {
                        Client client = response.body();
                        Log.d("ClassDetailDialog", "Client: " + client);
                        if (client != null) {
                            binding.tvStudentName.setText(context.getString(R.string.detail_class_screen_student_name, client.getFirstName() + " " + client.getLastName()));
                        }
                    }
                }

                @Override
                public void onFailure(Call<Client> call, Throwable throwable) {

                }
            });
        }

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
}
