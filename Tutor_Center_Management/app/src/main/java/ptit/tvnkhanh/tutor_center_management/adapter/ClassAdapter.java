package ptit.tvnkhanh.tutor_center_management.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.databinding.ClassItemBinding;
import ptit.tvnkhanh.tutor_center_management.models.TutoringClass;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.Utility;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {
    private List<TutoringClass> classes;
    private Context context;
    private boolean isTutor;
    private OnRegisterClickListener listener;

    public ClassAdapter(List<TutoringClass> classes, Context context, boolean isTutor, OnRegisterClickListener listener) {
        this.classes = classes;
        this.context = context;
        this.isTutor = isTutor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ClassItemBinding binding = ClassItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TutoringClass tutoringClass = classes.get(position);
        holder.bind(tutoringClass);
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ClassItemBinding binding;

        public ViewHolder(@NonNull ClassItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TutoringClass tutoringClass) {
            UserSession userSession = UserSession.getInstance();
            boolean isRegistered = false;
            if (tutoringClass.getTutorId() != null && !tutoringClass.getTutorId().isEmpty() && userSession.getAccount().getRoleId().equals(Constants.ROLE_TUTOR_ID) && Objects.equals(tutoringClass.getStatus(), Constants.CLASS_STATUS_APPROVED)) {
                isRegistered = tutoringClass.getTutorId().contains(userSession.getTutor().get_id());
            }
            String reason = Utility.getReasonByClassId(tutoringClass.get_id());
            List<String> schedule = tutoringClass.getSchedule();
            String scheduleString = String.join(", ", schedule);
            List<String> subjects = new ArrayList<>();
            for (int i = 0; i < tutoringClass.getSubjectDetails().size(); i++) {
                String subjectName = tutoringClass.getSubjectDetails().get(i).getSubjectName();
                subjects.add(subjectName);
            }
            String subjectsString = String.join(", ", subjects);
            int salary = Integer.parseInt(tutoringClass.getSalary());
            String salaryString = Utility.formatNumber(salary);
            binding.setIsTutor(isTutor);
            binding.setIsRegistered(isRegistered);
            binding.setIsEditable(tutoringClass.getStatus().equals(Constants.CLASS_STATUS_REJECTED));
            binding.setIsNotActive(!tutoringClass.getStatus().equals(Constants.CLASS_STATUS_ASSIGNED));
            binding.tvClassId.setText(Utility.boldText(context.getString(R.string.classes_screen_class_id, tutoringClass.get_id())));
            binding.tvStatus.setText(Utility.boldText(context.getString(R.string.classes_screen_status, tutoringClass.getStatus())));
            binding.tvStudentInfo.setText(Utility.boldText(context.getString(R.string.classes_screen_student_info, tutoringClass.getStudentInfo())));
            binding.tvSubject.setText(Utility.boldText(context.getString(R.string.classes_screen_subject, subjectsString)));
            binding.tvSchedule.setText(Utility.boldText(context.getString(R.string.classes_screen_schedule, scheduleString)));
            binding.tvForm.setText(Utility.boldText(context.getString(R.string.classes_screen_form, tutoringClass.getForm())));
            binding.tvAddress.setText(Utility.boldText(context.getString(R.string.classes_screen_address, tutoringClass.getAddress())));
            binding.tvSalary.setText(Utility.boldText(context.getString(R.string.classes_screen_salary, salaryString)));
            if (reason != null)
                binding.tvReason.setText(Utility.boldText(context.getString(R.string.classes_screen_reason, reason)));
            binding.btnRegister.setOnClickListener(view -> {
                Log.d("ClassAdapter", "Register button clicked for class: " + tutoringClass.get_id());
                if (listener != null) {
                    listener.onRegisterClick(tutoringClass.get_id());
                }
            });
            binding.llContainer.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onClassItemClicked(tutoringClass);
                }
            });
            binding.ivEditClass.setOnClickListener(view -> {
                Log.d("ClassAdapter", "Edit button clicked for class: " + tutoringClass.get_id());
                if (listener != null) {
                    listener.onEditClick(tutoringClass);
                }
            });
        }
    }

    public interface OnRegisterClickListener {
        void onRegisterClick(String classId);
        void onClassItemClicked(TutoringClass tutoringClass);
        void onEditClick(TutoringClass tutoringClass);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<TutoringClass> newClasses) {
        this.classes = newClasses;
        notifyDataSetChanged();
    }

}
