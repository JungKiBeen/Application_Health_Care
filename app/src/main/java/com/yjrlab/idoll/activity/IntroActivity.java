package com.yjrlab.idoll.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.partron.wearable.band.sdk.core.BandResultCode;
import com.partron.wearable.band.sdk.core.interfaces.OnCompleteListener;
import com.partron.wearable.band.sdk.core.interfaces.PWB_Client;
import com.partron.wearable.band.sdk.core.item.UrbanInfoItem;
import com.yjrlab.idoll.R;
import com.yjrlab.idoll.libs.SettingUtils;

import java.util.Calendar;

/**
 * Created by jongrakmoon on 2017. 6. 1..
 */

public class IntroActivity extends BaseActivity {
    private Thread mIntroTimer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        mIntroTimer = new Thread(new IntroTimer());
        mIntroTimer.start();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIntroTimer != null) {
            mIntroTimer.interrupt();
        }
    }

    private class IntroTimer implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(1500);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (SettingUtils.loadUserProfile(getContext()) == null) {
                            startActivity(new Intent(getContext(), UserProfileActivity.class));
                        } else {
                            startActivity(new Intent(getContext(), MainActivity.class));
                        }

                        finish();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
