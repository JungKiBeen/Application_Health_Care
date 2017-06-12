package com.yjrlab.idoll.libs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.partron.wearable.band.sdk.core.BandResultCode;
import com.partron.wearable.band.sdk.core.BandUUID;
import com.partron.wearable.band.sdk.core.ConnectionState;
import com.partron.wearable.band.sdk.core.PWB_ClientManager;
import com.partron.wearable.band.sdk.core.UserProfile;
import com.partron.wearable.band.sdk.core.interfaces.BandConnectStateCallback;
import com.partron.wearable.band.sdk.core.interfaces.OnCompleteListener;
import com.partron.wearable.band.sdk.core.interfaces.PWB_Client;
import com.partron.wearable.band.sdk.core.item.UrbanInfoItem;
import com.yjrlab.idoll.model.DataRecordModel;

/**
 * Created by yeonjukim on 2017. 6. 2..
 */

public class iDollService extends Service implements BandConnectStateCallback {
    public static final String ACTION_CONNECT_STATUS = iDollService.class.getName() + ".status";
    public static final String ACTION_TODAY_STEP = iDollService.class.getName() + ".todayStep";
    public static final String INTENT_STATUS = "status";
    public static final String INTENT_STEP = "step";


    private static final String TAG = iDollService.class.getSimpleName();
    public static final int CONNECT_DEVICE = 300;
    public static final int TODAY_STEP = 400;

    private BluetoothAdapter bluetoothAdapter;
    PWB_ClientManager clientManager;
    private PWB_Client mClient;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private AlarmManager alarmManager;

    final Messenger messenger = new Messenger(new IncomingHandler());

    @Override
    public void onCreate() {
        super.onCreate();

        if (SettingUtils.loadUserProfile(getContext()) == null) {
            stopSelf();
            return;
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Log.d(TAG, "서비스와 연결 완료!");
        getClient();
        cancelPIntent();

    }

    private PendingIntent cancelPIntent() {
        Intent intent = new Intent(getContext(), iDollService.class);
        PendingIntent pIntent = PendingIntent.getService(getContext(), CODE_PINTENT_SERVICE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pIntent);
        return pIntent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (SettingUtils.loadUserProfile(getContext()) == null) {
            stopSelf();
            return null;
        }
        startConnect();
        return messenger.getBinder();

    }

    private void startConnect() {
        if (mClient != null) {
            Log.d("####", "AAAAA");
            if (mClient.isBandConnected()) {
                Intent intent = new Intent(ACTION_CONNECT_STATUS);
                intent.putExtra(INTENT_STATUS, true);
                sendBroadcast(intent);
            } else {
                mClient.bandConnect(SettingUtils.loadBleDevice(getContext()));
                mClient.registerBandConnectStateCallback(this);
            }
        }

    }

    // mClient.bandConnect() 이후 콜백
    @Override
    public void onBandConnectState(int i, final ConnectionState connectionState) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("###", connectionState.name());
                ConnectionState state = mClient.getConnectionState();
                Intent intent = new Intent(ACTION_CONNECT_STATUS);
                switch (state) {
                    case CONNECTED:
                        intent.putExtra(INTENT_STATUS, true);
                        sendBroadcast(intent);
                        restartServiceWithSec(0);
                        Toast.makeText(getContext(), "기기와 연결되었습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case DISCONNECTED:
                        intent.putExtra(INTENT_STATUS, false);
                        sendBroadcast(intent);
                        restartServiceWithSec(10 * 60 * 1000);
                        Toast.makeText(getContext(), "기기와 해제되었습니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (SettingUtils.loadUserProfile(getContext()) == null) {
            stopSelf();
            return Service.START_NOT_STICKY;
        }
        if (bluetoothAdapter.isEnabled()) {
            Log.d("####", "블루투스가 켜져있다.");

            if (mClient.isBandConnected()) {
                Log.d("####", "벤드가 연결되어 있다.");
                startGetStep();

            } else {
                Log.d("####", "벤드가 연결되어 있지 않다.");
                startConnect();
            }
        } else {
            Log.d("####", "블루투스가 꺼져있다.");
            restartServiceWithSec(10 * 60 * 1000);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void startGetStep() {
        mClient.getUrbanMode().getUrbanInfo(new OnCompleteListener() {
            @Override
            public void onResult(int i, Object o) {
                if (i == BandResultCode.SUCCESS) {
                    UrbanInfoItem item = (UrbanInfoItem) o;

                    int step = item.getStep();
                    int calory = item.getCalories();

                    int targetSteps = SettingUtils.loadTargetSteps(getContext());
                    UserProfile profile = SettingUtils.loadUserProfile(getContext());
                    if (profile == null) {
                        Log.d("####", "등록되지 않은상태에서 서비스가 실행되었음.");
                        return;
                    }
                    int weight = profile.getWeight();
                    int height = profile.getHeight();

                    DataRecordModel model = new DataRecordModel();
                    model.setDateNow();
                    model.setStep(step);
                    model.setCalory(calory);
                    model.setTargetSteps(targetSteps);
                    model.setWeight(weight);
                    model.setHeight(height);
                    model.save(getContext());
                    Log.d("####", "저장 완료:" + step + "걸음");

                    Intent intent = new Intent(ACTION_TODAY_STEP);
                    intent.putExtra(INTENT_STEP, model);
                    sendBroadcast(intent);

                    restartServiceWithSec(60 * 60 * 1000);
                } else {
                    Intent intent = new Intent(ACTION_TODAY_STEP);
                    sendBroadcast(intent);

                    Log.d("####", "걸음수를 가져올 수 없다.");
                    restartServiceWithSec(10 * 60 * 1000);
                }
            }
        });
    }

    private static final int CODE_PINTENT_SERVICE = 7050;

    private void restartServiceWithSec(long millSce) {
        Log.d("####", millSce + "이후 다시 시작");
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + millSce, cancelPIntent());
    }


    public void getClient() {
        if (clientManager == null) {
            clientManager = PWB_ClientManager.getInstance();
            mClient = clientManager.create(getApplicationContext(), SettingUtils.loadUserProfile(getContext()), BandUUID.PWB_200);

        }
    }


    public Context getContext() {
        return this;
    }


    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECT_DEVICE:
                    startConnect();
                    break;
                case TODAY_STEP:
                    if (mClient.isBandConnected())
                        startGetStep();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


}
