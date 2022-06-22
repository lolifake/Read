package com.example.readandwrite.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.readandwrite.ui.ColloctionFragment;
import com.example.readandwrite.ui.MyArticleFragment;

public class MyAdapter extends FragmentStateAdapter {


    public MyAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //每个页面对应的Fragment
        switch (position){
            case 0:
                return new MyArticleFragment();
            case 1:
                return new ColloctionFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
