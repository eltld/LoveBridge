package com.lovebridge.recommend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.lovebridge.R;
import com.lovebridge.library.YARFragment;
import com.lovebridge.library.tools.YARActivityUtil;
import com.lovebridge.library.view.pulltorefresh.PullToRefreshBase;
import com.lovebridge.library.view.pulltorefresh.PullToRefreshListView;
import com.lovebridge.utils.JsonResolveUtils;

import java.util.List;


public class RecommendListFragment extends YARFragment {
    private PullToRefreshListView mPullToRefreshListView;
    private NearByPeopleAdapter mRecommendListAdapter;

    @Override
    public int doGetContentViewId() {
        // TODO Auto-generated method stub
        return R.layout.recommendlist;
    }

    @Override
    public void doInitSubViews(View containerView) {
        // TODO Auto-generated method stub
        mPullToRefreshListView = (PullToRefreshListView) containerView.findViewById(R.id.pull_refresh_list);

        mPullToRefreshListView.getRefreshableView().setPadding(10, 10, 10, 10);

        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mRecommendListAdapter = new NearByPeopleAdapter(mActivity);
        mPullToRefreshListView.setAdapter(mRecommendListAdapter);
        mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NearByPeople user = (NearByPeople) mRecommendListAdapter.getItem(position - 1);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                Intent intent = new Intent(mContext, RecommendDetailActivity.class);
                YARActivityUtil.start(mActivity, intent, bundle);
            }
        });
    }

    @Override
    public void doInitDataes() {
        List<NearByPeople> list = JsonResolveUtils.resolveNearbyPeople();
        mRecommendListAdapter.setList(list);
        mRecommendListAdapter.notifyDataSetChanged();
    }

    @Override
    public void doAfter() {
        // TODO Auto-generated method stub
    }
}
