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

        /*????????????*/
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*????????????*/
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

    /*????????????*/
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); //????????????
    }

    /*??????????????????*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
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
                    //???????????????????????????
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4?????????????????????????????????????????????
                        handleImageOnKitKat(data);
                    } else {
                        //4.4??????????????????????????????????????????
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
            //?????????document?????????Uri,??????document id ??????
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//????????????????????????id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);

            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //?????????content?????????Uri,????????????????????????
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //?????????file?????????Uri,??????????????????????????????
            imagePath = uri.getPath();
        }
        displayImage(imagePath);//??????????????????????????????
        uploadIcon(imagePath);
        Save(imagePath);
    }

    /*??????????????????*/
    public void resizeImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//????????????
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESIZE_CODE);
    }


    /*????????????*/
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        } /*else {
            Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
        }*/
    }

    /*????????????????????????*/
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
        //??????Uri???selection??????????????????????????????
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /*????????????*/
    private void uploadIcon(String imagePath) {
        User user = BmobUser.getCurrentUser(User.class);
        user.setImagePath(imagePath);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null)
                {
                    Toast.makeText(IconActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("Icon","update fail:"+e.getMessage());
                }
            }
        });
    }

    /*???????????????????????????????????????*/
    private void Save(String imagePath){
        LitePalUser litePalUser = new LitePalUser();
        /*litePalUser.setUsername(username);*/
        litePalUser.setImagePath(imagePath);
        litePalUser.updateAll("username = ?",username);
        Log.d("IconActivit","Insert ImagePath Success");
    }
}