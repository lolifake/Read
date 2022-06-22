package com.example.readandwrite.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readandwrite.R;
import com.example.readandwrite.adapter.BmobArticleAdapter;
import com.example.readandwrite.adapter.MyArticleAdapter;
import com.example.readandwrite.bean.BbArticle;
import com.example.readandwrite.bean.LitePalArticle;
import com.example.readandwrite.bean.User;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyArticleFragment extends Fragment {

    private View view;
    private List<BbArticle> bbarticleList = new ArrayList<>();
    private MyArticleAdapter adapter;
    private String name;
    private User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_article, container, false);

        /*得到当前用户*/
        if(BmobUser.isLogin())
        {
            user = BmobUser.getCurrentUser(User.class);
            name = user.getUsername( );
            /*从云数据库中得到数据*/
            initBombArticleList();
        }
//        initList();

        return view;
    }

    private void initBombArticleList() {
        if(bbarticleList != null){
            bbarticleList.clear();
        }
        BmobQuery<BbArticle> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("author",name);
        bmobQuery.findObjects(new FindListener<BbArticle>() {
            @Override
            public void done(List<BbArticle> list, BmobException e) {
                if(e == null)
                {
                    bbarticleList = list;
                    /*显示RecycleView*/
                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
                    GridLayoutManager layoutManager = new GridLayoutManager(getContext(),1);
                    recyclerView.setLayoutManager(layoutManager);
                    adapter = new MyArticleAdapter(bbarticleList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Log.d("MyArticleActivity","MyArticle 查询成功"+bbarticleList.size());
                    Log.d("MyArticleActivity","MyArticle name"+name);
                }
                else{
                    Log.d("MyArticleActivity","查询失败"+e.getMessage());
                }
            }
        });
    }
}