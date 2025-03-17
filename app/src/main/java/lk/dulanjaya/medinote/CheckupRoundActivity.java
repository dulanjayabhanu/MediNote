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

import lk.dulanjaya.medinote.model.CheckupRoundAdapter;
import lk.dulanjaya.medinote.model.CheckupRoundDTO;
import lk.dulanjaya.medinote.model.DataBaker;
import lk.dulanjaya.medinote.model.UiToolkitManager;

public class CheckupRoundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkup_round);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UiToolkitManager.setStatusBarAndNavigationBarColor(CheckupRoundActivity.this, R.color.primary_color_white, R.color.primary_color_white);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // get dataBakingManagers
                DataBaker.DataBakingManager<CheckupRoundDTO> dataBakingManager1 = new DataBaker.DataBakingManager<>();
                DataBaker.DataBakingManager<Entry> dataBakingManager2 = new DataBaker.DataBakingManager<>();

                // get checkupRoundDTOList from the data bank
                String checkupRoundDTOFilePath = DataBaker.DirectoryInspector.getFilePath(CheckupRoundActivity.this, 0, 0);
                List<CheckupRoundDTO> checkupRoundDTOList = dataBakingManager1.dataServe(checkupRoundDTOFilePath);

                // get checkupRoundEntryList from the data bank
                String checkupRoundEntryFilePath = DataBaker.DirectoryInspector.getFilePath(CheckupRoundActivity.this, 0, 1);
                List<Entry> checkupRoundEntryList = dataBakingManager2.dataServe(checkupRoundEntryFilePath);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!checkupRoundEntryList.isEmpty()){
                            // load line chart
                            LineChart checkupRoundLinechart = findViewById(R.id.checkupRoundLineChart);
                            checkupRoundLinechart.setDescription(null);
                            checkupRoundLinechart.setClickable(false);
                            checkupRoundLinechart.setPinchZoom(false);
                            checkupRoundLinechart.setHighlightPerDragEnabled(false);
                            checkupRoundLinechart.setHighlightPerTapEnabled(false);
                            checkupRoundLinechart.setDoubleTapToZoomEnabled(false);
                            checkupRoundLinechart.setDrawGridBackground(false);
                            checkupRoundLinechart.animateY(1500, Easing.EaseOutCirc);
                            checkupRoundLinechart.setExtraOffsets(16f, 24f, 24f, 16f);

                            // construct the Y axis left side
                            YAxis leftAxis = checkupRoundLinechart.getAxisLeft();
                            leftAxis.setAxisLineWidth(1f);
                            leftAxis.setTextColor(getColor(R.color.secondary_color_black));
                            leftAxis.setTextSize(10f);
                            leftAxis.setGridColor(getColor(R.color.primary_border_color));

                            // construct the Y axis right side
                            YAxis rightAxis = checkupRoundLinechart.getAxisRight();
                            rightAxis.setEnabled(false);

                            // construct the X axis
                            XAxis xAxis = checkupRoundLinechart.getXAxis();
                            xAxis.setAxisLineWidth(1f);
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setTextColor(getColor(R.color.secondary_color_black));
                            xAxis.setTextSize(10f);
                            xAxis.setGridColor(getColor(R.color.primary_border_color));

                            // construct the chart line
                            LineDataSet lineDataSet = new LineDataSet(checkupRoundEntryList, "");
                            lineDataSet.setColors(getColor(R.color.primary_color_green));
                            lineDataSet.setValueTextColor(getColor(R.color.primary_color_transparent));
                            lineDataSet.setValueTextSize(10f);
                            lineDataSet.setCircleColor(getColor(R.color.secondary_color_green));
                            lineDataSet.setCircleRadius(6f);
                            lineDataSet.setCircleHoleColor(getColor(R.color.primary_color_white));
                            lineDataSet.setCircleHoleRadius(3f);
                            lineDataSet.setLineWidth(2f);

                            LineData lineData = new LineData(lineDataSet);
                            lineData.setHighlightEnabled(true);

                            checkupRoundLinechart.setData(lineData);

                            List<LegendEntry> legendEntryArrayList = new ArrayList<>();
                            legendEntryArrayList.add(new LegendEntry("", Legend.LegendForm.CIRCLE, Float.NaN, Float.NaN, null, getColor(R.color.primary_color_transparent)));

                            checkupRoundLinechart.getLegend().setCustom(legendEntryArrayList);
                            checkupRoundLinechart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                            checkupRoundLinechart.invalidate();

                            if(!checkupRoundDTOList.isEmpty()){
                                // exclude the no results banner
                                UiToolkitManager.excludeNoResultBanner(CheckupRoundActivity.this, R.id.checkupRoundNoResultsTextView);

                                // load all record recyclerView
                                RecyclerView allRecordsRecyclerView = findViewById(R.id.checkupRoundRecyclerViewAllRecords);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CheckupRoundActivity.this);
                                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                allRecordsRecyclerView.setLayoutManager(linearLayoutManager);

                                CheckupRoundAdapter checkupRoundAdapter = new CheckupRoundAdapter(checkupRoundDTOList);
                                allRecordsRecyclerView.setAdapter(checkupRoundAdapter);
                                allRecordsRecyclerView.invalidate();
                            }
                        }

                        // construct the backward button
                        ImageButton backImageButton = findViewById(R.id.checkupRoundImageButtonBack);
                        backImageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UiToolkitManager.ActivityManager.navigateToActivity(CheckupRoundActivity.this, UiToolkitManager.ActivityManager.getHomeActivity());
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
            UiToolkitManager.ActivityManager.navigateToActivity(CheckupRoundActivity.this, UiToolkitManager.ActivityManager.getHomeActivity());
        }
        return super.onKeyDown(keyCode, event);
    }
}