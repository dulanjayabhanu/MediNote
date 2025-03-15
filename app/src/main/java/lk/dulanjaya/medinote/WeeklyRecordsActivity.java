package lk.dulanjaya.medinote;

import android.os.Bundle;
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

public class WeeklyRecordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weekly_records);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UiToolkitManager.setStatusBarAndNavigationBarColor(WeeklyRecordsActivity.this, R.color.primary_color_white, R.color.primary_color_white);

        new Thread(new Runnable(){
            @Override
            public void run(){
                // get dataBakingManagers
                DataBaker.DataBakingManager<TimeViceAnalyseDTO> dataBakingManager1 = new DataBaker.DataBakingManager<>();
                DataBaker.DataBakingManager<Entry> dataBakingManager2 = new DataBaker.DataBakingManager<>();

                // get all file path
                String weeklyRecordsDTOFilePath = DataBaker.DirectoryInspector.getFilePath(WeeklyRecordsActivity.this, 2, 0);
                List<TimeViceAnalyseDTO> weeklyRecordsDTOList = dataBakingManager1.dataServe(weeklyRecordsDTOFilePath);

                String weeklyRecordsSysEntryFilePath = DataBaker.DirectoryInspector.getFilePath(WeeklyRecordsActivity.this, 2, 1);
                List<Entry> weeklyRecordsSysEntryList = dataBakingManager2.dataServe(weeklyRecordsSysEntryFilePath);

                String weeklyRecordsPulEntryFilePath = DataBaker.DirectoryInspector.getFilePath(WeeklyRecordsActivity.this, 2, 2);
                List<Entry> weeklyRecordsPulEntryList = dataBakingManager2.dataServe(weeklyRecordsPulEntryFilePath);

                String weeklyRecordsDiaEntryFilePath = DataBaker.DirectoryInspector.getFilePath(WeeklyRecordsActivity.this, 2, 3);
                List<Entry> weeklyRecordsDiaEntryList = dataBakingManager2.dataServe(weeklyRecordsDiaEntryFilePath);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!weeklyRecordsSysEntryList.isEmpty() &&
                                !weeklyRecordsPulEntryList.isEmpty() &&
                                !weeklyRecordsDiaEntryList.isEmpty()
                        ){
                            // load line chart
                            LineChart weeklyRecordsLineChart = findViewById(R.id.weeklyRecordsLineChart);
                            weeklyRecordsLineChart.setDescription(null);
                            weeklyRecordsLineChart.setClickable(false);
                            weeklyRecordsLineChart.setPinchZoom(false);
                            weeklyRecordsLineChart.setHighlightPerDragEnabled(false);
                            weeklyRecordsLineChart.setHighlightPerTapEnabled(false);
                            weeklyRecordsLineChart.setDoubleTapToZoomEnabled(false);
                            weeklyRecordsLineChart.setDrawGridBackground(false);
                            weeklyRecordsLineChart.animateY(1500, Easing.EaseOutCirc);
                            weeklyRecordsLineChart.setExtraOffsets(16f, 24f, 24f, 16f);

                            // construct the Y axis left side
                            YAxis leftAxis = weeklyRecordsLineChart.getAxisLeft();
                            leftAxis.setAxisLineWidth(1f);
                            leftAxis.setTextColor(getColor(R.color.secondary_color_black));
                            leftAxis.setTextSize(10f);
                            leftAxis.setGridColor(getColor(R.color.primary_border_color));

                            // construct the Y axis right side
                            YAxis rightAxis = weeklyRecordsLineChart.getAxisRight();
                            rightAxis.setEnabled(false);

                            // construct the X axis
                            XAxis xAxis = weeklyRecordsLineChart.getXAxis();
                            xAxis.setAxisLineWidth(1f);
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setTextColor(getColor(R.color.secondary_color_black));
                            xAxis.setTextSize(10f);
                            xAxis.setGridColor(getColor(R.color.primary_border_color));

                            // construct the chart line for represent the sys data
                            LineDataSet lineDataSet1 = new LineDataSet(weeklyRecordsSysEntryList, "");
                            lineDataSet1.setColors(getColor(R.color.primary_color_green));
                            lineDataSet1.setValueTextColor(getColor(R.color.primary_color_transparent));
                            lineDataSet1.setValueTextSize(10f);
                            lineDataSet1.setCircleColor(getColor(R.color.primary_color_green));
                            lineDataSet1.setCircleRadius(6f);
                            lineDataSet1.setCircleHoleColor(getColor(R.color.primary_color_white));
                            lineDataSet1.setCircleHoleRadius(3f);
                            lineDataSet1.setLineWidth(2f);

                            // construct the chart line for represent the pul data
                            LineDataSet lineDataSet2 = new LineDataSet(weeklyRecordsPulEntryList, "");
                            lineDataSet2.setColors(getColor(R.color.secondary_color_black));
                            lineDataSet2.setValueTextColor(getColor(R.color.primary_color_transparent));
                            lineDataSet2.setValueTextSize(10f);
                            lineDataSet2.setCircleColor(getColor(R.color.neutral_color_black));
                            lineDataSet2.setCircleRadius(6f);
                            lineDataSet2.setCircleHoleColor(getColor(R.color.primary_color_white));
                            lineDataSet2.setCircleHoleRadius(3f);
                            lineDataSet2.setLineWidth(2f);

                            // construct the chart line for represent the dia data
                            LineDataSet lineDataSet3 = new LineDataSet(weeklyRecordsDiaEntryList, "");
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

                            weeklyRecordsLineChart.setData(lineData);

                            List<LegendEntry> legendEntryArrayList = new ArrayList<>();
                            legendEntryArrayList.add(new LegendEntry("", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getColor(R.color.primary_color_transparent)));

                            weeklyRecordsLineChart.getLegend().setCustom(legendEntryArrayList);
                            weeklyRecordsLineChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                            weeklyRecordsLineChart.invalidate();

                            if(!weeklyRecordsDTOList.isEmpty()){
                                // exclude the no results banner
                                UiToolkitManager.excludeNoResultBanner(WeeklyRecordsActivity.this, R.id.weeklyRecordsNoResultsTextView);

                                // load all record recyclerView
                                RecyclerView allRecordsRecyclerView = findViewById(R.id.weeklyRecordsRecyclerViewAllRecords);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WeeklyRecordsActivity.this);
                                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                allRecordsRecyclerView.setLayoutManager(linearLayoutManager);

                                TimeViceAnalyseAdapter timeViceAnalyseAdapter = new TimeViceAnalyseAdapter(weeklyRecordsDTOList);
                                allRecordsRecyclerView.setAdapter(timeViceAnalyseAdapter);
                                allRecordsRecyclerView.invalidate();
                            }
                        }

                        // construct the backward button
                        ImageButton backImageButton = findViewById(R.id.weeklyRecordsImageButtonBack);
                        backImageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UiToolkitManager.ActivityManager.navigateToActivity(WeeklyRecordsActivity.this, new HomeActivity());
                            }
                        });
                    }
                });
            }
        }).start();
    }
}