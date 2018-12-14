package com.example.zmx.facerecognitionattendancemanager;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        replaceFragment(new HistoryFragment());

        //toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.toolbar_menu);
        }

        //drawer_layout抽屉布局
        drawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navView = findViewById(R.id.nav_view);
        //设置抽屉中单项点击事件
//        navView.setCheckedItem(R.id.nav_history);           //设置被选中的项目
        navView.setNavigationItemSelectedListener(new NavigationView.
                OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();        //点击每个按钮后都后关闭抽屉
                //根据id判定点击的按钮
                switch (item.getItemId()) {
                    case R.id.nav_history:
                        replaceFragment(new HistoryFragment());
                        Snackbar.make(drawerLayout, "Data deleted", Snackbar.LENGTH_LONG)
                                .setAction("撤销", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(MainActivity.this, "Data restored",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
                        break;

                    default:
                        break;
                }
                return true;
            }
        });


        //FloatingActionButton悬浮按钮
        FloatingActionButton flaSignature = findViewById(R.id.fab_signature);
        flaSignature.setOnClickListener(this);

    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.addToBackStack(null);       //按返回后返回到上一个Fragment
        transaction.commit();
    }

    //按钮响应
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击扫脸按钮的功能
            case R.id.fab_signature:

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
