package com.yjrlab.idoll.libs;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjrlab.idoll.R;
import com.yjrlab.idoll.model.DataRecordModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private JSONArray doingDietPrograms;
    private JSONArray questRecords;

    private Calendar today;

    private int year;
    private int month;
    private int lastDay;
    private int startDayOfWeek;
    private int endDayOfWeek;

    private int recordIndex;

    private OnCalendarClickListener listener;

    public CalendarAdapter() {
        today = Calendar.getInstance();
    }

    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CalendarViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_calendar, parent, false));
    }

    @Override
    public void onBindViewHolder(final CalendarViewHolder holder, final int position) {
        holder.mImageViewQuestRecord.setVisibility(View.GONE);
        holder.mTextViewStep.setText("");
        holder.mTextViewCalory.setText("");
        holder.mImageViewQuestRecord.setVisibility(View.GONE);

        final int thisDate = position - startDayOfWeek + 1;

        if (isToday(thisDate)) {
            holder.mTextViewDay.setBackgroundResource(R.drawable.bg_calendar_today);
            holder.mTextViewDay.setTextColor(Color.WHITE);
            holder.mTextViewDay.setPaintFlags(holder.mTextViewDay.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        } else {
            holder.mTextViewDay.setTextColor(Color.GRAY);
            holder.mTextViewDay.setBackground(null);
            holder.mTextViewDay.setTypeface(Typeface.DEFAULT);
        }


        if (thisDate <= 0 || thisDate > lastDay) {
            holder.mTextViewDay.setText("");
            return;
        }

        holder.mTextViewDay.setText(Integer.toString(thisDate));

        if (questRecords != null) {
            try {
                if (recordIndex < questRecords.length()) {
                    JSONObject record = (JSONObject) questRecords.get(recordIndex);
                    if (record.getLong("record_date") % 100 == thisDate) {
                        holder.mImageViewQuestRecord.setVisibility(View.VISIBLE);
                        recordIndex++;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int month = this.month + 1;
        String date = year + (month < 10 ? "0" + month : "" + month) + (thisDate < 10 ? "0" + thisDate : "" + thisDate);
        DataRecordModel model = DataRecordModel.loadRecord(holder.mTextViewDay.getContext(), date);
        holder.mTextViewStep.setText(model.getStep() + "걸음");
        holder.mTextViewCalory.setText(model.getCalory() + "kcal");

        if (model.isSuccess()) {
            holder.mImageViewQuestRecord.setVisibility(View.VISIBLE);
        } else {
            holder.mImageViewQuestRecord.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (endDayOfWeek == 1) {
            return lastDay + startDayOfWeek;
        } else if (startDayOfWeek == 7) {
            startDayOfWeek = 0;
            return lastDay + (8 - endDayOfWeek);
        } else {
            return lastDay + startDayOfWeek + (8 - endDayOfWeek);
        }
    }

    public void setDate(int year, int month) {
        this.year = year;
        this.month = month;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 0);
        startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        for (int i = 0; i < 33; i++) {
            calendar.setTimeInMillis(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000L);
            if (month != calendar.get(Calendar.MONTH)) {
                lastDay = i;
                break;
            }
        }
        endDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        this.recordIndex = 0;
        this.doingDietPrograms = null;
        this.questRecords = null;
        notifyDataSetChanged();
    }

    public void setData(JSONArray doingDietPrograms, JSONArray questRecords) {
        this.recordIndex = 0;
        this.doingDietPrograms = doingDietPrograms;
        this.questRecords = questRecords;
        notifyDataSetChanged();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
        setDate(this.year, this.month);
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
        setDate(this.year, this.month);
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder {
        private ViewGroup mLayoutMain;
        private TextView mTextViewDay;
        private TextView mTextViewStep;
        private TextView mTextViewCalory;
        private ImageView mImageViewQuestRecord;

        public CalendarViewHolder(View itemView) {
            super(itemView);
            mLayoutMain = (ViewGroup) itemView.findViewById(R.id.layoutMain);
            mTextViewDay = (TextView) itemView.findViewById(R.id.textViewDay);
            mImageViewQuestRecord = (ImageView) itemView.findViewById(R.id.imageViewQuest);
            mTextViewStep = (TextView) itemView.findViewById(R.id.textViewStep);
            mTextViewCalory = (TextView) itemView.findViewById(R.id.textViewCalory);
        }
    }

    private boolean isToday(int date) {
        return today.get(Calendar.YEAR) == year && today.get(Calendar.MONTH) == month && today.get(Calendar.DATE) == date;
    }

    private boolean isBeforeDay(int date) {
        return date <= (today.get(Calendar.YEAR) * 10000 + today.get(Calendar.MONTH) * 100 + today.get(Calendar.DATE));
    }

    public String getDate() {
        int resultMonth = month + 1;
        return year + "" + (resultMonth < 10 ? "0" + resultMonth : resultMonth);

    }

    public void setOnCalendarClickListener(OnCalendarClickListener listener) {
        this.listener = listener;
    }

    public interface OnCalendarClickListener {
        public void onCalendarClick(int date, boolean isDoingProgram);
    }
}
