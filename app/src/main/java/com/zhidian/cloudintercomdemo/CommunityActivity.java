package com.zhidian.cloudintercomdemo;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhidian.cloudintercomlibrary.CloudIntercomLibrary;
import com.zhidian.cloudintercomlibrary.entity.AllAddressBean;
import com.zhidian.cloudintercomlibrary.entity.EventBusBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class CommunityActivity extends AppCompatActivity {

    private RecyclerView                             mRvCommunity;
    private RecyclerView                             mRvPartition;
    private List<AllAddressBean>                     mAllAddressBeanList;
    private ArrayList<Integer>                       mCommunityChooseList;
    private PartitionAdapter                         mPartitionAdapter;
    private ArrayList<Integer>                       mPartitionChooseList;
    private List<AllAddressBean.PartitionListEntity> mPartitionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        initView();
    }

    private void initView() {
        mRvCommunity = (RecyclerView) findViewById(R.id.rv_community_list);
        mRvPartition = (RecyclerView) findViewById(R.id.rv_partition_list);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        //获取所有小区和分区
        CloudIntercomLibrary.getInstance().getAllAddressList(this, new CloudIntercomLibrary.getDataCallBack<List<AllAddressBean>>() {

            @Override
            public void onSuccess(final List<AllAddressBean> list) {
                if (list == null || list.size() == 0) {
                    return;
                }
                mAllAddressBeanList = list;
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
                    edit.putString("current_community_id", partitionName);
                    edit.apply();

                }


                mCommunityChooseList = new ArrayList<>();
                int communityPosition = 0;


                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).communityId.equals(sp.getString("current_community_id", ""))) {
                        communityPosition = i;
                    }
                }
                mCommunityChooseList.add(communityPosition);
                CommunityAdapter communityAdapter = new CommunityAdapter();

                mPartitionChooseList = new ArrayList<>();


                mPartitionList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).communityId.equals(sp.getString("current_community_id", ""))) {
                        mPartitionList.addAll(list.get(i).partitionList);
                    }
                }
                mPartitionAdapter = new PartitionAdapter();

                mRvCommunity.setLayoutManager(new LinearLayoutManager(CommunityActivity.this, LinearLayoutManager.VERTICAL, false));
                mRvCommunity.setAdapter(communityAdapter);

                mRvPartition.setLayoutManager(new LinearLayoutManager(CommunityActivity.this, LinearLayoutManager.VERTICAL, false));

                mRvPartition.setAdapter(mPartitionAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                progressDialog.dismiss();
            }
        });
    }

    class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_community, parent, false);
            return new ViewHolder(rootView);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView       tvItem;
            private ImageView      ivLeft;
            private RelativeLayout rlBg;

            public ViewHolder(View itemView) {
                super(itemView);
                tvItem = (TextView) itemView.findViewById(R.id.tv_left);
                ivLeft = (ImageView) itemView.findViewById(R.id.iv_left);
                rlBg = (RelativeLayout) itemView.findViewById(R.id.rl_bg);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<AllAddressBean.PartitionListEntity> partitionList = mAllAddressBeanList.get(position).partitionList;
                    mCommunityChooseList.clear();
                    mCommunityChooseList.add(position);
                    CommunityAdapter.this.notifyDataSetChanged();
                    mPartitionList = partitionList;
                    mPartitionAdapter.notifyDataSetChanged();
                }
            });
            if (mCommunityChooseList.get(0) == position) {
                holder.ivLeft.setVisibility(View.VISIBLE);
                holder.rlBg.setBackgroundColor(getResources().getColor(R.color.white));
            } else {
                holder.ivLeft.setVisibility(View.INVISIBLE);
                holder.rlBg.setBackgroundColor(getResources().getColor(R.color.blue_light));
            }
            holder.tvItem.setText(mAllAddressBeanList.get(position).communityName);
        }

        @Override
        public int getItemCount() {
            return mAllAddressBeanList.size();
        }


    }

    class PartitionAdapter extends RecyclerView.Adapter<PartitionAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView  tvItem;
            private ImageView ivCheck;

            public ViewHolder(View itemView) {
                super(itemView);
                tvItem = (TextView) itemView.findViewById(R.id.tv_left);
                ivCheck = (ImageView) itemView.findViewById(R.id.iv_check);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_check, parent, false);
            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final SharedPreferences sp = getSharedPreferences("Demo", MODE_PRIVATE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences.Editor edit = sp.edit();

                    AllAddressBean.PartitionListEntity entity = mPartitionList.get(position);
                    mPartitionChooseList.clear();
                    mPartitionChooseList.add(position);
                    AllAddressBean allAddressEntity = mAllAddressBeanList.get(mCommunityChooseList.get(0));
                    edit.putString("current_community_id", allAddressEntity.communityId);
                    edit.putString("current_community_name", allAddressEntity.communityName);
                    edit.putString("current_partition_id", entity.partitionId);
                    edit.putString("current_partition_name", entity.partitionName);
                    edit.apply();
                    PartitionAdapter.this.notifyDataSetChanged();
                    EventBus.getDefault().post(new EventBusBean(1, "refresh", position));
                    finish();
                }
            });
            if (mPartitionList.get(position).partitionId.equals(sp.getString("current_partition_id", ""))) {
                holder.ivCheck.setVisibility(View.VISIBLE);
            } else {
                holder.ivCheck.setVisibility(View.GONE);
            }
            holder.tvItem.setText(mPartitionList.get(position).partitionName);
        }

        @Override
        public int getItemCount() {
            return mPartitionList.size();
        }
    }
}
