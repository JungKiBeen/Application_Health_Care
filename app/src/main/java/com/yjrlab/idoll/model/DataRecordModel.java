package com.yjrlab.idoll.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.partron.wearable.band.sdk.core.UserProfile;
import com.yjrlab.idoll.libs.SettingUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jongrakmoon on 2017. 6. 4..
 */

public class DataRecordModel implements Parcelable {

    public static final String TAG = DataRecordModel.class.getSimpleName();

    private static final String PREFERENCE_STEP = "step";
    private static final String PREFERENCE_CALORY = "calory";
    private static final String PREFERENCE_WEIGHT = "weight";
    private static final String PREFERENCE_HEIGHT = "height";
    private static final String PREFERENCE_TARGET_STEPS = "targetSteps";

    private static final String PREFERENCE_PRE_CALORY = "preCalory";

    public int getPreCalory() {
        return preCalory;
    }

    public void setPreCalory(int preCalory) {
        this.preCalory = preCalory;
    }

    public enum DateType {
        TODAY, WEEK, MONTH;
    }

    private String date;
    private int step;
    private int calory;
    private int weight;
    private int height;
    private int targetSteps;
    private int preCalory;

    public void save(Context context) {
        DataRecordModel preModel = DataRecordModel.loadRecord(context, this.date);
        if (this.calory - preModel.preCalory > 1) {
            this.preCalory = this.calory;
            this.weight = preModel.weight - 10;
        } else {
            this.preCalory = preModel.preCalory;
            this.weight = preModel.weight;
        }

        context.getSharedPreferences(TAG, Context.MODE_PRIVATE)
                .edit()
                .putInt(date + PREFERENCE_STEP, step)
                .putInt(date + PREFERENCE_CALORY, calory)
                .putInt(date + PREFERENCE_WEIGHT, weight)
                .putInt(date + PREFERENCE_HEIGHT, height)
                .putInt(date + PREFERENCE_TARGET_STEPS, targetSteps)
                .putInt(date + PREFERENCE_PRE_CALORY, preCalory)
                .apply();
    }

    public static void saveNow(Context context) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String date = format.format(System.currentTimeMillis());
        UserProfile profile = SettingUtils.loadUserProfile(context);
        int target = SettingUtils.loadTargetSteps(context);
        if (profile != null) {
            context.getSharedPreferences(TAG, Context.MODE_PRIVATE)
                    .edit()
                    .putInt(date + PREFERENCE_STEP, 0)
                    .putInt(date + PREFERENCE_CALORY, 0)
                    .putInt(date + PREFERENCE_WEIGHT, profile.getWeight())
                    .putInt(date + PREFERENCE_HEIGHT, profile.getHeight())
                    .putInt(date + PREFERENCE_TARGET_STEPS, target)
                    .putInt(date + PREFERENCE_PRE_CALORY, 0)
                    .apply();
        }

    }

    @NonNull
    public static DataRecordModel loadRecord(Context context, String date) {

        int weight = 0;
        int height = 0;
        UserProfile profile = SettingUtils.loadUserProfile(context);
        if (profile != null) {
            weight = profile.getWeight();
            height = profile.getHeight();
        }

        DataRecordModel model = new DataRecordModel();

        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        model.step = preferences.getInt(date + PREFERENCE_STEP, 0);
        model.calory = preferences.getInt(date + PREFERENCE_CALORY, 0);
        model.weight = preferences.getInt(date + PREFERENCE_WEIGHT, weight);
        model.height = preferences.getInt(date + PREFERENCE_HEIGHT, height);
        model.targetSteps = preferences.getInt(date + PREFERENCE_TARGET_STEPS, 5000);
        model.preCalory = preferences.getInt(date + PREFERENCE_PRE_CALORY, 0);
        model.date = date;

        return model;

    }

    public boolean isSuccess() {
        return step != 0 && step >= targetSteps;
    }

    public void setDateNow() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        this.date = format.format(System.currentTimeMillis());
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getCalory() {
        return calory;
    }

    public void setCalory(int calory) {
        this.calory = calory;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getTargetSteps() {
        return targetSteps;
    }

    public void setTargetSteps(int targetSteps) {
        this.targetSteps = targetSteps;
    }

    public static DataRecordModel getSavedData(Context context, DateType dateType) {

        UserProfile profile = SettingUtils.loadUserProfile(context);
        if (profile == null) {
            return null;
        }

        List<DataRecordModel> recordModels = getSavedDataList(context, dateType);


        int step = 0;
        int calory = 0;
        int weight = recordModels.get(recordModels.size() - 1).weight;
        int height = profile.getHeight();
        int targetSteps = SettingUtils.loadTargetSteps(context);


        for (DataRecordModel model : recordModels) {
            step += model.getStep();
            calory += model.getCalory();
        }

        DataRecordModel result = new DataRecordModel();
        result.setDateNow();
        result.setStep(step);
        result.setCalory(calory);
        result.setWeight(weight);
        result.setHeight(height);
        result.setTargetSteps(targetSteps);

        return result;

    }

    public static List<DataRecordModel> getSavedDataList(Context context, DateType dateType) {

        UserProfile profile = SettingUtils.loadUserProfile(context);
        if (profile == null) {
            return null;
        }

        List<DataRecordModel> recordModels = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

        Calendar calendar = Calendar.getInstance();
        switch (dateType) {
            case TODAY:
                recordModels.add(loadRecord(context, format.format(calendar.getTimeInMillis())));
                break;
            case WEEK:
                int startWeekDate = calendar.getMinimum(Calendar.DAY_OF_WEEK);
                int lastWeekDate = calendar.getMaximum(Calendar.DAY_OF_WEEK);
                for (int i = startWeekDate; i <= lastWeekDate; i++) {
                    calendar.set(Calendar.DAY_OF_WEEK, i);
                    recordModels.add(loadRecord(context, format.format(calendar.getTimeInMillis())));
                }
                break;
            case MONTH:
                int startMonthDate = calendar.getMinimum(Calendar.DAY_OF_MONTH);
                int lastMonthDate = calendar.getMaximum(Calendar.DAY_OF_MONTH);

                for (int i = startMonthDate; i <= lastMonthDate; i++) {
                    calendar.set(Calendar.DAY_OF_MONTH, i);
                    recordModels.add(loadRecord(context, format.format(calendar.getTimeInMillis())));
                }
                break;
        }


        return recordModels;

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeInt(this.step);
        dest.writeInt(this.calory);
        dest.writeInt(this.weight);
        dest.writeInt(this.height);
        dest.writeInt(this.targetSteps);
    }

    public DataRecordModel() {
    }

    protected DataRecordModel(Parcel in) {
        this.date = in.readString();
        this.step = in.readInt();
        this.calory = in.readInt();
        this.weight = in.readInt();
        this.height = in.readInt();
        this.targetSteps = in.readInt();
    }

    public static final Parcelable.Creator<DataRecordModel> CREATOR = new Parcelable.Creator<DataRecordModel>() {
        @Override
        public DataRecordModel createFromParcel(Parcel source) {
            return new DataRecordModel(source);
        }

        @Override
        public DataRecordModel[] newArray(int size) {
            return new DataRecordModel[size];
        }
    };
}
