package com.yjrlab.idoll.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.partron.wearable.band.sdk.core.UserProfile;
import com.yjrlab.idoll.R;
import com.yjrlab.idoll.dialog.SearchDeviceDialog;
import com.yjrlab.idoll.libs.SettingUtils;
import com.yjrlab.idoll.libs.iDollService;
import com.yjrlab.idoll.model.DataRecordModel;

import static com.yjrlab.idoll.activity.Fragment2.INTENT_FROM_F2;

/**
 * Created by jongrakmoon on 2017. 6. 1..
 */

public class UserProfileActivity extends BaseActivity implements View.OnClickListener {

    private static final int PERMISSION_LOCATION_REQUEST_CODE = 100;
    private RadioGroup mRadioGroupGender;
    private EditText mEditTextAge, mEditTextHeight, mEditTextWeight;
    private Button mButtonSearch;
    private Button mButtonSave;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setLayout();
    }

    private void setLayout() {
        mRadioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);

        mEditTextAge = (EditText) findViewById(R.id.editTextAge);
        mEditTextHeight = (EditText) findViewById(R.id.editTextHeight);
        mEditTextWeight = (EditText) findViewById(R.id.editTextWeight);

        mButtonSearch = (Button) findViewById(R.id.buttonSearch);
        mButtonSearch.setOnClickListener(this);

        mButtonSave = (Button) findViewById(R.id.buttonSave);
        mButtonSave.setOnClickListener(this);

        if (getIntent().getStringExtra(INTENT_FROM_F2) != null) {
            if (getIntent().getStringExtra(INTENT_FROM_F2).equals(INTENT_FROM_F2)) {
                mButtonSave.setVisibility(View.VISIBLE);
                mButtonSearch.setVisibility(View.GONE);
            }
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSearch:
                if (checkLocationPermission(this)) {
                    try {
                        int gender = (mRadioGroupGender.getCheckedRadioButtonId() == R.id.radioButtonMan ? 0 : 1);
                        int age = Integer.parseInt(mEditTextAge.getText().toString());
                        int weight = Integer.parseInt(mEditTextWeight.getText().toString());
                        int height = Integer.parseInt(mEditTextHeight.getText().toString());

                        UserProfile profile = new UserProfile();
                        profile.setGender(gender);
                        profile.setAge(age);
                        profile.setWeight(weight);
                        profile.setHeight(height);

                        mRadioGroupGender.setEnabled(false);
                        mEditTextAge.setEnabled(false);
                        mEditTextHeight.setEnabled(false);
                        mEditTextWeight.setEnabled(false);


                        Dialog dialog = new SearchDeviceDialog(getContext(), profile);
                        dialog.setCancelable(true);
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                startActivity(new Intent(getContext(), IntroActivity.class));
                                finish();
                            }
                        });

                        dialog.show();

                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "나이, 몸무게, 키는 정수로만 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.buttonSave:
                int gender = (mRadioGroupGender.getCheckedRadioButtonId() == R.id.radioButtonMan ? 0 : 1);
                int age = Integer.parseInt(mEditTextAge.getText().toString());
                int weight = Integer.parseInt(mEditTextWeight.getText().toString());
                int height = Integer.parseInt(mEditTextHeight.getText().toString());

                UserProfile profile = new UserProfile();
                profile.setGender(gender);
                profile.setAge(age);
                profile.setWeight(weight);
                profile.setHeight(height);

                SettingUtils.saveUserProfile(getContext(), profile);
                DataRecordModel.saveNow(this);
                stopService(new Intent(getContext(), iDollService.class));
                startActivity(new Intent(this, IntroActivity.class));
                break;
        }


    }

    public static boolean checkLocationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(activity, "블루투스 벤드를 검색하기위해 위치정보가 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
