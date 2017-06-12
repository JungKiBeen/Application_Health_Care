package com.yjrlab.idoll.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.yjrlab.idoll.R;

/**
 * Created by yeonjukim on 2017. 6. 2..
 */

public class BluetoothProgressDialog extends Dialog {
    private Context context;


    public BluetoothProgressDialog(Context context) {
        super(context);
        this.context = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_bluetooth_progress);

    }

}
