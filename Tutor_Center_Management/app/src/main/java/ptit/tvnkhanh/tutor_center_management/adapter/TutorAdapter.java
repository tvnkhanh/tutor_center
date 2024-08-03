package ptit.tvnkhanh.tutor_center_management.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.databinding.TeacherInformationItemBinding;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;

public class TutorAdapter extends RecyclerView.Adapter<TutorAdapter.ViewHolder> {
    private List<Tutor> tutors;
    private Context context;
    private boolean isClient;
    private OnTutorClickListener listener;

    public TutorAdapter(List<Tutor> tutors, Context context, boolean isClient, OnTutorClickListener listener) {
        this.tutors = tutors;
        this.context = context;
        this.isClient = isClient;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        TeacherInformationItemBinding binding = TeacherInformationItemBinding.inflate(inflater, parent, false);
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
        private TeacherInformationItemBinding binding;

        public ViewHolder(@NonNull TeacherInformationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Tutor tutor) {
            binding.setIsClient(isClient);
            String fullName = tutor.getFirstName() + " " + tutor.getLastName();
            List<String> subjects = new ArrayList<>();
            for (int i = 0; i < tutor.getSubjects().size(); i++) {
                String subjectName = tutor.getSubjects().get(i).getSubjectName();
                subjects.add(subjectName);
            }
            String subjectsString = String.join(", ", subjects);
            Glide.with(context)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_default_avatar)
                            .error(R.drawable.ic_default_avatar))
                    .load(tutor.getPortraitPhotos().get(0))
                    .into(binding.ivAvatar);
            binding.tvName.setText(fullName);
            binding.tvSubject.setText(subjectsString);
            binding.tvLocation.setText(tutor.getAddress());
            binding.btnHire.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onHireClicked();
                }
            });
            binding.clContainer.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onTutorItemClicked(tutor);
                }
            });
        }
    }

    public interface OnTutorClickListener {
        void onHireClicked();
        void onTutorItemClicked(Tutor tutor);
    }
}
