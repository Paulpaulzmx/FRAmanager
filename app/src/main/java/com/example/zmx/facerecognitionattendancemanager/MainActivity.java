package com.example.zmx.facerecognitionattendancemanager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private DrawerLayout drawerLayout;

    private static final String CURRENT_FRAGMENT = "STATE_FRAGMENT_SHOW";

    private FragmentManager fragmentManager;

    private Fragment currentFragment = new Fragment();
    private List<Fragment> fragments = new ArrayList<>();

    private FloatingActionButton floatingActionButton;

    private int currentIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();      //初始化fragmentManager

        //toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.toolbar_menu);
        }

        //drawer_layout抽屉布局
        drawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navView = findViewById(R.id.nav_view);

//        navView.setCheckedItem(R.id.nav_history);           //设置被选中的项目
        //设置抽屉中单项点击事件
        navView.setNavigationItemSelectedListener(new NavigationView.
                OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();        //点击每个按钮后都后关闭抽屉
                //根据id判定点击的按钮
                switch (item.getItemId()) {
                    case R.id.nav_history:
                        floatingActionButton.setImageResource(R.mipmap.fab_signature);
                        currentIndex = 0;
                        break;

                    case R.id.nav_stu_list:
                        floatingActionButton.setImageResource(R.mipmap.fab_register);
                        currentIndex = 1;
                        break;

                    case R.id.nav_time_set:
                        currentIndex = 2;
                        break;

                    default:
                        break;
                }

                showFragment();

                return true;
            }
        });


        //FloatingActionButton悬浮按钮
        floatingActionButton = findViewById(R.id.fab_signature);
        floatingActionButton.setOnClickListener(this);


        if (savedInstanceState != null) {       //内存重启时调用

            //获取“内存重启”时保存的索引下标
            currentIndex = savedInstanceState.getInt(CURRENT_FRAGMENT, 0);

            //注意，添加顺序要跟下面添加的顺序一样！！！！
            fragments.removeAll(fragments);
            fragments.add(fragmentManager.findFragmentByTag(0 + ""));
            fragments.add(fragmentManager.findFragmentByTag(1 + ""));
            fragments.add(fragmentManager.findFragmentByTag(2 + ""));

            //恢复fragment页面
            restoreFragment();

        } else {      //正常启动时调用

            fragments.add(new HistoryFragment());
            fragments.add(new StuListFragment());
            fragments.add(new TimeSettingFragment());

            showFragment();
        }
    }

//以下是fragment切换涉及到的逻辑

    /**
     * 使用show() hide()切换页面
     * 显示fragment
     */
    private void showFragment() {

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (!fragments.get(currentIndex).isAdded()) {      //之前没有被添加过
            transaction
                    .hide(currentFragment)
                    .add(R.id.fragment, fragments.get(currentIndex), "" + currentIndex);  //第三个参数为添加当前的fragment时绑定一个tag
        } else {
            transaction
                    .hide(currentFragment)
                    .show(fragments.get(currentIndex));
        }

        currentFragment = fragments.get(currentIndex);

        transaction.commit();
    }


    /**
     * 恢复fragment
     */
    private void restoreFragment() {

        FragmentTransaction mBeginTransaction = fragmentManager.beginTransaction();


        for (int i = 0; i < fragments.size(); i++) {

            if (i == currentIndex) {
                mBeginTransaction.show(fragments.get(i));
            } else {
                mBeginTransaction.hide(fragments.get(i));
            }

        }

        mBeginTransaction.commit();

        //把当前显示的fragment记录下来
        currentFragment = fragments.get(currentIndex);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //“内存重启”时保存当前的fragment名字
        outState.putInt(CURRENT_FRAGMENT, currentIndex);
        super.onSaveInstanceState(outState);
    }


    //按钮响应
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击扫脸按钮的功能
            case R.id.fab_signature:
                switch (currentIndex) {
                    case 0:
                        Toast.makeText(MainActivity.this, "拍照签到", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Intent takePictureIntent = new Intent(
                                MainActivity.this, TakePhotoActivity.class);
                        startActivity(takePictureIntent);
                    default:
                        break;
                }

                break;
            default:
                break;
        }
    }


    //设置toolbar样式，为menu中的toolbar.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    //给toolbar中按钮添加功能
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                //设置的代码
                break;
            case android.R.id.home:     //注意深坑：这里的R前面还有个android
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    //重写返回键的方法，按返回键后检查抽屉栏是否打开，若打开，关闭它；若未打开，正常的返回即可。
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
