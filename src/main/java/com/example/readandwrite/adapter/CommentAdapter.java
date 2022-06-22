package com.example.readandwrite.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readandwrite.R;
import com.example.readandwrite.bean.Comment;
import com.example.readandwrite.bean.LitePalUser;
import com.example.readandwrite.bean.User;

import org.litepal.LitePal;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment> comments ;
    private Context mContext;

    public CommentAdapter(List<Comment> commentList) {
        comments = commentList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
       /* ImageView cardView;*/
        LinearLayout layout;
        TextView username;
        TextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView;
            username=(TextView) itemView.findViewById(R.id.username);
            comment=(TextView) itemView.findViewById(R.id.comment);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(BmobUser.isLogin()){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    dialog.setMessage("确定删除此评论");
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int position = holder.getAdapterPosition();
                            User user = BmobUser.getCurrentUser(User.class);
                            String username = user.getUsername();
                            Log.d("Test","Current UserName:"+username);
                            if(comments.get(position).getUser().getObjectId().equals(user.getObjectId()))
                            {
                                deleteComment(position);
                                Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }else{
                                Toast.makeText(mContext, "你没有这个权限", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.show();
                }else{
                    Toast.makeText(mContext, "请先登录", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
        return holder;
    }
    /*从云数据库中删除*/
    private void deleteComment(int position) {
        Comment comment = new Comment();
        String objectId = comments.get(position).getObjectId();
        comment.delete(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Log.d("Test","delete comment success");
                }else{
                    Log.d("Test","delete comment fail:"+e.getMessage());
                }
            }
        });
        comments.remove(position);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        /*String imagePath = findImagePath(comment.getUser().getUsername());*/
        holder.comment.setText(comment.getContent());
        holder.username.setText(comment.getUser().getUsername());
        /*displayImage(holder,imagePath);*/
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


}
