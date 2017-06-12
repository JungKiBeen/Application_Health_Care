package com.yjrlab.idoll.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.yjrlab.idoll.R;
import com.yjrlab.idoll.libs.CalendarAdapter;
import com.yjrlab.idoll.libs.CalendarAdapter.OnCalendarClickListener;

import java.util.Calendar;

public class Fragment4 extends Fragment implements OnCalendarClickListener {

    private View view;
    private CalendarAdapter mCalendarAdapter;
    private int firstYear = 2016;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment4, null,false);
        setCalendar();
        return view;
    }

    private void setCalendar() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        RecyclerView mRecyclerViewCalendar = (RecyclerView) view.findViewById(R.id.recyclerViewCalendar);
        mRecyclerViewCalendar.setLayoutManager(new GridLayoutManager(getContext(), 7));
        mCalendarAdapter = new CalendarAdapter();
        mRecyclerViewCalendar.setAdapter(mCalendarAdapter);
        mCalendarAdapter.setDate(year, month);
        mCalendarAdapter.setOnCalendarClickListener(this);


        Spinner mSpinnerYear = (Spinner) view.findViewById(R.id.spinnerYear);
        Spinner mSpinnerMonth = (Spinner) view.findViewById(R.id.spinnerMonth);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        for (int i = firstYear; i < 2020; i++) {
            yearAdapter.add(Integer.toString(i) + "년");
        }

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        for (int i = 1; i < 13; i++) {
            monthAdapter.add(Integer.toString(i) + "월");
        }

        mSpinnerYear.setAdapter(yearAdapter);
        mSpinnerYear.setSelection(yearAdapter.getPosition(year + "년"));
        mSpinnerMonth.setAdapter(monthAdapter);
        mSpinnerMonth.setSelection(month);
    }

    @Override
    public void onCalendarClick(int date, boolean isDoingProgram) {

    }
}
