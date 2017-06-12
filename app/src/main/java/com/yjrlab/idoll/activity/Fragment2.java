package com.yjrlab.idoll.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.database.DatabaseUtilsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.partron.wearable.band.sdk.core.UserProfile;
import com.yjrlab.idoll.R;
import com.yjrlab.idoll.libs.SettingUtils;
import com.yjrlab.idoll.model.DataRecordModel;

public class Fragment2 extends Fragment implements View.OnClickListener {

    public static final String INTENT_FROM_F2 = "f2";
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment2, null, false);
        setLayout();
        return rootView;
    }

    private void setLayout() {
        DataRecordModel model = DataRecordModel.getSavedData(getContext(), DataRecordModel.DateType.TODAY);

        ((TextView) rootView.findViewById(R.id.tv_weight)).setText(model.getWeight() + "kg");
        ((TextView) rootView.findViewById(R.id.tv_calory)).setText(model.getCalory() + "kcal");
        ((TextView) rootView.findViewById(R.id.tv_target)).setText(model.getTargetSteps() + "step");

        float bmi = 10000 * ((float) model.getWeight() / (model.getHeight() * model.getHeight()));
        setImageView(bmi);

        rootView.findViewById(R.id.bt_switch).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setLayout();
    }

    private void setImageView(float bmi) {
        int imageId;
        if (bmi < 18.5) {
            imageId = R.drawable.image_1;
        } else if (bmi < 25) {
            imageId = R.drawable.image_2;
        } else if (bmi < 35) {
            imageId = R.drawable.image_3;
        } else if (bmi < 40) {
            imageId = R.drawable.image_4;
        } else {
            imageId = R.drawable.image_5;
        }
        ((ImageView) rootView.findViewById(R.id.img_model)).setImageResource(imageId);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_switch:
                Intent intent = new Intent(getContext(), UserProfileActivity.class);
                intent.putExtra(INTENT_FROM_F2, INTENT_FROM_F2);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }
}
