package com.example.readandwrite.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readandwrite.R;
import com.example.readandwrite.activity.EditActivity;
import com.example.readandwrite.bean.BbArticle;
import com.example.readandwrite.bean.LitePalArticle;

import org.litepal.LitePal;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class MyArticleAdapter extends RecyclerView.Adapter<MyArticleAdapter.ViewHolder> {

    private Context mContext;
    private List<LitePalArticle> mArticleList;
    private List<BbArticle> bbArticleList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView textTitle;
        TextView textAuthor;
        TextView textContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=(CardView) itemView;
            textTitle=(TextView) itemView.findViewById(R.id.text_title);
            textAuthor=(TextView) itemView.findViewById(R.id.text_author);
            textContent=(TextView) itemView.findViewById(R.id.text_content);
        }
    }
    public MyArticleAdapter(List<BbArticle> bArticleList){
        bbArticleList = bArticleList;
        /*mArticleList = articleList;*/
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.article_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        /*???????????????????????????*/
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(mContext, EditActivity.class);
                String title = null,content = null;
                int id = 0;
                /*if(mArticleList.size() == bbArticleList.size()) {
                    *//*???LitePal????????????????????????*//*
                    LitePalArticle article = mArticleList.get(position);
                    title = article.getTitle();
                    content = article.getContent();
                    id = article.getId();
                }*/
                /*??????????????????????????????*/
                BbArticle article1 =  bbArticleList.get(position);
                String objectId=article1.getObjectId();
                title = article1.getTitle();
                content = article1.getContent();
                /*????????????*/
                intent.putExtra("objectId",objectId);
                intent.putExtra("id",id);
                intent.putExtra("title",title);
                intent.putExtra("content",content);
                mContext.startActivity(intent);
            }
        });
        /*????????????*/
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                /*??????dialog????????????*/
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("????????????????????????");
                dialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int position = holder.getAdapterPosition();
                       /* if(mArticleList.size() == bbArticleList.size())
                        {
                            *//*?????????????????????????????????*//*
                            LitePalArticle article = mArticleList.get(position);
                            *//*???????????????????????????????????????id??????*//*
                            LitePal.delete(LitePalArticle.class,article.getId());
                        }*/
                        deleteArticle(position);
                        bbArticleList.remove(position);
                        notifyItemChanged(position);
                        notifyItemRangeChanged(position,getItemCount()-position);
                        Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show();
                        /*Intent intent = new Intent(mContext, MyArticleActivity.class);
                        mContext.startActivity(intent);*/

                        dialogInterface.dismiss();
                    }
                });
                dialog.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.show();
                return true;
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /*if(mArticleList.size()==bbArticleList.size())
        {
            LitePalArticle article = mArticleList.get(position);
            holder.textTitle.setText(article.getTitle());
            holder.textAuthor.setText(article.getAuthor());
            holder.textContent.setText(article.getContent());
        }
        else
        {*/
            BbArticle article = bbArticleList.get(position);
            holder.textTitle.setText(article.getTitle());
            holder.textAuthor.setText(article.getAuthor());
            holder.textContent.setText(article.getContent());
       /* }*/
//        holder.textAuthor.setText(article.getTxt_author());
    }

    @Override
    public int getItemCount() {
        return bbArticleList.size();
    }

    /*??????????????????????????????*/
    private void deleteArticle(int position)
    {
        BbArticle bbArticle = new BbArticle();
        String objectId = bbArticleList.get(position).getObjectId();
        /*??????????????????????????????*/
        bbArticle.delete(objectId,new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Log.d("MyArticleAdapter","????????????");
                }
                else{
                    Log.d("MyArticleAdapter","????????????"+e.getMessage());
                }
            }
        });
    }
}
