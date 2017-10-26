package com.zhidian.cloudintercomdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.zhidian.cloudintercomlibrary.CloudIntercomLibrary;

public class SettingActivity extends AppCompatActivity {

    private TextView mTvNotice;
    private Switch   mSwitchNotice;
    private TextView mTvSilence;
    private Switch   mSwitchSilence;
    private TextView mTvVibrate;
    private Switch   mSwitchVibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        //对呼叫进行相关设置
        mSwitchNotice.setChecked(CloudIntercomLibrary.getInstance().isNoNotice());
        mSwitchSilence.setChecked(CloudIntercomLibrary.getInstance().isSilenceMode());
        mSwitchVibrate.setChecked(CloudIntercomLibrary.getInstance().isVibrate());
        mSwitchNotice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CloudIntercomLibrary.getInstance().setNoNotice(isChecked);
            }
        });
        mSwitchSilence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CloudIntercomLibrary.getInstance().setSilenceMode(isChecked);
            }
        });
        mSwitchVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CloudIntercomLibrary.getInstance().setVibrate(isChecked);
            }
        });

    }

    private void initView() {
        mTvNotice = (TextView) findViewById(R.id.tv_notice);
        mSwitchNotice = (Switch) findViewById(R.id.switch_notice);
        mTvSilence = (TextView) findViewById(R.id.tv_silence);
        mSwitchSilence = (Switch) findViewById(R.id.switch_silence);
        mTvVibrate = (TextView) findViewById(R.id.tv_vibrate);
        mSwitchVibrate = (Switch) findViewById(R.id.switch_vibrate);
    }
}
