package com.example.readandwrite.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.readandwrite.R;
import com.example.readandwrite.bean.LitePalUser;
import com.example.readandwrite.bean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity {

    private boolean FLAG=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        /*设置toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*设置ActionBar*/
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Button register = (Button) findViewById(R.id.register);
        /*EditText res_email = (EditText) findViewById(R.id.res_email);*/
        EditText res_pwd = (EditText) findViewById(R.id.res_pwd);
        EditText enres_pwd = (EditText) findViewById(R.id.enres_pwd);
        EditText res_name = (EditText) findViewById(R.id.res_name);

        res_pwd.setInputType(129);//设置密码不可见，如果editText.getInputType()的值为128则代表目前是明文显示密码，为129则是隐藏密码
        enres_pwd.setInputType(129);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*  String email = res_email.getText().toString();*/
                /*得到编辑框数据*/
                String pwd = res_pwd.getText().toString();
                String en_pwd = enres_pwd.getText().toString();
                String name = res_name.getText().toString();
                /*判断昵称，密码是否为空*/
                if (name.equals("") || pwd.equals("") || en_pwd.equals("")) {
                    Toast.makeText(RegisterActivity.this, "昵称或密码不能为空", Toast.LENGTH_SHORT).show();
                }
                else if(pwd.equals(en_pwd)){
                    /*向本地数据库中插入数据*/
                    Save(name);
                    /*添加用户信息，进行注册操作*/
                    User bmobUser = new User();
                    bmobUser.setUsername(name);
                    bmobUser.setPassword(pwd);
                    bmobUser.signUp(new SaveListener<BmobUser>() {
                        @Override
                        public void done(BmobUser bmobUser, BmobException e) {
                            if(e == null)
                            {
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                Log.d("Test","Register fail:"+e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void Save(String username)
    {
        LitePalUser litePalUser = new LitePalUser();
        litePalUser.setUsername(username);
        litePalUser.save();
    }

}