package com.example.readandwrite.ui;

import android.Manifest;
import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readandwrite.MainActivity;
import com.example.readandwrite.R;
import com.example.readandwrite.activity.IconActivity;
import com.example.readandwrite.activity.LoginActivity;
import com.example.readandwrite.activity.SettingActivity;
import com.example.readandwrite.adapter.MyAdapter;
import com.example.readandwrite.bean.LitePalUser;
import com.example.readandwrite.bean.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

public class UserHomeFragment extends Fragment {

    private View view,bottomPop;
    private ImageView userIcon;
    private TextView userName;
    private ViewPager2 viewPager2;
    private BottomSheetDialog bottomSheetDialog;
    private Fragment collectFragment = new ColloctionFragment();
    private Fragment myFragment = new MyArticleFragment();
    private List<Fragment> fragmentList = new ArrayList<>();
    private String[] tabTitles;
    private TabLayout tab;
    private String imagePath;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_home, container, false);
        userIcon = (ImageView) view.findViewById(R.id.icon_image);
        userName = (TextView) view.findViewById(R.id.username);
        tab = (TabLayout) view.findViewById(R.id.tab);
        viewPager2 = (ViewPager2) view.findViewById(R.id.pager);
        bottomPop = LayoutInflater.from(getContext()).inflate(R.layout.bottom_popwindow,null);
        initData();
        //创建适配器
        MyAdapter adapter = new MyAdapter(getActivity());
        viewPager2.setAdapter(adapter);
        //TabLayout与viewpage2联动
        new TabLayoutMediator(tab, viewPager2, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabTitles[position]);
            }
        }).attach();
        //显示图片
        /*String imagePath = getActivity().getIntent().getStringExtra("imagePath");*/
        initImagePath();
        if(imagePath!=null){
            displayImage(imagePath);
        }
        setLinsters();
        return view;
    }

    private void initData() {
        tabTitles = new String[]{"我的文章","收藏文章"};
        fragmentList.add(myFragment);
        fragmentList.add(collectFragment);
    }


    private void setLinsters() {
        if(BmobUser.isLogin())
        {
            User user = BmobUser.getCurrentUser(User.class);
            String name = user.getUsername();
            userName.setText(name);
            bottomSheetDialog = new BottomSheetDialog(getContext());
            bottomSheetDialog.setContentView(bottomPop);

            userIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.show();
                }
            });

            Button picture = (Button) bottomPop.findViewById(R.id.picture);
            Button dismiss = (Button) bottomPop.findViewById(R.id.dismiss);

            picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), IconActivity.class);
                    startActivity(intent);
                }
            });


            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*bottomSheetDialog.dismiss();*/
                    BmobUser.logOut();//退出登录，同时清除缓存对象
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    startActivity(intent);
                    bottomSheetDialog.dismiss();
                }
            });

            //viewpage2选中改变监听
            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                }
            });
            //TabLayout选中改变监听
            tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

//            downloadIcon(user);
            //user_icon.setClickable(false);
        }else{
            userName.setText("未登录");
            userIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    /*显示图片*/
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            userIcon.setImageBitmap(bitmap);
        } else {
            Toast.makeText(getActivity(), "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    /*从本地数据库中读取数据*/
    private void initImagePath()
    {
        if(BmobUser.isLogin()){
            User user = BmobUser.getCurrentUser(User.class);
            String name = user.getUsername();
            List<LitePalUser> userList = LitePal.findAll(LitePalUser.class);
            for(LitePalUser palUser : userList){
                if(palUser.getUsername().equals(name)){
                    imagePath = palUser.getImagePath();
                    break;
                }
            }
        }
    }

}