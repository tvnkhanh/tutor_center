package ptit.tvnkhanh.tutor_center_management.view;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import java.util.List;
import java.util.Objects;

import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.adapter.PaymentAdapter;
import ptit.tvnkhanh.tutor_center_management.databinding.DialogPaymentBinding;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentPaymentScreenBinding;
import ptit.tvnkhanh.tutor_center_management.models.Payment;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.common.ClassService;
import ptit.tvnkhanh.tutor_center_management.services.common.PaymentService;
import ptit.tvnkhanh.tutor_center_management.services.common.models.PaymentInfoDataResponse;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentScreenFragment extends Fragment {
    private static final String PAYMENT_TITLE_DIALOG = "Amount: ";
    private FragmentPaymentScreenBinding binding;
    private DialogPaymentBinding dialogBinding;
    private ClassService classService;
    private PaymentService paymentService;
    private UserSession userSession;
    private String token;
    private PaymentAdapter adapter;
    private List<PaymentInfoDataResponse> paymentInfoDataResponses;
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPaymentScreenBinding.inflate(getLayoutInflater());
        token = SharedPreferencesUtility.getString(requireContext(), Constants.X_AUTH_TOKEN, "");
        userSession = UserSession.getInstance();
        classService = RetrofitClient.getRetrofitInstance().create(ClassService.class);
        getPaymentInfo();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        hidePaymentDialog();
    }

    private void initUI() {
        binding.progressBar.setVisibility(View.GONE);
        RecyclerView recyclerView = binding.rvPaymentContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void getPaymentInfo() {
        binding.progressBar.setVisibility(View.VISIBLE);
        String roleId = userSession.getAccount().getRoleId();
        if (Objects.equals(roleId, Constants.ROLE_TUTOR_ID)) {
            getPaymentInfoByTutorId();
        } else if (Objects.equals(roleId, Constants.ROLE_CLIENT_ID)) {
            getPaymentInfoByClientId();
        }
    }

    private void getPaymentInfoByClientId() {
        if (token != null && !token.isEmpty()) {
            Log.d("getPaymentInfoByClientId", userSession.getAccount().getClientId());
            classService.getClassesPaymentInfoByClientId(token, userSession.getAccount().getClientId()).enqueue(new Callback<List<PaymentInfoDataResponse>>() {
                @Override
                public void onResponse(Call<List<PaymentInfoDataResponse>> call, Response<List<PaymentInfoDataResponse>> response) {
                    if (response.isSuccessful()) {
                        paymentInfoDataResponses = response.body();
                        if (paymentInfoDataResponses != null) {
                            adapter = new PaymentAdapter(getContext(), paymentInfoDataResponses, dataResponse -> {
                                showPaymentDialog(dataResponse);
                            });
                            initUI();
                        } else {
                            Log.d("getPaymentInfoByClientId", "Payment info data null");
                        }
                    } else {
                        Log.e("getPaymentInfoByClientId", "Failed to get classes. Error: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<PaymentInfoDataResponse>> call, Throwable throwable) {
                    Log.e("getPaymentInfoByClientId", "Error: " + throwable.getMessage());
                }
            });
        }
    }

    private void getPaymentInfoByTutorId() {
        if (token != null && !token.isEmpty()) {
            classService.getClassesPaymentInfoByTutorId(token, userSession.getAccount().getTutorId()).enqueue(new Callback<List<PaymentInfoDataResponse>>() {
                @Override
                public void onResponse(Call<List<PaymentInfoDataResponse>> call, Response<List<PaymentInfoDataResponse>> response) {
                    if (response.isSuccessful()) {
                        paymentInfoDataResponses = response.body();
                        if (paymentInfoDataResponses != null) {
                            adapter = new PaymentAdapter(getContext(), paymentInfoDataResponses, dataResponse -> {
                                showPaymentDialog(dataResponse);
                            });
                            initUI();
                        } else {
                            Log.d("getPaymentInfoByTutorId", "Payment info data null");
                        }
                    } else {
                        Log.e("getPaymentInfoByTutorId", "Failed to get classes. Error: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<PaymentInfoDataResponse>> call, Throwable throwable) {
                    Log.e("getPaymentInfoByTutorId", "Error: " + throwable.getMessage());
                }
            });
        }
    }

    private void showPaymentDialog(PaymentInfoDataResponse dataResponse) {
        String roleId = UserSession.getInstance().getAccount().getRoleId();
        int amount = Integer.parseInt(dataResponse.getSalary());
        if (Objects.equals(roleId, Constants.ROLE_TUTOR_ID)) {
            amount = Integer.parseInt(dataResponse.getSalary()) * 40 / 100;
        }
        dialog = new Dialog(requireContext());
        dialogBinding = DialogPaymentBinding.inflate(LayoutInflater.from(requireContext()));
        dialog.setContentView(dialogBinding.getRoot());

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);

        dialogBinding.tvPaymentInfo.setText(String.format("%s%s", PAYMENT_TITLE_DIALOG, Utility.formatNumber(amount)));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, Constants.PAYMENT_METHOD_LIST);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spinnerPaymentMethods.setAdapter(adapter);

        int finalAmount = amount;
        dialogBinding.btnSavePayment.setOnClickListener(v -> {
            String selectedPaymentMethod = (String) dialogBinding.spinnerPaymentMethods.getSelectedItem();
            Log.d("selectedPaymentMethod", selectedPaymentMethod);
            if (selectedPaymentMethod.equals(Constants.PAYMENT_METHOD_CASH_NAME)) {
                selectedPaymentMethod = Constants.PAYMENT_METHOD_CASH_ID;
            } else if (selectedPaymentMethod.equals(Constants.PAYMENT_METHOD_MOMO_NAME)) {
                selectedPaymentMethod = Constants.PAYMENT_METHOD_MOMO_ID;
            } else if (selectedPaymentMethod.equals(Constants.PAYMENT_METHOD_VISA_NAME)) {
                selectedPaymentMethod = Constants.PAYMENT_METHOD_VISA_ID;
            }

            Payment payment = new Payment();
            payment.setClassId(dataResponse.get_id());
            payment.setPaymentMethod(selectedPaymentMethod);
            payment.setAmount(String.valueOf(finalAmount));
            if (Objects.equals(roleId, Constants.ROLE_CLIENT_ID)) {
                payment.setClientId(userSession.getAccount().getClientId());
            } else {
                payment.setTutorId(userSession.getAccount().getTutorId());
            }
            makePayment(payment);
            dialog.dismiss();
        });

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void hidePaymentDialog() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.cancel();
                dialog.dismiss();
            }
            dialog = null;
        }
    }

    private void makePayment(Payment payment) {
        paymentService = RetrofitClient.getRetrofitInstance().create(PaymentService.class);
        if (token != null && !token.isEmpty()) {
            paymentService.makePayment(token, payment).enqueue(new Callback<Payment>() {
                @Override
                public void onResponse(Call<Payment> call, Response<Payment> response) {
                    if (response.isSuccessful()) {
                        Log.d("makePayment", "Payment success");
                        Utility.showToast(requireContext(), "Payment success");
                        getPaymentInfo();
                    } else {
                        Log.d("makePayment", "Payment failed");
                        Log.d("makePayment", "Error: " + response.message());
                        Log.d("makePayment", "Error: " + payment.toString());
                        Utility.showToast(requireContext(), "Payment failed");
                    }
                }

                @Override
                public void onFailure(Call<Payment> call, Throwable throwable) {
                    Log.d("makePayment", "Error: " + throwable.getMessage());
                    Utility.showToast(requireContext(), "Error: " + throwable.getMessage());
                }
            });
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hidePaymentDialog();
    }
}