package com.example.readandwrite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.readandwrite.activity.EditActivity;
import com.example.readandwrite.activity.SettingActivity;
import com.example.readandwrite.ui.EditFragment;
import com.example.readandwrite.ui.HomeFragment;
import com.example.readandwrite.ui.UserHomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private Fragment homeFragment = new HomeFragment();
    private Fragment userHomeFragment = new UserHomeFragment();
    private Fragment editFragment = new EditFragment();
    private List<Fragment> fragmentList = new ArrayList<>();
    private int lastFragment; //用于记录上个选择的Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));//设置状态栏颜色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏图标和文字颜色为暗色


        //默认初始化
        Bmob.initialize(this,"4411744e07f52d9f72363fdba661efbc");
        /*设置ToolBar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFragment();
        setListeners();
    }

    private void setListeners() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        switchFragment(0);
                        break;
                    case R.id.edit:
                        if(BmobUser.isLogin())
                        {
                            Intent intent = new Intent(MainActivity.this, EditActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.user_home:
                        switchFragment(2);
                        break;
                }
                return true;
            }
        });
    }

    private void initFragment() {
        bottomNavigationView = findViewById(R.id.nav_btn);

        fragmentList.add(homeFragment);
        fragmentList.add(editFragment);
        fragmentList.add(userHomeFragment);
        lastFragment = 0;
        switchFragment(lastFragment);
    }

    private void switchFragment(int lastFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.lin_lay_fragment,fragmentList.get(lastFragment));
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}