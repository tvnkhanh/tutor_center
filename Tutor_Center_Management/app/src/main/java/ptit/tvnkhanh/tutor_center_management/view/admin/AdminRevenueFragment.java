package ptit.tvnkhanh.tutor_center_management.view.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentAdminRevenueBinding;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.admin.AdminService;
import ptit.tvnkhanh.tutor_center_management.services.admin.models.RevenueResponse;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
import ptit.tvnkhanh.tutor_center_management.util.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRevenueFragment extends Fragment {

    private FragmentAdminRevenueBinding binding;
    private String token;
    private AdminService adminService;
    private long startDate;
    private long endDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminRevenueBinding.inflate(inflater, container, false);
        token = SharedPreferencesUtility.getString(requireContext(), Constants.X_AUTH_TOKEN, "");
        initUI();
        return binding.getRoot();
    }

    private void initUI() {
        binding.tvTotalRevenue.setVisibility(View.GONE);
        binding.btnDateRangePicker.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            calendar.clear();

            long today = MaterialDatePicker.todayInUtcMilliseconds();
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
            constraintsBuilder.setEnd(today);

            MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            builder.setTitleText("Select Date Range");
            builder.setCalendarConstraints(constraintsBuilder.build());

            final MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();
            dateRangePicker.show(requireActivity().getSupportFragmentManager(), dateRangePicker.toString());

            dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                @Override
                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                    if (selection != null) {
                        startDate = selection.first;
                        endDate = selection.second;

                        if (isDateRangeValid(startDate, endDate)) {
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
                            String start = sdf.format(startDate);
                            String end = sdf.format(endDate);

                            binding.tvSelectedRange.setText(String.format("Selected range: %s - %s", start, end));
                            fetchRevenueStatistics();
                        } else {
                            Utility.showToast(requireContext(), "Please select a range within 6 months.");
                        }
                    }
                }
            });
        });
    }

    private boolean isDateRangeValid(long startDate, long endDate) {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        startCal.setTimeInMillis(startDate);
        endCal.setTimeInMillis(endDate);

        int monthsDiff = (endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)) * 12 + endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH);
        return monthsDiff <= 6;
    }

    private void fetchRevenueStatistics() {
        adminService = RetrofitClient.getRetrofitInstance().create(AdminService.class);
        if (token != null && !token.isEmpty()) {
            String startDateISO = new SimpleDateFormat(Constants.DATE_FORMAT_MONGO_DB, Locale.getDefault()).format(new Date(startDate));
            String endDateISO = new SimpleDateFormat(Constants.DATE_FORMAT_MONGO_DB, Locale.getDefault()).format(new Date(endDate));

            Log.d("AdminRevenueFragment", "startDate: " + startDateISO);
            Log.d("AdminRevenueFragment", "endDate: " + endDateISO);

            adminService.getRevenueStatistics(token, startDateISO, endDateISO).enqueue(new Callback<List<RevenueResponse>>() {
                @Override
                public void onResponse(Call<List<RevenueResponse>> call, Response<List<RevenueResponse>> response) {
                    if (response.isSuccessful()) {
                        List<RevenueResponse> revenueList = response.body();
                        if (revenueList != null) {
                            Log.d("AdminRevenueFragment", "Revenue list: " + revenueList.toString());
                            displayChart(revenueList);

                            double totalRevenue = calculateTotalRevenue(revenueList);
                            updateTotalRevenue(totalRevenue);
                        }
                    } else {
                        Log.e("AdminRevenueFragment", "Error: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<RevenueResponse>> call, Throwable throwable) {
                    Log.e("AdminRevenueFragment", "Error: " + throwable.getMessage());
                }
            });
        }
    }

    private void displayChart(List<RevenueResponse> responseList) {
        Map<String, Float> monthlyRevenueMap = new HashMap<>();

        for (RevenueResponse revenue : responseList) {
            String month = revenue.getMonth() != null ? revenue.getMonth() : "Unknown";
            float revenueAmount = (float) revenue.getRevenue();

            if (monthlyRevenueMap.containsKey(month)) {
                monthlyRevenueMap.put(month, monthlyRevenueMap.get(month) + revenueAmount);
            } else {
                monthlyRevenueMap.put(month, revenueAmount);
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Float> entry : monthlyRevenueMap.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Revenue");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(dataSet);
        binding.barChart.setData(barData);

        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        YAxis leftAxis = binding.barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);

        binding.barChart.getAxisRight().setEnabled(false);
        binding.barChart.setFitBars(true);
        binding.barChart.invalidate();
    }

    private double calculateTotalRevenue(List<RevenueResponse> revenueList) {
        double totalRevenue = 0.0;
        for (RevenueResponse revenue : revenueList) {
            totalRevenue += revenue.getRevenue();
        }
        return totalRevenue;
    }

    private void updateTotalRevenue(double totalRevenue) {
        SpannableString formattedTotalRevenue = Utility.boldText(requireActivity().getString(R.string.revenue_total, Utility.formatNumber(totalRevenue)));
        binding.tvTotalRevenue.setText(formattedTotalRevenue);
        binding.tvTotalRevenue.setVisibility(View.VISIBLE);
    }
}