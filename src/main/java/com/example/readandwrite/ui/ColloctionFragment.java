package com.example.readandwrite.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readandwrite.R;
import com.example.readandwrite.adapter.BmobArticleAdapter;
import com.example.readandwrite.bean.BbArticle;
import com.example.readandwrite.bean.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ColloctionFragment extends Fragment {

    private BmobArticleAdapter adapter;
    private List<BbArticle> articleList = new ArrayList<>();
    private View view;
    private User user;
    private String objectId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_colloction, container, false);
        /*得到当前用户*/
        if(BmobUser.isLogin())
        {
            user = BmobUser.getCurrentUser(User.class);
            objectId = user.getObjectId();
        }
        /*初始化收藏文章数据*/
        initArticle();
        return view;
    }

    private void initArticle() {
        /*被收藏的文章数，因此查询的是文章表*/
        BmobQuery<BbArticle> articleBmobQuery = new BmobQuery<>();
        User bmobUser = new User();
        bmobUser.setObjectId(objectId);
        /*collectArticle是User表字段，用来存储该用户收藏的所有文章*/
        articleBmobQuery.addWhereRelatedTo("collectArticle",new BmobPointer(bmobUser));
        articleBmobQuery.findObjects(new FindListener<BbArticle>() {
            @Override
            public void done(List<BbArticle> list, BmobException e) {
                if(e == null)
                {
                    articleList = list;
                    /*将recycleView显示出来*/
                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    adapter = new BmobArticleAdapter(articleList);
                    recyclerView.setAdapter(adapter);
                    Log.d("Test","收藏个数:"+list.size());
                }
                else{
                    Log.d("Tset","查询失败:"+e.getMessage());
                }
            }
        });
    }
}