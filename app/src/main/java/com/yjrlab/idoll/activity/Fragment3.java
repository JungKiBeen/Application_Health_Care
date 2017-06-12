package com.yjrlab.idoll.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yjrlab.idoll.R;
import com.yjrlab.idoll.libs.SettingUtils;

public class Fragment3 extends Fragment implements View.OnClickListener {
    private TextView textViewTarget;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment3, null, false);

        setLayout();

        return rootView;
    }

    private void setLayout() {
        textViewTarget = (TextView) rootView.findViewById(R.id.tv_target);
        rootView.findViewById(R.id.bt_send).setOnClickListener(this);
        textViewTarget.setText(SettingUtils.loadTargetSteps(getContext()) + "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_send:
                if (TextUtils.isEmpty(textViewTarget.getText().toString())) {
                    Toast.makeText(getContext(), "걸음수를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (Integer.parseInt(textViewTarget.getText().toString()) == 0) {
                        Toast.makeText(getContext(), "걸음수를 입력하세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "걸음수를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                SettingUtils.saveTargetSteps(getContext(), Integer.parseInt(textViewTarget.getText().toString()));
                Toast.makeText(getContext(), "저장완료!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
