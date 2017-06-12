package com.yjrlab.idoll;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jongrakmoon on 2017. 6. 1..
 */

public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.BleDeviceViewHolder> {

    private List<BluetoothDevice> devices;
    private ItemOnDeviceSelectedListener listener;

    public BleDeviceAdapter(@Nullable List<BluetoothDevice> devices) {
        this.devices = devices;
    }

    public void setDevices(@Nullable List<BluetoothDevice> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    public List<BluetoothDevice> getDevices() {
        return devices;
    }

    public void setOnDeviceSelectedListener(ItemOnDeviceSelectedListener listener) {
        this.listener = listener;
    }


    @Override
    public BleDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BleDeviceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_ble_device, parent, false));
    }

    @Override
    public void onBindViewHolder(BleDeviceViewHolder holder, final int position) {
        final BluetoothDevice device = devices.get(position);

        holder.mTextViewName.setText(device.getName() == null ? "알 수 없음" : device.getName());
        holder.mTextViewAddress.setText(device.getAddress());
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSelected(position, device);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        if (devices == null) {
            return 0;
        } else {
            return devices.size();
        }
    }

    class BleDeviceViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewName;
        private TextView mTextViewAddress;
        private View parentView;

        BleDeviceViewHolder(View itemView) {
            super(itemView);
            this.parentView = itemView;
            this.mTextViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.mTextViewAddress = (TextView) itemView.findViewById(R.id.textViewAddress);
        }
    }

    public interface ItemOnDeviceSelectedListener {
        void onSelected(int position, BluetoothDevice device);
    }
}
