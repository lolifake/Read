package com.example.readandwrite.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.readandwrite.MainActivity;
import com.example.readandwrite.R;
import com.example.readandwrite.bean.LitePalUser;
import com.example.readandwrite.bean.User;
import com.example.readandwrite.ui.UserHomeFragment;

import org.litepal.LitePal;

import java.io.File;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class IconActivity extends Activity {

    public static final int CHOOSE_PHOTO = 2;
    public static final int RESIZE_CODE = 0;
    private String imagePath;
    private ImageView picture;
    private Uri uri;
    private User user;
    private String objectId,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon);
        ImageView back = (ImageView) findViewById(R.id.picture_back);
        Button chooseFromAlbum = (Button) findViewById(R.id.choose_from_album);
        Button submit = (Button)findViewById(R.id.submit);
        picture = (ImageView) findViewById(R.id.picture);

        Bmob.resetDomain("http://open-vip.bmob.cn/8/");
        user = BmobUser.getCurrentUser(User.class);
        objectId = user.getObjectId();
        username = user.getUsername();

        String path = findImagePath(username);
        displayImage(path);

        /*返回按钮*/
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*获取权限*/
                if (ContextCompat.checkSelfPermission(IconActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(IconActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IconActivity.this, MainActivity.class);
                intent.putExtra("imagePath",imagePath);
                startActivity(intent);
            }
        });
    }

    private String findImagePath(String author)
    {
        String imagePath = null;
        List<LitePalUser> userList = LitePal.findAll(LitePalUser.class);
        for(LitePalUser palUser : userList){
            if(palUser.getUsername().equals(author)){
                imagePath = palUser.getImagePath();
                break;
            }
        }
        return imagePath;
    }

    /*打开相册*/
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); //打开相册
    }

    /*获取相册权限*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "你拒绝了这个权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        //4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            case RESIZE_CODE:
                if(data != null){
                    resizeImage(uri);
                }
                break;
            default:
                break;
        }
    }


    private void handleImageBeforeKitKat(Intent data) {
        uri = data.getData();
        /*resizeImage(uri);*/
        imagePath = getImagePath(uri, null);
        displayImage(imagePath);
        uploadIcon(imagePath);
        Save(imagePath);
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        uri = data.getData();
        /*resizeImage(uri);*/
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri,通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);

            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的Uri,使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri,直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);//根据图片路径显示图片
        uploadIcon(imagePath);
        Save(imagePath);
    }

    /*重塑图片大小*/
    public void resizeImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//可以裁剪
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESIZE_CODE);
    }


    /*显示图片*/
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        } /*else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }*/
    }

    /*显示重塑后的图片*/
    private void showResizeImage(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(getResources(),photo);
            picture.setImageDrawable(drawable);
        }
    }


    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /*上传图片*/
    private void uploadIcon(String imagePath) {
        User user = BmobUser.getCurrentUser(User.class);
        user.setImagePath(imagePath);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null)
                {
                    Toast.makeText(IconActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("Icon","update fail:"+e.getMessage());
                }
            }
        });
    }

    /*将图片保存再本地，更新数据*/
    private void Save(String imagePath){
        LitePalUser litePalUser = new LitePalUser();
        /*litePalUser.setUsername(username);*/
        litePalUser.setImagePath(imagePath);
        litePalUser.updateAll("username = ?",username);
        Log.d("IconActivit","Insert ImagePath Success");
    }
}