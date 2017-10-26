package com.zhidian.cloudintercomdemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhidian.cloudintercomlibrary.CloudIntercomLibrary;
import com.zhidian.cloudintercomlibrary.entity.AllAddressBean;
import com.zhidian.cloudintercomlibrary.entity.EntranceBean;
import com.zhidian.cloudintercomlibrary.entity.EventBusBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mRvEntrace;
    private List<AllAddressBean> mAllAddressBeanList;
    private List<EntranceBean> mEntranceBeanList;
    private int mDefaultCommunityPosition;
    private String mLastCommunityId = "";
    private String mLastPartitionId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        EventBus.getDefault().register(this);
        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //获取权限
    private void requestPermission() {

        RxPermissions rxPermissions = new RxPermissions(MainActivity.this);
        rxPermissions.request(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean granted) throws Exception {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.community:
                startActivity(new Intent(this, CommunityActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        final SharedPreferences sp = getSharedPreferences("Demo", MODE_PRIVATE);
        mRvEntrace = (RecyclerView) findViewById(R.id.rv_entrance);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        //获取所有小区和分区
        CloudIntercomLibrary.getInstance().getAllAddressList(this, new CloudIntercomLibrary.getDataCallBack<List<AllAddressBean>>() {

            @Override
            public void onSuccess(final List<AllAddressBean> list) {
                if (list == null || list.size() == 0) {
                    return;
                }
                List<AllAddressBean.PartitionListEntity> partitionList = list.get(0).partitionList;
                if (partitionList == null || partitionList.size() == 0) {
                    return;
                }

                SharedPreferences sp = getSharedPreferences("Demo", MODE_PRIVATE);

                String current_community_id = sp.getString("current_community_id", "");
                String current_partition_id = sp.getString("current_partition_id", "");
                if (TextUtils.isEmpty(current_community_id) || TextUtils.isEmpty(current_partition_id)) {
                    SharedPreferences.Editor edit = sp.edit();
                    String communityId = list.get(0).communityId;
                    edit.putString("current_community_id", communityId);
                    String communityName = list.get(0).communityName;
                    edit.putString("current_community_name", communityName);
                    String partitionId = list.get(0).partitionList.get(0).partitionId;
                    edit.putString("current_partition_id", partitionId);
                    String partitionName = list.get(0).partitionList.get(0).partitionName;
                    edit.putString("current_partition_name", partitionName);
                    edit.apply();

                    current_community_id = communityId;
                    current_partition_id = partitionId;
                }
                //获取分区下的所有门口机列表
                CloudIntercomLibrary.getInstance()
                        .getEntranceList(current_partition_id, MainActivity.this, new CloudIntercomLibrary.getDataCallBack<List<EntranceBean>>() {

                            @Override
                            public void onSuccess(
                                    List<EntranceBean> result) {
                                mEntranceBeanList = result;
                                mRvEntrace.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
                                mRvEntrace.setAdapter(new EntranceAdapter());
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                progressDialog.dismiss();
                            }
                        });
            }

            @Override
            public void onError(Throwable e) {
                progressDialog.dismiss();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(EventBusBean bean) {
        if (bean.getName().equals("refresh")) {
            initView();
        }
    }

    public void logout(View view) {
        //登出
        CloudIntercomLibrary.getInstance().logout(this, new CloudIntercomLibrary.LogoutCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {
                SharedPreferences sp = getSharedPreferences("Demo", MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.clear();
                edit.apply();
                getApplicationContext().startActivity(
                        new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    class EntranceAdapter extends RecyclerView.Adapter<EntranceAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = View.inflate(parent.getContext(), R.layout.item_entrance, null);
            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sp = getSharedPreferences("Demo", MODE_PRIVATE);
                    CloudIntercomLibrary.getInstance()
                            .openLock(mEntranceBeanList.get(position).id,
                                    sp.getString("current_community_name", "") + sp.getString("current_partition_name", ""),
                                    mEntranceBeanList.get(position).name, MainActivity.this, null);

                }
            });
            EntranceBean entranceBean = mEntranceBeanList.get(position);
            //显示门口机名称和开门密码
            holder.tvItem.setText(entranceBean.name + "  开门密码为 : " + CloudIntercomLibrary.getInstance().getOpenEntrancePwd(entranceBean.sn));
        }

        @Override
        public int getItemCount() {
            return mEntranceBeanList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tvItem;

            public ViewHolder(View itemView) {
                super(itemView);
                tvItem = (TextView) itemView.findViewById(R.id.tv_item);
            }
        }
    }
}
