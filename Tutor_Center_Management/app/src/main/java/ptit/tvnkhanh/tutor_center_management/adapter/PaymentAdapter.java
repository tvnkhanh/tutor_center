package ptit.tvnkhanh.tutor_center_management.adapter;

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
import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.databinding.PaymentItemBinding;
import ptit.tvnkhanh.tutor_center_management.models.Subject;
import ptit.tvnkhanh.tutor_center_management.services.common.models.PaymentInfoDataResponse;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.Utility;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {
    private static final String FEE_PERCENT = "30%";
    private Context context;
    private List<PaymentInfoDataResponse> dataResponses;
    private OnPaymentInfoClickListener listener;
    private UserSession userSession = UserSession.getInstance();

    public PaymentAdapter(Context context, List<PaymentInfoDataResponse> dataResponses, OnPaymentInfoClickListener listener) {
        this.context = context;
        this.dataResponses = dataResponses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        PaymentItemBinding binding = PaymentItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentInfoDataResponse dataResponse = dataResponses.get(position);
        holder.bind(dataResponse);
    }

    @Override
    public int getItemCount() {
        return dataResponses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private PaymentItemBinding binding;

        public ViewHolder(@NonNull PaymentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PaymentInfoDataResponse dataResponse) {
            String roleId = userSession.getAccount().getRoleId();
            List<String> subjectsList = new ArrayList<>();
            for (Subject subject : dataResponse.getSubjects()) {
                subjectsList.add(subject.getSubjectName());
            }
            String subjects = String.join(", ", subjectsList);
            int amount = Integer.parseInt(dataResponse.getSalary());
            if (Objects.equals(roleId, Constants.ROLE_TUTOR_ID)) {
                amount = Integer.parseInt(dataResponse.getSalary()) * 30 / 100;
            }
            binding.setIsPaid(Objects.equals(dataResponse.getPaymentStatus(), Constants.PAYMENT_STATUS_PAID));
            binding.tvStatus.setText(Utility.boldText(context.getString(R.string.payment_screen_status, dataResponse.getPaymentStatus())));
            binding.tvClassId.setText(Utility.boldText(context.getString(R.string.payment_screen_class_id, dataResponse.get_id())));
            binding.tvSubject.setText(Utility.boldText(context.getString(R.string.payment_screen_subject, subjects)));
            binding.tvSchedule.setText(Utility.boldText(context.getString(R.string.payment_screen_schedule, dataResponse.getSchedule())));
            binding.tvForm.setText(Utility.boldText(context.getString(R.string.payment_screen_form, dataResponse.getForm())));
            binding.tvAddress.setText(Utility.boldText(context.getString(R.string.payment_screen_address, dataResponse.getAddress())));
            binding.tvSalary.setText(Utility.boldText(context.getString(R.string.payment_screen_salary, dataResponse.getSalary())));
            binding.tvFeePercent.setText(Utility.boldText(context.getString(R.string.payment_screen_fee_percent, FEE_PERCENT)));
            binding.tvAmount.setText(Utility.boldText(context.getString(R.string.payment_screen_amount, Utility.formatNumber(amount))));
            binding.btnPay.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onPayButtonClicked(dataResponse);
                }
            });
        }
    }

    public interface OnPaymentInfoClickListener {
        void onPayButtonClicked(PaymentInfoDataResponse dataResponse);
    }

    public void updateData(List<PaymentInfoDataResponse> newPaymentList) {
        this.dataResponses = newPaymentList;
        notifyDataSetChanged();
    }
}
