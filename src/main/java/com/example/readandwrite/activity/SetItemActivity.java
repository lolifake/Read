package com.example.readandwrite.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.readandwrite.R;
import com.example.readandwrite.bean.BbArticle;
import com.example.readandwrite.bean.LitePalUser;
import com.example.readandwrite.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class SetItemActivity extends AppCompatActivity {

    private String text_info;
    private String name;
    private EditText editText,pwdText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_item);
        /*设置toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*设置导航栏*/
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        TextView info_view = (TextView) findViewById(R.id.text_info);
        TextView newPwd=(TextView) findViewById(R.id.newPwd);
        editText = (EditText) findViewById(R.id.edit_info);
        text_info = getIntent().getStringExtra("info");
        /*根据选择的不同setting对象设置不同的显示*/
        if(text_info.equals("密码")){
            pwdText = (EditText) findViewById(R.id.pwd);
            newPwd.setVisibility(View.VISIBLE);
            pwdText.setVisibility(View.VISIBLE);
            info_view.setText("旧密码:");
            newPwd.setText("新密码:");
            editText.setHint("请输入旧密码");
            pwdText.setHint("请输入新密码");
        }
        else{
            info_view.setText(text_info);
            editText.setHint("请输入昵称");
        }

        if(BmobUser.isLogin()){
            User user = BmobUser.getCurrentUser(User.class);
            name = user.getUsername();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(SetItemActivity.this,SettingActivity.class);
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
            case R.id.submit:
                if(text_info.equals("昵称"))
                {
                    /*修改用户名，修改用户表和文章表中的有关数据*/
                    String username = editText.getText().toString();
                    updateLiteUser(username);
                    updateUser(username);
                    updateArticleList(username);
                    startActivity(intent);
                }
                else if(text_info.equals("密码"))
                {
                    /*修改密码*/
                    String oldPwd = editText.getText().toString();
                    String newPwd = pwdText.getText().toString();
                    updatePwd(oldPwd,newPwd);
                    startActivity(intent);
                }
                finish();
                break;
        }
        return true;
    }
    /*更新密码*/
    private void updatePwd(String oldPwd, String newPwd) {
        BmobUser.updateCurrentUserPassword(oldPwd, newPwd, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null)
                {
                    Log.d("SetItemActivity","密码修改成功");
                }else{
                    Log.d("SetItemActivity","密码修改失败");
                }
            }
        });
    }
    /*更新用户*/
    private void updateUser(String username) {
        User user = BmobUser.getCurrentUser(User.class);
        user.setUsername(username);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null)
                {
                    Log.d("SetItemActivity","User更新成功"+user.getUsername());
                }
                else{
                    Log.d("SetItemActivity","User更新失败"+e.getMessage());
                }
            }
        });
    }
    /*更新文章*/
    private void updateArticleList(String username)
    {
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<BbArticle> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("author",user.getUsername());
        bmobQuery.findObjects(new FindListener<BbArticle>() {
            @Override
            public void done(List<BbArticle> list, BmobException e) {
                if(e == null)
                {
                    for(int i =0 ;i< list.size();i++)
                    {
                        updateArticle(list.get(i),username);
                    }
                    Log.d("SetItem","更新完毕"+list.size());
                }
                else{
                    Log.d("SetItem","出现错误"+e.getMessage());
                }
            }
        });
    }

    /*更新一个文章*/
    private void updateArticle(BbArticle article,String username) {
        BbArticle bbArticle = new BbArticle();
        bbArticle.setObjectId(article.getObjectId());
        bbArticle.setAuthor(username);
        bbArticle.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null)
                {
                    Log.d("SetItem","更新一条文章成功:"+bbArticle.getAuthor());
                }
                else{
                    Log.d("SetItem","更新出现错误:"+e.getMessage());
                }
            }
        });
    }

    /*在本地数据库中更新用户*/
    private void updateLiteUser(String username)
    {
        LitePalUser user = new LitePalUser();
        user.setUsername(username);
        user.updateAll("username = ?",name);
        Log.d("Test","message:"+user.getUsername());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_toolbar,menu);
        return true;
    }
}