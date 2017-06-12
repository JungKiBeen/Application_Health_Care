package com.yjrlab.idoll.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;

import com.partron.wearable.band.sdk.core.UserProfile;
import com.yjrlab.idoll.R;
import com.yjrlab.idoll.libs.SettingUtils;
import com.yjrlab.idoll.libs.iDollService;
import com.yjrlab.idoll.model.DataRecordModel;

import static com.yjrlab.idoll.libs.iDollService.ACTION_CONNECT_STATUS;
import static com.yjrlab.idoll.libs.iDollService.ACTION_TODAY_STEP;
import static com.yjrlab.idoll.libs.iDollService.INTENT_STATUS;

public class MainActivity extends AppCompatActivity {

    private static final int MAX_PAGE = 4;
    private ViewPager viewPager;
    Fragment cur_fragment;
    private Messenger messenger;

    private Thread realTimeThread;

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            messenger = new Messenger(service);
            //  textViewReceive.setEnabled(true);
        }

        public void onServiceDisconnected(ComponentName className) {
            messenger = null;
        }
    };
    private boolean isConnected;
    private BluetoothProgressDialog progressDialog;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_CONNECT_STATUS)) {
                isConnected = intent.getBooleanExtra(INTENT_STATUS, false);
                progressDialog.dismiss();
            } else if (intent.getAction().equals(ACTION_TODAY_STEP)) {
                cur_fragment.onResume();
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        setLayout();
        registerReceiver(receiver, new IntentFilter(ACTION_CONNECT_STATUS));
        if (realTimeThread != null) {
            realTimeThread.interrupt();
        }

        realTimeThread = new Thread(realtimeRecrodTask);
        realTimeThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        if (realTimeThread != null) {
            realTimeThread.interrupt();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setLayout();
        bindService(new Intent(this, iDollService.class), connection, Context.BIND_AUTO_CREATE);
        progressDialog = new BluetoothProgressDialog(this);
        progressDialog.show();

    }


    private void setLayout() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new adapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(1);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                cur_fragment = fragmentSparseArray.get(position);
                if (cur_fragment != null)
                    cur_fragment.onResume();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    SparseArray<Fragment> fragmentSparseArray = new SparseArray<>();


    private class adapter extends FragmentPagerAdapter {
        public adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position < 0 || MAX_PAGE <= position)
                return null;
            switch (position) {
                case 0:
                    fragmentSparseArray.put(0, new Fragment1());
                    break;
                case 1:
                    fragmentSparseArray.put(1, new Fragment2());
                    break;
                case 2:
                    fragmentSparseArray.put(2, new Fragment3());
                    break;
                case 3:
                    fragmentSparseArray.put(3, new Fragment4());
                    break;
            }
            return fragmentSparseArray.get(position);
        }

        @Override
        public int getCount() {
            return MAX_PAGE;
        }
    }

    Runnable realtimeRecrodTask = new Runnable() {
        @Override
        public void run() {
            try {
                int i = 0;
                for (; ; ) {
                    i++;
                    final int index = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (messenger != null) {
                                    Message message = Message.obtain();
                                    message.what = iDollService.TODAY_STEP;
                                    messenger.send(message);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {

            }


        }
    };


}
