package com.tishcn.fimonitor.ui;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.tishcn.fimonitor.R;
import com.tishcn.fimonitor.sql.DBTask;
import com.tishcn.fimonitor.sql.FiMonitorContract;
import com.tishcn.fimonitor.sql.FiMonitorDbHelper;
import com.tishcn.fimonitor.sql.FiMonitorDbRunnable;
import com.tishcn.fimonitor.util.Constants;
import com.tishcn.fimonitor.util.DateFormat;
import com.tishcn.fimonitor.util.FiMonitor;

import java.util.ArrayList;

/**
 * Created by leona on 9/10/2016.
 */
public class StatisticsFragment extends Fragment {

    private PieChart mChart;
    private TextView mTotalTime;
    private TextView mTimeSprint;
    private TextView mTimeTmobile;
    private TextView mTimeUSCell;
    private TextView mTimeUnknown;
    private TextView mTimeUnknownLabel;
    private TextView mTotalTimeDiscon;
    private Button mClearStatsBtn;
    private Button mRefreshStatsBtn;
    private SharedPreferences mPrefs;
    private int mChartSize;
    private boolean mChartHole;
    private boolean hasLabels = true;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = true;
    private boolean hasCenterText1 = false;
    private boolean hasCenterText2 = false;
    private boolean isExploded = true;
    private boolean hasLabelForSelected = false ;
    private boolean hasValueLabelBackground = true;
    private Typeface tf;

    public StatisticsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        //Spinner durationSpin = (Spinner) rootView.findViewById(R.id.stats_duration_spinner);

        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.stats_duration_spinner_vals, android.R.layout.simple_spinner_dropdown_item);

        //durationSpin.setAdapter(durationAdapter);
        //durationSpin.setOnItemSelectedListener(this);

        mChart = (PieChart) rootView.findViewById(R.id.stats_chart1);
        mTotalTime = (TextView) rootView.findViewById(R.id.stats_total_time);
        mTimeSprint = (TextView) rootView.findViewById(R.id.stats_total_time_sprint);
        mTimeTmobile = (TextView) rootView.findViewById(R.id.stats_total_time_tmobile);
        mTimeUSCell = (TextView) rootView.findViewById(R.id.stats_total_time_uscell);
        mTotalTimeDiscon = (TextView) rootView.findViewById(R.id.stats_total_time_disconnected);
        mTimeUnknown = (TextView) rootView.findViewById(R.id.stats_total_time_unknown);
        mTimeUnknownLabel = (TextView) rootView.findViewById(R.id.stats_total_time_unknown_label);
        mClearStatsBtn = (Button) rootView.findViewById(R.id.stats_clear_btn);
        mClearStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DBTask().execute(new FiMonitorDbRunnable() {
                    @Override
                    public void executeDBTask() {
                        FiMonitorDbHelper dbHelper = new FiMonitorDbHelper(getContext());
                        dbHelper.deleteAllStatRows();
                        FiMonitor fiMonitor = new FiMonitor(getContext());
                        dbHelper.insertStatRow(fiMonitor.getNetworkOperator());
                    }

                    @Override
                    public void postExecuteDBTask() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setupChart();
                            }
                        }, 1000);
                    }
                });
            }
        });
        mRefreshStatsBtn = (Button) rootView.findViewById(R.id.stats_refresh_btn);
        mRefreshStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupChart();
            }
        });

        return rootView;
    }

        @Override
        public void onResume() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mChartSize = Integer.parseInt(mPrefs.getString(Constants.PREF_STAT_CHART_SIZE, Constants.PREF_STAT_CHART_SIZE_DEFAULT));
        mChartHole = mPrefs.getBoolean(Constants.PREF_STAT_CHART_HOLE, false);
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mChartSize, r.getDisplayMetrics());
        mChart.setMinimumHeight(px);
        setupChart();
        super.onResume();
    }

    private void setupChart() {
        mRefreshStatsBtn.setEnabled(false);
        mClearStatsBtn.setEnabled(false);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setDrawHoleEnabled(mChartHole);
        mChart.setHoleColor(Color.WHITE);
        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);
        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);
        mChart.setDrawCenterText(false);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        setData();
        //mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        mChart.spin(600, mChart.getRotationAngle(), mChart.getRotationAngle() + 360, Easing.EasingOption
                .EaseInCubic);
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
        mRefreshStatsBtn.setEnabled(true);
        mClearStatsBtn.setEnabled(true);
    }

    private void setData() {

        final Cursor[] cursor = new Cursor[1];
        final long[] totalTime = new long[1];
        totalTime[0] = 0;
        final long[] sprintTime = new long[1];
        sprintTime[0] = 0;
        final long[] tmobileTime = new long[1];
        tmobileTime[0] = 0;
        final long[] uscellTime = new long[1];
        uscellTime[0] = 0;
        final long[] unknownTime = new long[1];
        unknownTime[0] = 0;
        final long[] disconnectTime = new long[1];
        disconnectTime[0] = 0;
        final int[] rowCount = new int[1];

        new DBTask().execute(new FiMonitorDbRunnable() {
            @Override
            public void executeDBTask() {
                FiMonitorDbHelper db = new FiMonitorDbHelper(getContext());
                cursor[0] = db.getStatsRows();
                rowCount[0] = db.getStatsRowCount();
            }

            @Override
            public void postExecuteDBTask() {
                FiMonitor fiMonitor = new FiMonitor(getContext());
                String networkOperatorName = "";
                long startTime;
                long endTime;
                boolean endTimeNull;
                long difference = 0;
                cursor[0].moveToPosition(-1);
                while(cursor[0].moveToNext()){
                    networkOperatorName = fiMonitor.getNetworkOperatorName(cursor[0].getString(
                            cursor[0].getColumnIndex(FiMonitorContract.StatsTable
                                    .COLUMN_NAME_STAT_MCCMNC)));
                    startTime = cursor[0].getLong(
                            cursor[0].getColumnIndex(FiMonitorContract.StatsTable
                                    .COLUMN_NAME_STAT_START_TIME));
                    endTimeNull = cursor[0].isNull(cursor[0].getColumnIndex(FiMonitorContract.
                            StatsTable.COLUMN_NAME_STAT_END_TIME));
                    if(endTimeNull){
                        endTime = System.currentTimeMillis();
                    } else {
                        endTime = cursor[0].getLong(cursor[0].getColumnIndex(FiMonitorContract.StatsTable
                                .COLUMN_NAME_STAT_END_TIME));
                    }
                    difference = endTime - startTime;
                    totalTime[0] = totalTime[0] + difference;
                    switch (networkOperatorName){
                        case Constants.NETWORK_OPERATOR_NAME_SPRINT:
                            sprintTime[0] = sprintTime[0] + difference;
                            break;
                        case Constants.NETWORK_OPERATOR_NAME_TMOBILE:
                            tmobileTime[0] = tmobileTime[0] + difference;
                            break;
                        case Constants.NETWORK_OPERATOR_NAME_US_CELLULAR:
                            uscellTime[0] = uscellTime[0] + difference;
                            break;
                        case Constants.NETWORK_OPERATOR_NAME_UNKNOWN:
                            unknownTime[0] = unknownTime[0] + difference;
                            break;
                        default:
                            disconnectTime[0] = disconnectTime[0] + difference;
                            break;
                    }
                }
                mTotalTime.setText(DateFormat.durationFormat(totalTime[0]));
                mTimeSprint.setText(DateFormat.durationFormat(sprintTime[0]));
                mTimeTmobile.setText(DateFormat.durationFormat(tmobileTime[0]));
                mTimeUSCell.setText(DateFormat.durationFormat(uscellTime[0]));
                mTotalTimeDiscon.setText(DateFormat.durationFormat(disconnectTime[0]));
                mTimeUnknown.setText(DateFormat.durationFormat(unknownTime[0]));
                ArrayList<PieEntry> entries = new ArrayList<>();
                ArrayList<Integer> colors = new ArrayList<>();

                if(sprintTime[0] > 0){
                    //float percent = (((float) sprintTime[0]) / ((float) totalTime[0])) * 100.0f;
                    PieEntry entry = new PieEntry(sprintTime[0], Constants.NETWORK_OPERATOR_NAME_SPRINT);
                    entries.add(entry);
                    colors.add(ContextCompat.getColor(getContext(), R.color.colorSprint));
                }
                if(tmobileTime[0] > 0){
                    //float percent = (((float) tmobileTime[0]) / ((float) totalTime[0])) * 100.0f;
                    PieEntry entry = new PieEntry(tmobileTime[0], Constants.NETWORK_OPERATOR_NAME_TMOBILE);
                    entries.add(entry);
                    colors.add(ContextCompat.getColor(getContext(), R.color.colorTMobile));
                }
                if(uscellTime[0] > 0){
                    //float percent = (((float) uscellTime[0]) / ((float) totalTime[0])) * 100.0f;
                    PieEntry entry = new PieEntry(uscellTime[0], Constants.NETWORK_OPERATOR_NAME_US_CELLULAR);
                    entries.add(entry);
                    colors.add(ContextCompat.getColor(getContext(), R.color.colorUSCellular));
                }
                if(unknownTime[0] > 0){
                    mTimeUnknown.setVisibility(View.VISIBLE);
                    mTimeUnknownLabel.setVisibility(View.VISIBLE);
                    //float percent = (((float) unknownTime[0]) / ((float) totalTime[0])) * 100.0f;
                    PieEntry entry = new PieEntry(unknownTime[0], Constants.NETWORK_OPERATOR_NAME_UNKNOWN);
                    entries.add(entry);
                    colors.add(ContextCompat.getColor(getContext(), R.color.colorOther));
                }
                if(disconnectTime[0] > 0){
                    //float percent = (((float) disconnectTime[0]) / ((float) totalTime[0])) * 100.0f;
                    PieEntry entry = new PieEntry(disconnectTime[0], Constants.STATS_ACTION_DISCONNECT);
                    entries.add(entry);
                    colors.add(ContextCompat.getColor(getContext(), R.color.colorDisconnected));
                }

                PieDataSet dataSet = new PieDataSet(entries, "Cellular Connection Rate by Carrier");
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);
                dataSet.setColors(colors);
                dataSet.setValueLinePart1OffsetPercentage(80.f);
                dataSet.setValueLinePart1Length(0.2f);
                dataSet.setValueLinePart2Length(0.4f);
                dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                dataSet.setValueTextColor(Color.BLACK);
                PieData data = new PieData(dataSet);
                data.setValueFormatter(new PercentFormatter());
                data.setValueTextSize(10f);
                data.setValueTextColor(Color.BLACK);
                data.setValueTypeface(tf);
                mChart.setData(data);
                mChart.highlightValues(null);
                mChart.invalidate();
            }
        });
    }
}
