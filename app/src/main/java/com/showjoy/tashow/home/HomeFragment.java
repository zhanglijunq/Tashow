package com.showjoy.tashow.home;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.showjoy.tashow.R;
import com.showjoy.tashow.common.Config;
import com.showjoy.tashow.home.attentionLive.AttentionLiveFragment;
import com.showjoy.tashow.home.hotlive.HotLiveFragment;
import com.showjoy.tashow.home.newestlive.NewestLiveFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jay on 2015/8/28 0028.
 */
@SuppressLint("ValidFragment")
public class HomeFragment extends Fragment {

    private ViewPager viewPager;

    private TabLayout tabLayout;

    private TextView programTxt, expertTxt;

    private FragmentManager fm;

    public HomeFragment() {
    }

    public HomeFragment(FragmentManager fm) {
        this.fm = fm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        programTxt = (TextView) view.findViewById(R.id.txt_program);
        expertTxt = (TextView) view.findViewById(R.id.txt_expert);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewPager();
        initEvent();
    }

    private void initEvent() {
        programTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) programTxt.getTag();
                if (tag != null && Config.TAB_SELECT.equals(tag)) {
                    programTxt.setBackgroundResource(R.drawable.shape_left_half_tab_unchecked);
                    programTxt.setTextColor(getResources().getColor(R.color.tab_unchecked));
                    expertTxt.setBackgroundResource(R.drawable.shape_right_half_tab_checked);
                    expertTxt.setTextColor(getResources().getColor(R.color.white));
                } else {
                    programTxt.setBackgroundResource(R.drawable.shape_left_half_tab_checked);
                    programTxt.setTextColor(getResources().getColor(R.color.white));
                    expertTxt.setBackgroundResource(R.drawable.shape_right_half_tab_unchecked);
                    expertTxt.setTextColor(getResources().getColor(R.color.tab_unchecked));
                }
            }
        });
        expertTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) programTxt.getTag();
                if (tag != null && Config.TAB_SELECT.equals(tag)) {
                    expertTxt.setBackgroundResource(R.drawable.shape_right_half_tab_unchecked);
                    expertTxt.setTextColor(getResources().getColor(R.color.tab_unchecked));
                    programTxt.setBackgroundResource(R.drawable.shape_left_half_tab_checked);
                    programTxt.setTextColor(getResources().getColor(R.color.white));
                } else {
                    expertTxt.setBackgroundResource(R.drawable.shape_right_half_tab_checked);
                    expertTxt.setTextColor(getResources().getColor(R.color.white));
                    programTxt.setBackgroundResource(R.drawable.shape_left_half_tab_unchecked);
                    programTxt.setTextColor(getResources().getColor(R.color.tab_unchecked));
                }
            }
        });
    }

    private void initViewPager() {
        List<String> titles = new ArrayList<>();
        titles.add("热门");
        titles.add("最新");
        titles.add("关注");

        for(int i=0;i<titles.size();i++){
            tabLayout.addTab(tabLayout.newTab().setText(titles.get(i)));
        }
        List<android.support.v4.app.Fragment> fragments = new ArrayList<>();
        fragments.add(new HotLiveFragment());
        fragments.add(new NewestLiveFragment());
        fragments.add(new AttentionLiveFragment(tabLayout));

        HomeFragmentAdapter mFragmentAdapteradapter = new HomeFragmentAdapter(fm, fragments, titles);
        //给ViewPager设置适配器
        viewPager.setAdapter(mFragmentAdapteradapter);
        //将TabLayout和ViewPager关联起来。
        tabLayout.setupWithViewPager(viewPager);
    }
}
