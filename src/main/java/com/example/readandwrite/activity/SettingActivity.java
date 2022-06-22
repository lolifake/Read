package com.example.readandwrite.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readandwrite.MainActivity;
import com.example.readandwrite.R;

import cn.bmob.v3.BmobUser;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView nick_name,pwd;
    private Button login_out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        /*设置toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*得到控件*/
        nick_name = (TextView) findViewById(R.id.niki_name);
        pwd = (TextView) findViewById(R.id.pwd);
        login_out = (Button) findViewById(R.id.login_out);
        /*设置导航栏（home）*/
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        /*设置监听事件*/
        nick_name.setOnClickListener(this);
        pwd.setOnClickListener(this);
        login_out.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if(BmobUser.isLogin()){//用户是否登录，登录才可进行设置的更改
            Intent intent;
            switch (view.getId()){
                case R.id.login_out:
                    BmobUser.logOut();//退出登录，同时清除缓存对象
                    intent = new Intent(SettingActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.niki_name://修改昵称
                    Intent intent1 = new Intent(SettingActivity.this,SetItemActivity.class);
                    intent1.putExtra("info",nick_name.getText().toString());
                    startActivity(intent1);
                    finish();
                    break;
                case R.id.pwd://修改密码
                    intent = new Intent(SettingActivity.this,SetItemActivity.class);
                    intent.putExtra("info",pwd.getText().toString());
                    startActivity(intent);
                    finish();
                    break;
                default:
            }
        }else{
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
        }

    }
}