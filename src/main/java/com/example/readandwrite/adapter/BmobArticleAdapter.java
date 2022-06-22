package com.example.readandwrite.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readandwrite.R;
import com.example.readandwrite.activity.ArticleActivity;
import com.example.readandwrite.bean.BbArticle;
import java.util.List;

public class BmobArticleAdapter extends RecyclerView.Adapter<BmobArticleAdapter.ViewHolder>{
private Context mContext;
private List<BbArticle> mArticleList;

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

    public BmobArticleAdapter(List<BbArticle> articleList){
        mArticleList = articleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.article_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        /*点击卡片，实现跳转*/
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                BbArticle article = mArticleList.get(position);
                /*跳转并传递数据*/
                Intent intent = new Intent(mContext, ArticleActivity.class);
                intent.putExtra("activity",mContext.toString());
                intent.putExtra("author",article.getAuthor());
                intent.putExtra("id",article.getObjectId());
                intent.putExtra("title",article.getTitle());
                intent.putExtra("content",article.getContent());
                mContext.startActivity(intent);
                
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BbArticle article = mArticleList.get(position);
        holder.textTitle.setText(article.getTitle());
        holder.textAuthor.setText(article.getAuthor());
        holder.textContent.setText(article.getContent());
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

}
