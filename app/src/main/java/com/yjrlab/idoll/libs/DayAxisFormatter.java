package com.yjrlab.idoll.libs;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.yjrlab.idoll.model.DataRecordModel;

/**
 * Created by jongrakmoon on 2017. 6. 5..
 */

public class DayAxisFormatter implements IAxisValueFormatter {
    private int startDate;
    private DataRecordModel.DateType type;

    public DayAxisFormatter(int startDate, DataRecordModel.DateType type) {
        this.startDate = startDate;
        this.type = type;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int valueInt = (int) value;
        String week = "";

        if (type == DataRecordModel.DateType.WEEK) {
            switch (valueInt) {
                case 0:
                    week = "일";
                    break;
                case 1:
                    week = "월";
                    break;
                case 2:
                    week = "화";
                    break;
                case 3:
                    week = "수";
                    break;
                case 4:
                    week = "목";
                    break;
                case 5:
                    week = "금";
                    break;
                case 6:
                    week = "토";
                    break;
                default:
                    week = "";
                    break;
            }
        }


        String v = (((int) value) + startDate) + "일" + (type == DataRecordModel.DateType.WEEK ? "(" + week + ")" : "");


        return v;
    }
}
