package ptit.tvnkhanh.tutor_center_management.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.databinding.AdminClassItemBinding;
import ptit.tvnkhanh.tutor_center_management.models.Subject;
import ptit.tvnkhanh.tutor_center_management.models.TutoringClass;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.Utility;

public class AdminClassAdapter extends RecyclerView.Adapter<AdminClassAdapter.ViewHolder> {
    private List<TutoringClass> classes;
    private Context context;
    private OnAdminClassClickListener listener;

    public AdminClassAdapter(Context context, List<TutoringClass> classes, OnAdminClassClickListener listener) {
        this.classes = classes;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdminClassItemBinding binding = AdminClassItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TutoringClass classItem = classes.get(position);
        holder.bind(classItem);
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private AdminClassItemBinding binding;

        public ViewHolder(@NonNull AdminClassItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TutoringClass classItem) {
            List<String> subjectsList = new ArrayList<>();
            for (Subject subject : classItem.getSubjectDetails()) {
                subjectsList.add(subject.getSubjectName());
            }
            String subjects = String.join(", ", subjectsList);
            String tutors = "";
            if (classItem.getTutorId() != null) {
                tutors = String.join(", ", classItem.getTutorId());
            }
            List<String> schedule;
            String scheduleString = "";
            if (classItem.getSchedule() != null) {
                schedule = classItem.getSchedule();
                scheduleString = String.join(", ", schedule);
            }
            binding.setIsCompleted(false);
            binding.tvTutor.setVisibility(View.GONE);
            if (Objects.equals(classItem.getStatus(), Constants.CLASS_STATUS_REJECTED)) {
                binding.setIsCompleted(true);
            } else if (Objects.equals(classItem.getStatus(), Constants.CLASS_STATUS_PENDING)) {
                binding.setStatus(Constants.CLASS_STATUS_APPROVED_BUTTON);
            } else if (Objects.equals(classItem.getStatus(), Constants.CLASS_STATUS_APPROVED)) {
                binding.setStatus(Constants.CLASS_STATUS_ASSIGNED_BUTTON);
                binding.tvTutor.setVisibility(View.VISIBLE);
            } else if (Objects.equals(classItem.getStatus(), Constants.CLASS_STATUS_ASSIGNED)) {
                binding.setStatus(Constants.CLASS_STATUS_COMPLETED_BUTTON);
                binding.tvTutor.setVisibility(View.VISIBLE);
            } else if (Objects.equals(classItem.getStatus(), Constants.CLASS_STATUS_COMPLETED)) {
                binding.setIsCompleted(true);
                binding.tvTutor.setVisibility(View.VISIBLE);
            }
            binding.setIsPending(Objects.equals(classItem.getStatus(), Constants.CLASS_STATUS_PENDING));
            binding.tvClassId.setText(Utility.boldText(context.getString(R.string.classes_screen_class_id, classItem.get_id())));
            binding.tvStatus.setText(Utility.boldText(context.getString(R.string.classes_screen_status, classItem.getStatus())));
            binding.tvStudentInfo.setText(Utility.boldText(context.getString(R.string.classes_screen_student_info, classItem.getStudentInfo())));
            binding.tvSubject.setText(Utility.boldText(context.getString(R.string.classes_screen_subject, subjects)));
            binding.tvSchedule.setText(Utility.boldText(context.getString(R.string.classes_screen_schedule, scheduleString)));
            binding.tvForm.setText(Utility.boldText(context.getString(R.string.classes_screen_form, classItem.getForm())));
            binding.tvAddress.setText(Utility.boldText(context.getString(R.string.classes_screen_address, classItem.getAddress())));
            binding.tvSalary.setText(Utility.boldText(context.getString(R.string.classes_screen_salary, Utility.formatNumber(Integer.parseInt(classItem.getSalary())))));
            binding.tvRequirement.setText(Utility.boldText(context.getString(R.string.classes_screen_requirement, classItem.getRequirement())));
            binding.tvTutor.setText(Utility.boldText(context.getString(R.string.classes_screen_tutors, tutors)));
            binding.btnChangeStatus.setOnClickListener(view -> {
                // Handle button click
                if (listener != null) {
                    listener.onChangeStatusClick(classItem);
                }
            });
            binding.btnRejected.setOnClickListener(view -> {
                // Handle button click
                if (listener != null) {
                    listener.onRejectedClick(classItem);
                }
            });
            binding.llContainer.setOnClickListener(view -> {
                // Handle button click
                if (listener != null) {
                    listener.onClassItemDetailClick(classItem);
                }
            });
//            binding.ivEditClass.setOnClickListener(view -> {
//                // Handle button click
//                if (listener != null) {
//                    listener.onEditClassClick(classItem);
//                }
//            });
        }
    }

    public interface OnAdminClassClickListener {
        void onChangeStatusClick(TutoringClass classItem);
        void onRejectedClick(TutoringClass classItem);
        void onClassItemDetailClick(TutoringClass classItem);
        void onEditClassClick(TutoringClass classItem);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateClasses(List<TutoringClass> newClasses) {
        this.classes = newClasses;
        notifyDataSetChanged();
    }
}
