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

public class DailyRecordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daily_records);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UiToolkitManager.setStatusBarAndNavigationBarColor(DailyRecordsActivity.this, R.color.primary_color_white, R.color.primary_color_white);

        new Thread(new Runnable(){
            @Override
            public void run(){
                // get dataBakingManagers
                DataBaker.DataBakingManager<TimeViceAnalyseDTO> dataBakingManager1 = new DataBaker.DataBakingManager<>();
                DataBaker.DataBakingManager<Entry> dataBakingManager2 = new DataBaker.DataBakingManager<>();

                // get all file path
                String dailyRecordsDTOFilePath = DataBaker.DirectoryInspector.getFilePath(DailyRecordsActivity.this, 1, 0);
                List<TimeViceAnalyseDTO> dailyRecordsDTOList = dataBakingManager1.dataServe(dailyRecordsDTOFilePath);

                String dailyRecordsSysEntryFilePath = DataBaker.DirectoryInspector.getFilePath(DailyRecordsActivity.this, 1, 1);
                List<Entry> dailyRecordsSysEntryList = dataBakingManager2.dataServe(dailyRecordsSysEntryFilePath);

                String dailyRecordsPulEntryFilePath = DataBaker.DirectoryInspector.getFilePath(DailyRecordsActivity.this, 1, 2);
                List<Entry> dailyRecordsPulEntryList = dataBakingManager2.dataServe(dailyRecordsPulEntryFilePath);

                String dailyRecordsDiaEntryFilePath = DataBaker.DirectoryInspector.getFilePath(DailyRecordsActivity.this, 1, 3);
                List<Entry> dailyRecordsDiaEntryList = dataBakingManager2.dataServe(dailyRecordsDiaEntryFilePath);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!dailyRecordsSysEntryList.isEmpty() &&
                                !dailyRecordsPulEntryList.isEmpty() &&
                                !dailyRecordsDiaEntryList.isEmpty()
                        ){
                            // load line chart
                            LineChart dailyRecordsLineChart = findViewById(R.id.dailyRecordsLineChart);
                            dailyRecordsLineChart.setDescription(null);
                            dailyRecordsLineChart.setClickable(false);
                            dailyRecordsLineChart.setPinchZoom(false);
                            dailyRecordsLineChart.setHighlightPerDragEnabled(false);
                            dailyRecordsLineChart.setHighlightPerTapEnabled(false);
                            dailyRecordsLineChart.setDoubleTapToZoomEnabled(false);
                            dailyRecordsLineChart.setDrawGridBackground(false);
                            dailyRecordsLineChart.animateY(1500, Easing.EaseOutCirc);
                            dailyRecordsLineChart.setExtraOffsets(16f, 24f, 24f, 16f);

                            // construct the Y axis left side
                            YAxis leftAxis = dailyRecordsLineChart.getAxisLeft();
                            leftAxis.setAxisLineWidth(1f);
                            leftAxis.setTextColor(getColor(R.color.secondary_color_black));
                            leftAxis.setTextSize(10f);
                            leftAxis.setGridColor(getColor(R.color.primary_border_color));

                            // construct the Y axis right side
                            YAxis rightAxis = dailyRecordsLineChart.getAxisRight();
                            rightAxis.setEnabled(false);

                            // construct the X axis
                            XAxis xAxis = dailyRecordsLineChart.getXAxis();
                            xAxis.setAxisLineWidth(1f);
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setTextColor(getColor(R.color.secondary_color_black));
                            xAxis.setTextSize(10f);
                            xAxis.setGridColor(getColor(R.color.primary_border_color));

                            // construct the sys chart line
                            LineDataSet lineDataSet1 = new LineDataSet(dailyRecordsSysEntryList, "");
                            lineDataSet1.setColors(getColor(R.color.primary_color_green));
                            lineDataSet1.setValueTextColor(getColor(R.color.primary_color_transparent));
                            lineDataSet1.setValueTextSize(10f);
                            lineDataSet1.setCircleColor(getColor(R.color.primary_color_green));
                            lineDataSet1.setCircleRadius(6f);
                            lineDataSet1.setCircleHoleColor(getColor(R.color.primary_color_white));
                            lineDataSet1.setCircleHoleRadius(3f);
                            lineDataSet1.setLineWidth(2f);

                            // construct the pul chart line
                            LineDataSet lineDataSet2 = new LineDataSet(dailyRecordsPulEntryList, "");
                            lineDataSet2.setColors(getColor(R.color.secondary_color_black));
                            lineDataSet2.setValueTextColor(getColor(R.color.primary_color_transparent));
                            lineDataSet2.setValueTextSize(10f);
                            lineDataSet2.setCircleColor(getColor(R.color.neutral_color_black));
                            lineDataSet2.setCircleRadius(6f);
                            lineDataSet2.setCircleHoleColor(getColor(R.color.primary_color_white));
                            lineDataSet2.setCircleHoleRadius(3f);
                            lineDataSet2.setLineWidth(2f);

                            // construct the dia chart line
                            LineDataSet lineDataSet3 = new LineDataSet(dailyRecordsDiaEntryList, "");
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

                            dailyRecordsLineChart.setData(lineData);

                            List<LegendEntry> legendEntryArrayList = new ArrayList<>();
                            legendEntryArrayList.add(new LegendEntry("", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getColor(R.color.primary_color_transparent)));

                            dailyRecordsLineChart.getLegend().setCustom(legendEntryArrayList);
                            dailyRecordsLineChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                            dailyRecordsLineChart.invalidate();

                            // load all record recyclerView
                            if(!dailyRecordsDTOList.isEmpty()){
                                // exclude the no results banner
                                UiToolkitManager.excludeNoResultBanner(DailyRecordsActivity.this, R.id.dailyRecordsNoResultsTextView);

                                RecyclerView allRecordsRecyclerView = findViewById(R.id.dailyRecordsRecyclerViewAllRecords);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DailyRecordsActivity.this);
                                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                allRecordsRecyclerView.setLayoutManager(linearLayoutManager);

                                TimeViceAnalyseAdapter timeViceAnalyseAdapter = new TimeViceAnalyseAdapter(dailyRecordsDTOList);
                                allRecordsRecyclerView.setAdapter(timeViceAnalyseAdapter);
                                allRecordsRecyclerView.invalidate();
                            }
                        }

                        // construct the backward button
                        ImageButton backImageButton = findViewById(R.id.dailyRecordsImageButtonBack);
                        backImageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UiToolkitManager.ActivityManager.navigateToActivity(DailyRecordsActivity.this, new HomeActivity());
                            }
                        });
                    }
                });
            }
        }).start();

    }
}