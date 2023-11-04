package com.example.jlu.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChoosePhoto extends AppCompatActivity {

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static  final  int REQUEST_CODE_GET_ADJUST=3;
    private ImageView photo;
    private Uri imageUri;
    private Button goNext;
    public float adjust=0;//等级调整幅度

//choosephoto界面的加载


    private void init()
    {
        //帮助按钮
        Button tv_about =findViewById(R.id.about);
        tv_about.setOnClickListener(new View.OnClickListener() {
            @Override
            //about按钮的监听器，切换到aboutactivity
            public void onClick(View v) {
                ComponentName componentname = new ComponentName(ChoosePhoto.this, AboutActivity.class);//切换到aboutactivity
                Intent intent = new Intent();
                intent.setComponent(componentname);
                startActivity(intent);
            }
        });

        //天气按钮
        Button setWeather = findViewById(R.id.weather);

        setWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoosePhoto.this, GetAdjust.class);
                System.out.println("天气按钮被点击");
                startActivityForResult(intent,REQUEST_CODE_GET_ADJUST);
                System.out.println("1234");
            }
        });

        //相机、相册、下一步按钮的图标的加载
        Button camera,album,next,weather,back,help;
        help=findViewById(R.id.about);
        Drawable ds=getResources().getDrawable(R.drawable.help);
        ds.setBounds(0,0,90,90);
        help.setCompoundDrawables(null,ds,null,null);
        back=findViewById(R.id.back);
        //Drawable drawables=getResources().getDrawable(R.drawable.back);
        //drawables.setBounds(0,0,90,90);
        //back.setCompoundDrawables(null,drawables,null,null);

        weather=findViewById(R.id.weather);
        Drawable draws=getResources().getDrawable(R.drawable.weather);
        draws.setBounds(0,0,90,90);
        weather.setCompoundDrawables(null,draws,null,null);
        camera = findViewById(R.id.take_photo);
        Drawable drawableFirst = getResources().getDrawable(R.drawable.photography);
        drawableFirst.setBounds(0, 0, 90, 90);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        camera.setCompoundDrawables(null, drawableFirst, null, null);//只放上面
        album = findViewById(R.id.choose_photo);
        Drawable drawableSearch = getResources().getDrawable(R.drawable.album);
        drawableSearch.setBounds(0, 0, 90, 90);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        album.setCompoundDrawables(null, drawableSearch, null, null);//只放上面
        next = findViewById(R.id.go_next);
        //Drawable drawableNext = getResources().getDrawable(R.drawable.next);
        //drawableNext.setBounds(0, 0, 90, 90);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        //next.setCompoundDrawables(null, null, drawableNext, null);//只放上面
    }
    @Override



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo);












        init();
        photo = (ImageView) findViewById(R.id.photo);//imageview的加载
        goNext = (Button) findViewById(R.id.go_next);//下一步加载
        goNext.setVisibility(View.GONE);//暂时不可见
        Button back=findViewById(R.id.back);
        back.setVisibility(View.GONE);

        Button goNext = findViewById(R.id.go_next);
        ImageView photo = findViewById(R.id.photo);
        Button weather = findViewById(R.id.weather);
//返回按钮监听
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏 go_next 按钮
                goNext.setVisibility(View.GONE);

                // 显示特定图片
                photo.setImageResource(R.drawable.shote); // 替换为你想显示的特定图片资源

                // 显示 weather 按钮
                weather.setVisibility(View.VISIBLE);

                // 隐藏 Back 按钮自身
                back.setVisibility(View.GONE);
            }
        });



//下一步监听
        goNext.setOnClickListener(new View.OnClickListener() {//设置下一步按钮的监听
            @Override
            public void onClick(View view) {

                Bitmap bitmap = Method.setimage(photo);//获取imageview上的图片
                String path = Method.saveImageToCache(bitmap,ChoosePhoto.this);//保存图片到缓存并保存路径
                Intent intent = new Intent(ChoosePhoto.this,PhotoDisplay.class);
                intent.putExtra("image",path);//图片路径放入附加载数据
                intent.putExtra("adjust",adjust);
                //跳转到photodisplay界面
                startActivity(intent);
            }
        });

     //拍照按钮监听

        Button takePhoto = (Button) findViewById(R.id.take_photo);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //拍摄图片保存
                File outputImage = new File(getExternalCacheDir(),"output_image.jpg");
                try{//如果存在删除并创建一个新的
                    if(outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                imageUri = FileProvider.getUriForFile(ChoosePhoto.this,
                        "com.example.cameraalbumtest.fileprovider",outputImage);
                //获取图片文件的url

                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                //拍照

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                // 指定图片的输出地址为imageuri


                startActivityForResult(intent,TAKE_PHOTO);//跳转到相机应用进行拍照
            }
        });


        //照片选择Album按钮监听器设置

        Button choosePhoto = (Button) findViewById(R.id.choose_photo);
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ChoosePhoto.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChoosePhoto.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    // openAlbum
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent,CHOOSE_PHOTO);
                }
            }
        });
    }

//活动返回时被调用
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        photo.setImageBitmap(bitmap);
                        goNext.setVisibility(View.VISIBLE);
                        Button wea=findViewById(R.id.weather);
                        wea.setVisibility(View.GONE);
                        Button back=findViewById(R.id.back);
                        back.setVisibility(View.VISIBLE);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Button back=findViewById(R.id.back);
                    back.setVisibility(View.VISIBLE);
                    Uri uri = data.getData();
                    photo.setImageURI(uri);
                    goNext.setVisibility(View.VISIBLE);
                    Button wea=findViewById(R.id.weather);
                    wea.setVisibility(View.GONE);
                    Log.wtf("tts", uri.getPath());
                }
                break;
            case REQUEST_CODE_GET_ADJUST:
                // 处理来自 GetAdjust 的返回结果
                if (resultCode == Activity.RESULT_OK) {
                    // Receive the smokeLevel from the result
                    adjust = data.getFloatExtra("smokeLevel", 0.0f);
                    System.out.println("adjust="+adjust);
                    // Do something with the received data
                    // ...
                }
                break;


            default:
                break;
        }
    }
//重写home键
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //返回按钮点击事件
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}