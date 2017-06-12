package com.yjrlab.idoll.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by jongrakmoon on 2017. 6. 1..
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "###";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, getClass().getSimpleName());

    }

    protected Context getContext() {
        return this;
    }
}
