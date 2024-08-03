package ptit.tvnkhanh.tutor_center_management.view.admin;

import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

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
import java.util.List;
import java.util.Locale;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentAdminRevenueBinding;
import ptit.tvnkhanh.tutor_center_management.services.RetrofitClient;
import ptit.tvnkhanh.tutor_center_management.services.admin.AdminService;
import ptit.tvnkhanh.tutor_center_management.services.admin.models.RevenueResponse;
import ptit.tvnkhanh.tutor_center_management.util.Constants;
import ptit.tvnkhanh.tutor_center_management.util.SharedPreferencesUtility;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminRevenueBinding.inflate(getLayoutInflater());
        token = SharedPreferencesUtility.getString(requireContext(), Constants.X_AUTH_TOKEN, "");
        initUI();
        return binding.getRoot();
    }

    private void initUI() {
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

                        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
                        String start = sdf.format(startDate);
                        String end = sdf.format(endDate);

                        binding.tvSelectedRange.setText(String.format("Selected range: %s - %s", start, end));

                        fetchRevenueStatistics();
                    }
                }
            });
        });
    }

    private void fetchRevenueStatistics() {
        adminService = RetrofitClient.getRetrofitInstance().create(AdminService.class);
        if (token != null && !token.isEmpty()) {
            Log.d("AdminRevenueFragment", "startDate: " + startDate);
            Log.d("AdminRevenueFragment", "endDate: " + endDate);
            adminService.getRevenueStatistics(token, String.valueOf(startDate), String.valueOf(endDate)).enqueue(new Callback<List<RevenueResponse>>() {
                @Override
                public void onResponse(Call<List<RevenueResponse>> call, Response<List<RevenueResponse>> response) {
                    if (response.isSuccessful()) {
                        List<RevenueResponse> revenueList = response.body();
                        if (revenueList != null) {
                            displayChart(revenueList);
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
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < responseList.size(); i++) {
            RevenueResponse revenue = responseList.get(i);
            entries.add(new BarEntry(i, (float) revenue.getRevenue()));
            String month = revenue.getMonth() != null ? revenue.getMonth() : "Unknown";
            labels.add(month);
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

}