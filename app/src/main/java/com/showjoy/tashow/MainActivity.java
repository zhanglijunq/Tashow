package com.showjoy.tashow;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.showjoy.tashow.base.TaShowGlobal;
import com.showjoy.tashow.home.HomeFragment;
import com.showjoy.tashow.localLive.StartLiveActivity;
import com.showjoy.tashow.localLive.WatchLiveActivity;
import com.showjoy.tashow.person.PersonFragment;
import com.showjoy.tashow.person.SettingEvent;
import com.showjoy.tashow.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //UI Object
    private RelativeLayout tab_home,tab_live,tab_person;

    //Fragment Object
    private HomeFragment fg1;
    private PersonFragment pf;
    private FragmentManager fManager;
    private android.support.v4.app.FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_home);
        fManager = getFragmentManager();
        fm = getSupportFragmentManager();
        bindViews();
        tab_home.performClick();   //模拟一次点击，既进去后选择第一项
    }

    //UI组件初始化与事件绑定
    private void bindViews() {
        tab_home = (RelativeLayout) findViewById(R.id.tab_home);
        tab_live = (RelativeLayout) findViewById(R.id.tab_live);
        tab_person = (RelativeLayout) findViewById(R.id.tab_person);

        tab_home.setOnClickListener(this);
        tab_live.setOnClickListener(this);
        tab_person.setOnClickListener(this);
    }

    //重置所有文本的选中状态
    private void setSelected(){
        tab_home.setSelected(false);
        tab_live.setSelected(false);
        tab_person.setSelected(false);
    }

    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(fg1 != null)fragmentTransaction.hide(fg1);
        if(pf != null)fragmentTransaction.hide(pf);
    }


    @Override
    public void onClick(View v) {
        FragmentTransaction fTransaction = fManager.beginTransaction();
        switch (v.getId()){
            case R.id.tab_home:
//                hideAllFragment(fTransaction);
                setSelected();
                tab_home.setSelected(true);
//                if(fg1 == null){
//                    fg1 = new HomeFragment(fm);
//                    fTransaction.add(R.id.ly_content,fg1);
//                }else{
//                    fTransaction.show(fg1);
//                }
                fg1 = new HomeFragment(fm);
                fTransaction.replace(R.id.ly_content,fg1);
                break;
            case R.id.tab_live:
                UIUtils.gotoActivity(TaShowGlobal.appContext, StartLiveActivity.class);
                break;
            case R.id.tab_person:
                hideAllFragment(fTransaction);
                setSelected();
                tab_person.setSelected(true);
//                if(pf == null){
//                    pf = new PersonFragment();
//                    fTransaction.add(R.id.ly_content,pf);
//                }else{
//                    fTransaction.show(pf);
//                }
                pf = new PersonFragment();
                fTransaction.replace(R.id.ly_content,pf);
                break;
        }
        fTransaction.commit();
    }

    @Subscribe
    public void onEventMainThread(SettingEvent event){
        if (event.isChange()){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
