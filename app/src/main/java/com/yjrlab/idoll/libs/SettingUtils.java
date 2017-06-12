package com.yjrlab.idoll.libs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.partron.wearable.band.sdk.core.UserProfile;

/**
 * Created by jongrakmoon on 2017. 6. 1..
 */

public class SettingUtils {

    private static final String SETTING_TAG = "SETTING";

    private static final String PREFERENCE_KEY_GENDER = "gender";
    private static final String PREFERENCE_KEY_WEIGHT = "weight";
    private static final String PREFERENCE_KEY_HEIGHT = "height";
    private static final String PREFERENCE_KEY_AGE = "age";
    private static final String PREFERENCE_KEY_TAGEET_STEPS = "targetSteps";


    private static final String PREFERENCE_KEY_DEVICE = "device";


    public static void saveUserProfile(@NonNull Context context, @NonNull UserProfile profile) {
        context.getSharedPreferences(SETTING_TAG, Context.MODE_PRIVATE)
                .edit()
                .putInt(PREFERENCE_KEY_GENDER, profile.getGender())
                .putInt(PREFERENCE_KEY_AGE, profile.getAge())
                .putInt(PREFERENCE_KEY_HEIGHT, profile.getHeight())
                .putInt(PREFERENCE_KEY_WEIGHT, profile.getWeight())
                .apply();
    }

    @Nullable
    public static UserProfile loadUserProfile(@NonNull Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTING_TAG, Context.MODE_PRIVATE);
        UserProfile profile = new UserProfile();

        int gender = preferences.getInt(PREFERENCE_KEY_GENDER, -1);
        int age = preferences.getInt(PREFERENCE_KEY_AGE, -1);
        int height = preferences.getInt(PREFERENCE_KEY_HEIGHT, -1);
        int weight = preferences.getInt(PREFERENCE_KEY_WEIGHT, -1);

        if (age < 0) {
            return null;
        }

        profile.setGender(gender);
        profile.setAge(age);
        profile.setHeight(height);
        profile.setWeight(weight);

        return profile;
    }

    public static void saveBleDevice(@NonNull Context context, String address) {
        context.getSharedPreferences(SETTING_TAG, Context.MODE_PRIVATE)
                .edit()
                .putString(PREFERENCE_KEY_DEVICE, address)
                .apply();
    }

    @Nullable
    public static String loadBleDevice(@NonNull Context context) {
        return context.getSharedPreferences(SETTING_TAG, Context.MODE_PRIVATE)
                .getString(PREFERENCE_KEY_DEVICE, null);
    }

    public static void saveTargetSteps(@NonNull Context context, int targetSteps) {
        context.getSharedPreferences(SETTING_TAG, Context.MODE_PRIVATE)
                .edit()
                .putInt(PREFERENCE_KEY_TAGEET_STEPS, targetSteps)
                .apply();
    }

    @Nullable
    public static int loadTargetSteps(@NonNull Context context) {
        return context.getSharedPreferences(SETTING_TAG, Context.MODE_PRIVATE)
                .getInt(PREFERENCE_KEY_TAGEET_STEPS, 5000);
    }


}
