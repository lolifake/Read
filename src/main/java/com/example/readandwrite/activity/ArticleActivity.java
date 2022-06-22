package com.example.readandwrite.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readandwrite.MainActivity;
import com.example.readandwrite.R;
import com.example.readandwrite.adapter.BmobArticleAdapter;
import com.example.readandwrite.adapter.CommentAdapter;
import com.example.readandwrite.bean.BbArticle;
import com.example.readandwrite.bean.Comment;
import com.example.readandwrite.bean.LitePalUser;
import com.example.readandwrite.bean.User;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ArticleActivity extends AppCompatActivity {

    private List<User> userList = new ArrayList<>(); //收藏该帖子的用户
    private List<Comment> commentList = new ArrayList<>();
    private boolean flag = false;
    private CommentAdapter adapter;
    private FloatingActionButton fab,comment;
    private ImageView userIcon;
    private String UserObjectId;//当前用户的ID
    private String activity;
    private String author;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        //得到数据
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String objectId= intent.getStringExtra("id");//当前文章的ID
        activity = intent.getStringExtra("activity");
        author = intent.getStringExtra("author");
        //设置toolbar和 CollapsingToolbarLayout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        userIcon = (ImageView) findViewById(R.id.icon_image);
        TextView text_content = (TextView) findViewById(R.id.text_content);
        TextView text_author = (TextView) findViewById(R.id.author);
        setSupportActionBar(toolbar);
        /*设置文章标题名*/
        collapsingToolbarLayout.setTitle(title);
        /*得到用户头像*/
        String imagePath = findImagePath(author);
        displayImage(imagePath);
        text_author.setText(author);
        text_content.setText(content);
        /*得到当前用户*/
        if(BmobUser.isLogin())
        {
            User currentUser = BmobUser.getCurrentUser(User.class);
            UserObjectId = currentUser.getObjectId();
        }
        /*得到当前文章评论数并显示出来*/
        initComment(objectId);
        /*设置导航栏*/
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        /*编辑收藏按钮*/
        fab = (FloatingActionButton) findViewById(R.id.collect);
        findCollectArticle(objectId);
        /*编辑评论按钮*/
        comment = (FloatingActionButton) findViewById(R.id.comment);
        View bottomComment = LayoutInflater.from(ArticleActivity.this).inflate(R.layout.bottom_comment,null);
        BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(ArticleActivity.this);
        bottomSheetDialog.setContentView(bottomComment);
        /*评论点击事件*/
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(User.isLogin()){
                    bottomSheetDialog.show();
                    Button send_comment = (Button) bottomComment.findViewById(R.id.send_comment);
                    EditText user_comment = (EditText) bottomComment.findViewById(R.id.user_comment);
                    send_comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String content = user_comment.getText().toString();
                            if(content.equals("")){
                                Toast.makeText(ArticleActivity.this, "评论不能为空", Toast.LENGTH_SHORT).show();
                            }else{
                                saveComment(objectId,content);
                                Toast.makeText(ArticleActivity.this, "发布评论成功", Toast.LENGTH_SHORT).show();
                                user_comment.setText(" ");
                                bottomSheetDialog.dismiss();
                            }
                        }
                    });
                }else{
                    Toast.makeText(ArticleActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Log.d("ArticleActivity","userLiset:"+userList.size());
    }

    /*从Bmob中查询评论数据*/
    private void initComment(String objectId) {
        if(commentList != null){
            commentList.clear();
        }
        BmobQuery<Comment> bmobQuery = new BmobQuery<>();
        BbArticle article = new BbArticle();
        article.setObjectId(objectId);
        bmobQuery.addWhereEqualTo("article",new BmobPointer(article));
        bmobQuery.include("user");
        bmobQuery.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if(e == null)
                {
                    commentList = list;
                    /*显示出来*/
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.comment_view);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(ArticleActivity.this,1);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    adapter = new CommentAdapter(commentList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.addItemDecoration(new DividerItemDecoration(ArticleActivity.this,DividerItemDecoration.VERTICAL));
                    Log.d("Article","查询成功"+commentList.size());
                }
                else{
                    Log.d("Article","error message:"+e.getMessage());
                }
            }
        });
    }

    /*添加评论*/
    private void saveComment(String articleObjectId,String content)
    {
        User user = BmobUser.getCurrentUser(User.class);
        BbArticle article = new BbArticle();
        article.setObjectId(articleObjectId);
        final Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setArticle(article);
        comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e == null){
                    initComment(articleObjectId);
                    Toast.makeText(ArticleActivity.this, "发布评论成功", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("Article","comment fail:"+e.getMessage());
                }
            }
        });
    }

    /*查找是否已收藏该文章*/
    private void findCollectArticle(String objectId)
    {
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        BbArticle article = new BbArticle();
        article.setObjectId(objectId);
        bmobQuery.addWhereRelatedTo("collects",new BmobPointer(article));
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e == null)
                {
                    userList = list;
                    User user = new User();
                    user.setObjectId(UserObjectId);
//                    Log.d("ArticleActivity", "flag:"+flag);
                    for(int i = 0;i < userList.size();i++)
                    {
//                        Log.d("ArticleActivity", "username"+userList.get(i).getUsername());
                        if(userList.get(i).getObjectId().equals(user.getObjectId()))
                        {
                            flag = true;
                            Log.d("ArticleActivity", "flag:"+flag);
                        }
                    }
                    if(flag)
                    {
                        fab.setImageResource(R.drawable.scucess_collect);
                        /*取消收藏*/
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteArticle(objectId);
                                deleteUser(objectId);
                                fab.setImageResource(R.drawable.nav_collection);
                                flag = false;
                                Toast.makeText(ArticleActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{/*收藏功能*/
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(BmobUser.isLogin()){
                                    updateArticle(objectId);
                                    updateUser(objectId);
                                    fab.setImageResource(R.drawable.scucess_collect);
                                    flag = true;
                                }
                                else{
                                    Toast.makeText(ArticleActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    Log.d("ArticleActivity","查询收藏用户成功"+userList.size());
                }else{
                    Log.d("ArticleActivity","查询收藏用户失败"+e.getMessage());
                }
            }
        });
    }
    /*更新数据，即删除之前多对多关联，即取消对该帖子的收藏操作*/
    /*对BbArticle表进行操作*/
    private void deleteArticle(String objectId)
    {
        BbArticle article = new BbArticle();
        article.setObjectId(objectId);
        User user = BmobUser.getCurrentUser(User.class);
        BmobRelation relation = new BmobRelation();
        relation.remove(user);
        article.setCollects(relation);
        article.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Log.d("ArticleActivity","BbArticle关联关系删除成功");
                }
                else{
                    Log.d("ArticleActivity","BbArticle关联关系删除失败"+e.getMessage());
                }
            }
        });
    }

    /*对User表进行操作*/
    private void deleteUser(String objectId)
    {
        User user = new User();
        user.setObjectId(UserObjectId);
        BbArticle article = new BbArticle();
        article.setObjectId(objectId);

        BmobRelation relation = new BmobRelation();
        relation.remove(article);
        user.setCollectArticle(relation);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null)
                {
                    Log.d("ArticleActivity","User关联关系删除成功");
                }
                else{
                    Log.d("ArticleActivity","User关联关系删除失败"+e.getMessage());
                }
            }
        });
    }

    /*更新User表中的数剧*/
    private void updateUser(String objectId)
    {
        User user = BmobUser.getCurrentUser(User.class);
        BbArticle article = new BbArticle();
        article.setObjectId(objectId);
        /*将当前被收藏的帖子插入用户的collectArticle字段中,表明用户收藏了该帖子*/
        BmobRelation relation = new BmobRelation();
        /*将当前文章添加到多对多关联中*/
        relation.add(article);
        user.setCollectArticle(relation);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Log.d("Test","CollectArticle 插入成功");
                }
                else{
                    Log.d("Test","CollectArticle 插入失败："+e.getMessage());
                }
            }
        });
    }

    /*更新BbArticle表中的数据*/
    private void updateArticle(String objectId){
        /*更新Article表中的有关数据*/
        User user = BmobUser.getCurrentUser(User.class);
        BbArticle article = new BbArticle();
        article.setObjectId(objectId);
        /*将当前用户添加到BbArticle表中的collect字段值中，表明该帖子被当前用户收藏*/
        BmobRelation relation = new BmobRelation();
        /*将当前用户添加到多对多关联中*/
        relation.add(user);
        article.setCollects(relation);
        article.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Toast.makeText(ArticleActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("Tset","Collect Fail:"+e.getMessage());
                }
            }
        });
    }

    /*显示头像*/
    private void displayImage(String imagePath)
    {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            userIcon.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    /*找到头像路径*/
    private String findImagePath(String author)
    {
        boolean flag = false;
        final String[] imagePath = {null};
        List<LitePalUser> userList = LitePal.findAll(LitePalUser.class);
        for(LitePalUser palUser : userList){
            if(palUser.getUsername().equals(author)){
                imagePath[0] = palUser.getImagePath();
                flag = true;
                break;
            }
        }
        if(!flag){
            User user = BmobUser.getCurrentUser(User.class);
            BmobQuery<User> userBmobQuery = new BmobQuery<>();
            userBmobQuery.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if(e == null)
                    {
                        for(User user1:list){
                            if(user1.getObjectId().equals(user.getObjectId()))
                            {
                                imagePath[0] = user1.getImagePath();
                                break;
                            }
                        }
                    }
                    else{
                        Log.d("Article","find imagePath from Bmob fail:"+e.getMessage());
                    }

                }
            });
        }
        return imagePath[0];
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(ArticleActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
}