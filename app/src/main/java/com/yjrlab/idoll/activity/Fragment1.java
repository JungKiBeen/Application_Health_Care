package com.yjrlab.idoll.activity;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.yjrlab.idoll.R;
import com.yjrlab.idoll.libs.CheckableButton;
import com.yjrlab.idoll.libs.DayAxisFormatter;
import com.yjrlab.idoll.model.DataRecordModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Fragment1 extends Fragment implements View.OnClickListener {
    LinearLayout layoutDay;

    private View rootView;
    private CheckableButton btDay;
    private CheckableButton btWeek;
    private CheckableButton btMonth;

    private TextView tvWeight;
    private TextView tvStep;
    private TextView tvCalory;

    private LineChart lineRecord;
    private BarChart barRecord;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment1, null, false);
        setLayout();
        return rootView;
    }

    private void setLayout() {
        layoutDay = (LinearLayout) rootView.findViewById(R.id.layout_day);

        btDay = (CheckableButton) rootView.findViewById(R.id.bt_day);
        btDay.setOnClickListener(this);
        btWeek = (CheckableButton) rootView.findViewById(R.id.bt_week);
        btWeek.setOnClickListener(this);
        btMonth = (CheckableButton) rootView.findViewById(R.id.bt_month);
        btMonth.setOnClickListener(this);


        //날짜 초기화
        String date = new SimpleDateFormat("MM.dd(E)").format(new Date(System.currentTimeMillis()));
        ((TextView) rootView.findViewById(R.id.tv_d_date)).setText(date);

        tvWeight = (TextView) rootView.findViewById(R.id.tv_d_weight_data);
        tvStep = (TextView) rootView.findViewById(R.id.tv_d_pbw_data);
        tvCalory = (TextView) rootView.findViewById(R.id.tv_d_calory_data);


        lineRecord = (LineChart) rootView.findViewById(R.id.lineRecord);
        barRecord = (BarChart) rootView.findViewById(R.id.barRecord);

        //화면 초기화
        setCheckView(btDay, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        setGraph();
    }

    public void setGraph() {
        if (viewType == DataRecordModel.DateType.TODAY) {
            barRecord.setVisibility(View.VISIBLE);
            lineRecord.setVisibility(View.GONE);

            DataRecordModel model = DataRecordModel.loadRecord(getContext(), new SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis()));


            ArrayList<BarEntry> yVal = new ArrayList<>();
            yVal.add(new BarEntry(0, model.getStep()));

            BarDataSet set = new BarDataSet(yVal, "오늘기록");
            set.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<BarEntry> yVal2 = new ArrayList<>();
            yVal2.add(new BarEntry(1, model.getTargetSteps()));

            BarDataSet set2 = new BarDataSet(yVal2, "목표기록");
            set2.setColors(ColorTemplate.PASTEL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set);
            dataSets.add(set2);

            BarData data = new BarData(dataSets);
            barRecord.setData(data);
            barRecord.getDescription().setText("iDoll");
            barRecord.getXAxis().setDrawLabels(false);
        } else {
            barRecord.setVisibility(View.GONE);
            lineRecord.setVisibility(View.VISIBLE);

            ArrayList<Entry> values = new ArrayList<>();
            ArrayList<Entry> valuesCal = new ArrayList<>();

            List<DataRecordModel> models = DataRecordModel.getSavedDataList(getContext(), viewType);

            for (int i = 0; i < models.size(); i++) {
                values.add(new BarEntry(i, models.get(i).getStep()));
                valuesCal.add(new BarEntry(i, models.get(i).getCalory()));
            }

            LineDataSet set1 = new LineDataSet(values, "운동량");

            // set the line to be drawn like this "- - - - - -"
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));

            LineDataSet set2 = new LineDataSet(valuesCal, "소모칼로리");

            // set the line to be drawn like this "- - - - - -"
            set2.setColor(Color.RED);
            set2.setCircleColor(Color.RED);
            set2.setLineWidth(1f);
            set2.setCircleRadius(3f);
            set2.setDrawCircleHole(false);
            set2.setValueTextSize(9f);
            set2.setDrawFilled(true);
            set2.setFormLineWidth(1f);
            set2.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            dataSets.add(set2); // add the datasets

            int startData = Integer.parseInt(models.get(0).getDate().substring(6));

            lineRecord.getXAxis().setValueFormatter(new DayAxisFormatter(startData, viewType));

            lineRecord.getXAxis().setLabelCount(models.size(), true);
            lineRecord.getXAxis().setTextSize(5);
            lineRecord.getDescription().setText("iDoll");
            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            lineRecord.setData(data);


        }

    }


    @Override
    public void onClick(View v) {
        setCheckView(btDay, false);
        setCheckView(btWeek, false);
        setCheckView(btMonth, false);

        switch (v.getId()) {
            case R.id.bt_day:
                setCheckView(btDay, true);
                break;
            case R.id.bt_week:
                setCheckView(btWeek, true);
                break;
            case R.id.bt_month:
                setCheckView(btMonth, true);
                break;


        }
    }

    private DataRecordModel.DateType viewType = DataRecordModel.DateType.TODAY;

    private void setCheckView(CheckableButton bt, boolean b) {

        SimpleDateFormat format = new SimpleDateFormat("MM.dd(E)");

        Calendar calendar = Calendar.getInstance();
        String today = format.format(calendar.getTimeInMillis());

        DataRecordModel model;
        if (bt == btDay) {
            viewType = DataRecordModel.DateType.TODAY;
            model = DataRecordModel.getSavedData(getContext(), DataRecordModel.DateType.TODAY);
            ((TextView) rootView.findViewById(R.id.tv_d_date)).setText(today);
        } else if (bt == btWeek) {
            viewType = DataRecordModel.DateType.WEEK;
            model = DataRecordModel.getSavedData(getContext(), DataRecordModel.DateType.WEEK);
            ((TextView) rootView.findViewById(R.id.tv_d_date)).setText((calendar.get(Calendar.MONTH) + 1) + "월 " + calendar.get(Calendar.WEEK_OF_MONTH) + "주 (");
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getMinimum(Calendar.DAY_OF_WEEK));
            String startDate = calendar.get(Calendar.DATE) + "";

            calendar.set(Calendar.DAY_OF_WEEK, calendar.getMaximum(Calendar.DAY_OF_WEEK));
            String endDate = calendar.get(Calendar.DATE) + "";
            ((TextView) rootView.findViewById(R.id.tv_d_date)).append(startDate + " - " + endDate + ")");

        } else {
            viewType = DataRecordModel.DateType.MONTH;
            model = DataRecordModel.getSavedData(getContext(), DataRecordModel.DateType.MONTH);
            ((TextView) rootView.findViewById(R.id.tv_d_date)).setText((calendar.get(Calendar.MONTH) + 1) + "월 " + calendar.get(Calendar.YEAR));
        }

        tvStep.setText(model.getStep() + "");
        tvCalory.setText(model.getCalory() + "");
        tvWeight.setText(model.getWeight() + "");

        if (b) {
            bt.setChecked(true);
            bt.setTextColor(Color.parseColor("#3F51B5"));

        } else {
            bt.setChecked(false);
            bt.setTextColor(Color.parseColor("#ffffff"));
        }

        setGraph();
    }


}
