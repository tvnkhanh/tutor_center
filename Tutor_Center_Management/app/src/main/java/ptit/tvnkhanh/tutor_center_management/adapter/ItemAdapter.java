package ptit.tvnkhanh.tutor_center_management.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ptit.tvnkhanh.tutor_center_management.databinding.DialogItemRadioBinding;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<String> items;
    private int selectedPosition;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public ItemAdapter(Context context, List<String> items, OnItemClickListener onItemClickListener, int selectedPosition) {
        this.items = items;
        this.onItemClickListener = onItemClickListener;
        this.context = context;
        this.selectedPosition = selectedPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        DialogItemRadioBinding binding = DialogItemRadioBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items, position, holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private DialogItemRadioBinding binding;

        public ViewHolder(@NonNull DialogItemRadioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(List<String> items, int position, int adapterPosition) {
            binding.radioButton.setChecked(position == selectedPosition);
            binding.tvItem.setText(items.get(position));
            binding.llContainer.setOnClickListener(view -> {
                int oldPosition = selectedPosition;
                selectedPosition = adapterPosition;
                notifyItemChanged(oldPosition);
                notifyItemChanged(selectedPosition);
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
