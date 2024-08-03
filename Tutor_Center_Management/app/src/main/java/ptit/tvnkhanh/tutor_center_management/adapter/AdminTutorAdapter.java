package ptit.tvnkhanh.tutor_center_management.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.databinding.AdminTutorItemBinding;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;
import ptit.tvnkhanh.tutor_center_management.util.Utility;

public class AdminTutorAdapter extends RecyclerView.Adapter<AdminTutorAdapter.ViewHolder> {
    private List<Tutor> tutors;
    private Context context;
    private OnTutorItemClickListener listener;

    public AdminTutorAdapter(List<Tutor> tutors, Context context, OnTutorItemClickListener listener) {
        this.tutors = tutors;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdminTutorItemBinding binding = AdminTutorItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tutor tutor = tutors.get(position);
        holder.bind(tutor);
    }

    @Override
    public int getItemCount() {
        return tutors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private AdminTutorItemBinding binding;

        public ViewHolder(@NonNull AdminTutorItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Tutor tutor) {
            String name = tutor.getFirstName() + " " + tutor.getLastName();
            String dateOfBirth = Utility.convertDateFormat(tutor.getDateOfBirth().getTime());
            binding.tvStatus.setText(Utility.boldText(context.getString(R.string.teacher_item_status, tutor.getStatus())));
            binding.tvName.setText(Utility.boldText(context.getString(R.string.teacher_item_name, name)));
            binding.tvDateOfBirth.setText(Utility.boldText(context.getString(R.string.teacher_item_birth, dateOfBirth)));
            binding.tvGender.setText(Utility.boldText(context.getString(R.string.teacher_item_gender, tutor.getGender())));
            binding.tvAddress.setText(Utility.boldText(context.getString(R.string.teacher_item_address, tutor.getAddress())));
            binding.btnApprove.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onApproveClick(tutor);
                }
            });
            binding.btnReject.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onRejectClick(tutor);
                }
            });
        }
    }

    public interface OnTutorItemClickListener {
        void onApproveClick(Tutor tutor);
        void onRejectClick(Tutor tutor);
    }
}
