package com.zhidian.cloudintercomdemo;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import com.zhidian.cloudintercomlibrary.CloudIntercomLibrary;

/**
 * Created by blackflagbin on 2017/5/16.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        CloudIntercomLibrary.getInstance().init(getApplicationContext());
        //设置呼叫被取消和呼叫超时的notification图片icon
        CloudIntercomLibrary.getInstance().setIconResId(R.mipmap.icon_logo);
        //设置账号在别处登录的监听
        CloudIntercomLibrary.getInstance().setOnAccountReplacedListener(new CloudIntercomLibrary.OnAccountReplacedListener() {

            @Override
            public void onAccountReplaced() {
                SharedPreferences sp = getSharedPreferences("Demo", MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.clear();
                edit.apply();
                getApplicationContext().startActivity(new Intent(getApplicationContext(),LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }
}
