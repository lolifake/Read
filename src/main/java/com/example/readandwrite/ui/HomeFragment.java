package com.example.readandwrite.ui;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.readandwrite.R;
import com.example.readandwrite.adapter.BmobArticleAdapter;
import com.example.readandwrite.bean.BbArticle;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class HomeFragment extends Fragment {
    private View view;
    private List<BbArticle> articleList = new ArrayList<>();
    private BmobArticleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home,container,false);

        initArticle();

        return view;
    }

    /*初始化articleList，从Bmob云数据库取数据*/
    private void initArticle() {
        BmobQuery<BbArticle> articleBmobQuery = new BmobQuery<>();
        /*按更新的时间顺序排序查询*/
        articleBmobQuery.order("-updatedAt");//排序
        articleBmobQuery.findObjects(new FindListener<BbArticle>() {
            @Override
            public void done(List<BbArticle> list, BmobException e) {
                if(e == null)
                {
                    articleList = list;
                    /*显示出来*/
                    RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(staggeredGridLayoutManager);
                    adapter = new BmobArticleAdapter(articleList);
                    recyclerView.setAdapter(adapter);
                    Log.d("Test","查询成功");
                }
                else{
                    Log.d("Test","查询数据失败"+e.getMessage());
                }
            }
        });
    }

}