package com.zhidian.cloudintercomdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhidian.cloudintercomlibrary.CloudIntercomLibrary;
import com.zhidian.cloudintercomlibrary.entity.UserBean;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button   mBtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSharedPreferences("Demo", MODE_PRIVATE).getBoolean("isLogin", false)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        initView();
    }


    private void initView() {
        mEtUsername = (EditText) findViewById(R.id.et_username);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtLogin = (Button) findViewById(R.id.bt_login);

        mBtLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                submit();
                break;
        }
    }

    private void submit() {
        String username = mEtUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = mEtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        //登录
        CloudIntercomLibrary.getInstance()
                .login(mEtUsername.getText().toString(), mEtPassword.getText().toString(), this,
                        new CloudIntercomLibrary.LoginCallBack<UserBean>() {

                            @Override
                            public void onSuccess(UserBean result) {

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                SharedPreferences sp = getSharedPreferences("Demo", MODE_PRIVATE);
                                SharedPreferences.Editor edit = sp.edit();
                                edit.putBoolean("isLogin", true);
                                edit.apply();
                                finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                                //其实这里不需要作多余的处理，sdk默认会弹出错误提示的Toast
                            }
                        });


    }
}
