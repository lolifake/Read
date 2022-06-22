package com.example.readandwrite.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readandwrite.MainActivity;
import com.example.readandwrite.R;
import com.example.readandwrite.bean.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {

    private EditText user_name,user_pwd;
    private  int FLAG = 0;//判断用户输入是否正确标志位
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*设置toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*将导航栏显示出来*/
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button login = (Button) findViewById(R.id.login);
        user_name=(EditText) findViewById(R.id.login_name);
        user_pwd=(EditText) findViewById(R.id.login_pwd);
        user_pwd.setInputType(129); //隐藏密码

        TextView register = (TextView) findViewById(R.id.register);
        /*登录按钮*/
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*得到用户名和密码*/
                String name = user_name.getText().toString();
                String pwd = user_pwd.getText().toString();
                Log.d("Test","email:"+name);
                Log.d("Test","pwd:"+pwd);

                if(name.equals(""))//判断用户名是否为空
                {
                    Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                }
                if(pwd.equals(""))//判断密码是否为空
                {
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
                /*从云数据库查找用户数据*/
                User bmobUser = new User();
                bmobUser.setUsername(name);
                bmobUser.setPassword(pwd);
                bmobUser.login(new SaveListener<Object>() {
                    @Override
                    public void done(Object o, BmobException e) {
                        if(e==null){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            Log.d("Test","Login Fail"+e.getMessage());
                        }
                    }
                });


            }
        });

        /*注册按钮*/
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}