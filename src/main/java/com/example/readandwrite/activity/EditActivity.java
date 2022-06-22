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
import android.widget.EditText;
import android.widget.Toast;

import com.example.readandwrite.MainActivity;
import com.example.readandwrite.R;
import com.example.readandwrite.bean.BbArticle;
import com.example.readandwrite.bean.LitePalArticle;
import com.example.readandwrite.bean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class EditActivity extends AppCompatActivity {

    private EditText edit_title,edit_content;
    private int id;
    private String objectId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        /*设置toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*设置导航按钮*/
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);//设置home按钮图片
        }
        edit_title = (EditText) findViewById(R.id.edit_title);
        edit_content = (EditText) findViewById(R.id.edit_content);
        intitData();
    }
    /*如果是进行编辑操作，将title和content显示出来*/
    private void intitData() {
        Intent intent = getIntent();
        if(intent != null)
        {
            id = intent.getIntExtra("id",0);
            objectId = intent.getStringExtra("objectId");
            if(objectId!=null)
            {
                edit_title.setText(intent.getStringExtra("title"));
                edit_content.setText(intent.getStringExtra("content"));
            }
        }
    }

    /*显示menu按钮*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }
    /*设置按钮功能*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.send:
                /*得到当前用户的用户名，即文章的作者*/
                User bmobUser = BmobUser.getCurrentUser(User.class);
                String author = bmobUser.getUsername();
                /*从编辑框中得到文章标题和文章内容*/
                String title = edit_title.getText().toString();
                String content = edit_content.getText().toString();
                if(objectId!=null)/*对文章进行修改*/
                {
                    if(title.equals("")){
                        Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        /*将修改后的文章存储在本地*/
                        LitePalArticle article = new LitePalArticle();
                        article.setTitle(title);
                        article.setContent(content);
                        article.updateAll("id = ?",String.valueOf(id));
                        /*将修改后的文章上传到云数据库*/
                        update(title,content,author);
                        /*实现页面跳转*/
                        Intent intent = new Intent(EditActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(this, "文章修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else/*新建一个文章*/
                {
                    if(title.equals("")){/*设置标题不能为空*/
                        Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        /*数据插入本地LitePal库中的Article表中*/
                        LitePalArticle article = new LitePalArticle();
                        article.setContent(content);
                        article.setTitle(title);
                        article.setAuthor(author);
                        article.save();
                        /*将数据插入Bmob云数据库中*/
                        save(title,content,author);
                        /*页面跳转*/
                        Intent intent = new Intent(EditActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(this, "文章发布成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
            default:
        }
        return true;
    }
    /*将新发表的文章插入云数据库中*/
    private void save(String title,String content,String author)
    {
        BbArticle bbArticle = new BbArticle();
        bbArticle.setAuthor(author);
        bbArticle.setContent(content);
        bbArticle.setTitle(title);
        bbArticle.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e == null)
                {
                    Log.d("Test","S:"+s);
                }else{
                    Log.d("Test","EditWrong:"+e.toString());
                }
            }
        });
    }
    /*更新云数据库中的数据*/
    private void update(String title,String content,String author)
    {
        BbArticle article = new BbArticle();
        article.setObjectId(objectId);
        article.setAuthor(author);
        article.setTitle(title);
        article.setContent(content);
        /*更新操作*/
        article.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null)
                {
                    Log.d("Test","更新成功");
                }
                else{
                    Log.d("Test","更新失败"+e.getMessage());
                }
            }
        });
    }
}