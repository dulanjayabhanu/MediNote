package lk.dulanjaya.medinote;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import lk.dulanjaya.medinote.model.DataBaker;
import lk.dulanjaya.medinote.model.TimeViceAnalyseAdapter;
import lk.dulanjaya.medinote.model.TimeViceAnalyseDTO;
import lk.dulanjaya.medinote.model.UiToolkitManager;

public class MonthlyRecordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_monthly_records);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UiToolkitManager.setStatusBarAndNavigationBarColor(MonthlyRecordsActivity.this, R.color.primary_color_white, R.color.primary_color_white);

        new Thread(new Runnable(){
            @Override
            public void run(){
                // get dataBakingManagers
                DataBaker.DataBakingManager<TimeViceAnalyseDTO> dataBakingManager1 = new DataBaker.DataBakingManager<>();
                DataBaker.DataBakingManager<Entry> dataBakingManager2 = new DataBaker.DataBakingManager<>();

                // get all file path
                String monthlyRecordsDTOFilePath = DataBaker.DirectoryInspector.getFilePath(MonthlyRecordsActivity.this, 3, 0);
                List<TimeViceAnalyseDTO> monthlyRecordsDTOList = dataBakingManager1.dataServe(monthlyRecordsDTOFilePath);

                String monthlyRecordsSysEntryFilePath = DataBaker.DirectoryInspector.getFilePath(MonthlyRecordsActivity.this, 3, 1);
                List<Entry> monthlyRecordsSysEntryList = dataBakingManager2.dataServe(monthlyRecordsSysEntryFilePath);

                String monthlyRecordsPulEntryFilePath = DataBaker.DirectoryInspector.getFilePath(MonthlyRecordsActivity.this, 3, 2);
                List<Entry> monthlyRecordsPulEntryList = dataBakingManager2.dataServe(monthlyRecordsPulEntryFilePath);

                String monthlyRecordsDiaEntryFilePath = DataBaker.DirectoryInspector.getFilePath(MonthlyRecordsActivity.this, 3, 3);
                List<Entry> monthlyRecordsDiaEntryList = dataBakingManager2.dataServe(monthlyRecordsDiaEntryFilePath);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!monthlyRecordsSysEntryList.isEmpty() &&
                                !monthlyRecordsPulEntryList.isEmpty() &&
                                !monthlyRecordsDiaEntryList.isEmpty()
                        ){
                            // load line chart
                            LineChart monthlyRecordsLineChart = findViewById(R.id.monthlyRecordsLineChart);
                            monthlyRecordsLineChart.setDescription(null);
                            monthlyRecordsLineChart.setClickable(false);
                            monthlyRecordsLineChart.setPinchZoom(false);
                            monthlyRecordsLineChart.setHighlightPerDragEnabled(false);
                            monthlyRecordsLineChart.setHighlightPerTapEnabled(false);
                            monthlyRecordsLineChart.setDoubleTapToZoomEnabled(false);
                            monthlyRecordsLineChart.setDrawGridBackground(false);
                            monthlyRecordsLineChart.animateY(1500, Easing.EaseOutCirc);
                            monthlyRecordsLineChart.setExtraOffsets(16f, 24f, 24f, 16f);

                            // construct the Y axis left side
                            YAxis leftAxis = monthlyRecordsLineChart.getAxisLeft();
                            leftAxis.setAxisLineWidth(1f);
                            leftAxis.setTextColor(getColor(R.color.secondary_color_black));
                            leftAxis.setTextSize(10f);
                            leftAxis.setGridColor(getColor(R.color.primary_border_color));

                            // construct the Y axis right side
                            YAxis rightAxis = monthlyRecordsLineChart.getAxisRight();
                            rightAxis.setEnabled(false);

                            // construct the X axis
                            XAxis xAxis = monthlyRecordsLineChart.getXAxis();
                            xAxis.setAxisLineWidth(1f);
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setTextColor(getColor(R.color.secondary_color_black));
                            xAxis.setTextSize(10f);
                            xAxis.setGridColor(getColor(R.color.primary_border_color));

                            // construct the chart line ofr represent the sys data
                            LineDataSet lineDataSet1 = new LineDataSet(monthlyRecordsSysEntryList, "");
                            lineDataSet1.setColors(getColor(R.color.primary_color_green));
                            lineDataSet1.setValueTextColor(getColor(R.color.primary_color_transparent));
                            lineDataSet1.setValueTextSize(10f);
                            lineDataSet1.setCircleColor(getColor(R.color.primary_color_green));
                            lineDataSet1.setCircleRadius(6f);
                            lineDataSet1.setCircleHoleColor(getColor(R.color.primary_color_white));
                            lineDataSet1.setCircleHoleRadius(3f);
                            lineDataSet1.setLineWidth(2f);

                            // construct the chart line ofr represent the pul data
                            LineDataSet lineDataSet2 = new LineDataSet(monthlyRecordsPulEntryList, "");
                            lineDataSet2.setColors(getColor(R.color.secondary_color_black));
                            lineDataSet2.setValueTextColor(getColor(R.color.primary_color_transparent));
                            lineDataSet2.setValueTextSize(10f);
                            lineDataSet2.setCircleColor(getColor(R.color.neutral_color_black));
                            lineDataSet2.setCircleRadius(6f);
                            lineDataSet2.setCircleHoleColor(getColor(R.color.primary_color_white));
                            lineDataSet2.setCircleHoleRadius(3f);
                            lineDataSet2.setLineWidth(2f);

                            // construct the chart line ofr represent the dia data
                            LineDataSet lineDataSet3 = new LineDataSet(monthlyRecordsDiaEntryList, "");
                            lineDataSet3.setColors(getColor(R.color.secondary_color_green));
                            lineDataSet3.setValueTextColor(getColor(R.color.primary_color_transparent));
                            lineDataSet3.setValueTextSize(10f);
                            lineDataSet3.setCircleColor(getColor(R.color.secondary_color_green));
                            lineDataSet3.setCircleRadius(6f);
                            lineDataSet3.setCircleHoleColor(getColor(R.color.primary_color_white));
                            lineDataSet3.setCircleHoleRadius(3f);
                            lineDataSet3.setLineWidth(2f);

                            LineData lineData = new LineData();
                            lineData.addDataSet(lineDataSet1);
                            lineData.addDataSet(lineDataSet2);
                            lineData.addDataSet(lineDataSet3);
                            lineData.setHighlightEnabled(true);

                            monthlyRecordsLineChart.setData(lineData);

                            List<LegendEntry> legendEntryArrayList = new ArrayList<>();
                            legendEntryArrayList.add(new LegendEntry("", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getColor(R.color.primary_color_transparent)));

                            monthlyRecordsLineChart.getLegend().setCustom(legendEntryArrayList);
                            monthlyRecordsLineChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                            monthlyRecordsLineChart.invalidate();

                            if(!monthlyRecordsDTOList.isEmpty()){
                                // exclude the no results banner
                                UiToolkitManager.excludeNoResultBanner(MonthlyRecordsActivity.this, R.id.monthlyRecordsNoResultsTextView);

                                // load all record recyclerView
                                RecyclerView allRecordsRecyclerView = findViewById(R.id.monthlyRecordsRecyclerViewAllRecords);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MonthlyRecordsActivity.this);
                                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                allRecordsRecyclerView.setLayoutManager(linearLayoutManager);

                                TimeViceAnalyseAdapter timeViceAnalyseAdapter = new TimeViceAnalyseAdapter(monthlyRecordsDTOList);
                                allRecordsRecyclerView.setAdapter(timeViceAnalyseAdapter);
                                allRecordsRecyclerView.invalidate();
                            }
                        }

                        // construct the backward button
                        ImageButton backImageButton = findViewById(R.id.monthlyRecordsImageButtonBack);
                        backImageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UiToolkitManager.ActivityManager.navigateToActivity(MonthlyRecordsActivity.this, UiToolkitManager.ActivityManager.getHomeActivity());
                            }
                        });
                    }
                });
            }
        }).start();
    }

    // override the navigation back button event
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            UiToolkitManager.ActivityManager.navigateToActivity(MonthlyRecordsActivity.this, UiToolkitManager.ActivityManager.getHomeActivity());
        }
        return super.onKeyDown(keyCode, event);
    }
}