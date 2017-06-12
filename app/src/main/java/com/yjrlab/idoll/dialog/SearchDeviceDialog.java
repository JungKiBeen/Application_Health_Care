package com.yjrlab.idoll.dialog;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.partron.wearable.band.sdk.core.BandUUID;
import com.partron.wearable.band.sdk.core.PWB_ClientManager;
import com.partron.wearable.band.sdk.core.UserProfile;
import com.partron.wearable.band.sdk.core.interfaces.BandScanCallback;
import com.partron.wearable.band.sdk.core.interfaces.PWB_Client;
import com.yjrlab.idoll.BleDeviceAdapter;
import com.yjrlab.idoll.R;
import com.yjrlab.idoll.libs.SettingUtils;
import com.yjrlab.idoll.model.DataRecordModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jongrakmoon on 2017. 6. 1..
 */

public class SearchDeviceDialog extends AppCompatDialog implements BleDeviceAdapter.ItemOnDeviceSelectedListener {

    private UserProfile userProfile;
    private PWB_Client client;
    private List<BluetoothDevice> devices;
    private BleDeviceAdapter mBleDeviceAdapter;

    public SearchDeviceDialog(Context context, UserProfile userProfile) {
        super(context);
        this.userProfile = userProfile;
    }

    public SearchDeviceDialog(Context context, int theme, UserProfile userProfile) {
        super(context, theme);
        this.userProfile = userProfile;
    }

    protected SearchDeviceDialog(Context context, boolean cancelable, OnCancelListener cancelListener, UserProfile userProfile) {
        super(context, cancelable, cancelListener);
        this.userProfile = userProfile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_search_device);

        RecyclerView mRecyclerViewDevice = (RecyclerView) findViewById(R.id.recyclerViewBleDevice);
        this.devices = new ArrayList<>();
        this.mBleDeviceAdapter = new BleDeviceAdapter(this.devices);
        this.mBleDeviceAdapter.setOnDeviceSelectedListener(this);
        mRecyclerViewDevice.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewDevice.setAdapter(mBleDeviceAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (client == null) {
            PWB_ClientManager clientManager = PWB_ClientManager.getInstance();
            client = clientManager.create(getContext().getApplicationContext(), userProfile, BandUUID.PWB_200);
            client.registerBandScanCallback(new BandScanCallback() {
                @Override
                public void onBandScanCallback(int i, BluetoothDevice bluetoothDevice, int i1) {
                    if (bluetoothDevice != null) {
                        if (!devices.contains(bluetoothDevice)) {
                            devices.add(bluetoothDevice);
                            mBleDeviceAdapter.notifyItemChanged(devices.size() - 1);

                        }
                    }
                }
            });
        }

        if (client == null) {
            Toast.makeText(getContext(), "해당 단말은 블루투스를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        client.bandScan().start(1000 * 10);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (client != null) {
            client.bandScan().stop();
        }
    }

    @Override
    public void onSelected(int position, BluetoothDevice device) {
        SettingUtils.saveBleDevice(getContext(), device.getAddress());
        SettingUtils.saveUserProfile(getContext(), userProfile);
        DataRecordModel.saveNow(getContext());
        dismiss();
    }
}
