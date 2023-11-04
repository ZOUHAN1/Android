package com.example.jlu.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.example.jlu.myapplication.MyClient;
import com.dtflys.forest.config.ForestConfiguration;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mingle.widget.ShapeLoadingDialog;
import com.yalantis.ucrop.UCrop;
import com.example.jlu.myapplication.Smoke;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import android.graphics.Rect;
import android.widget.Toast;

import org.json.JSONArray;

import id.zelory.compressor.Compressor;

public class PhotoDisplay extends AppCompatActivity {
    private ImageView photo;
    private Uri photoUri;
    private String newUri = "";
    private String op_sd = "";
    private ShapeLoadingDialog shapeLoadingDialog;
    private int offset = 0;
    public float adjust=0;
   public static class Msg {
        public int code;
        public String message;
        public Data data;
        public static class Data{
            public String main_img;
            public Smoke[] sub_img;
        }
    }
    private List<Smoke> SmokeList;
    private void init()//初始化按钮图标
    {
        Button crop,confirm;
        crop = findViewById(R.id.go_edit);//编辑按钮
        Drawable drawableCamera = getResources().getDrawable(R.drawable.crop);//按钮图标
        drawableCamera.setBounds(0, 0, 90, 90);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        crop.setCompoundDrawables(null, drawableCamera, null, null);//只放上面
        confirm = findViewById(R.id.go_confirm);//确认按钮
        Drawable drawableAlbum = getResources().getDrawable(R.drawable.confirmation);
        drawableAlbum.setBounds(0, 0, 90, 90);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        confirm.setCompoundDrawables(null, drawableAlbum, null, null);//只放上面
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {//bundle防止状态改变信息丢失
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();//行动栏设置
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);//home按钮可以点击
            actionBar.setDisplayHomeAsUpEnabled(true);//向上导航可点击
        }
        setContentView(R.layout.activity_photo_display);//显示activity_photo_display界面
        init();//界面初始化
        photo = (ImageView) findViewById(R.id.photo);//照片展示
        shapeLoadingDialog = new ShapeLoadingDialog(PhotoDisplay.this);//加载对话框
        shapeLoadingDialog.setLoadingText("Calcing...");//加载文本
        shapeLoadingDialog.setCanceledOnTouchOutside(false);//对话框不可关闭

        Bitmap bitmap = null;
        Intent intent = getIntent();
        final String path = intent.getStringExtra("image");//获取前一个activity图片路径
        adjust=intent.getFloatExtra("adjust",0.0f);//获取调整幅度
        if (path.equals("null"))
            photo.setImageResource(R.drawable.temp);
        else {
            try {
                File file = new File(path);
                if (file.exists()) {
                    bitmap = BitmapFactory.decodeFile(path);
                    photo.setImageBitmap(bitmap);
                    photoUri = Uri.parse("file://" + path);//路径解析为url
                }
            } catch (Exception e) {
            }
        }
        Button button = (Button) findViewById(R.id.go_confirm);//加载确认按钮

        button.setOnClickListener(view -> {//判断网络是否连接
            if(!Method.isNetworkConnected(PhotoDisplay.this))
            {
                Toast.makeText(PhotoDisplay.this, "网络无连接！", Toast.LENGTH_SHORT).show();
                return ;
            }//未连接弹出对话框并返回

            final String opd = photoUri.toString().substring(7);//去掉协议头部后的图片路径
            System.out.println("opd="+opd+'\n');
            shapeLoadingDialog.show();//显示加载动画
            ForestConfiguration forest = ForestConfiguration.configuration();//配置Forest网络请求框架的相关参数和选项
            MyClient myClient = forest.createInstance(MyClient.class);//通过Forest框架可以将接口转换为可发送HTTP请求的实例，便于进行网络请求
            offset = 0;
            compress(opd);//图片压缩
            new Thread(
                () -> {
                try{
                    String result = myClient.upload(opd, progress -> {//显示文件上传过程
                        System.out.println("总字节数: " + progress.getTotalBytes());   // 文件大小
                        System.out.println("当前已上传字节数: " + progress.getCurrentBytes());   // 已上传字节数
                        System.out.println("完成度: " + Math.round(progress.getRate() * 100) + "%");  // 已上传百分比
                        if (progress.isDone()) {   // 是否上传完成
                            System.out.println("--------   文件上传成功!   --------");
                        }
                    });
                    System.out.println("result="+result);//查看服务器返回结果
                    SmokeList = new ArrayList<>();//烟雾
                    //Gson gson = new Gson();
                    Msg msg = new Gson().fromJson(result, Msg.class);
                    Msg.Data data = msg.data;//副本拷贝
                    //mainimg路径和subimg路径及其烟雾等级

                    File file;//存储下载的图片文件
                    Smoke tmp;
                    op_sd = Method.getTimeStr();//当前时间
                    System.out.println("外部目录："+getExternalFilesDir(null).getPath());
                    file = myClient.downloadFile(//下载yolo3识别后的图片，即带有标记的图片
                            getExternalFilesDir(null).getPath(),
                            op_sd + String.valueOf(offset) + ".jpg",
                            progress -> {
                                System.out.println("total bytes: " + progress.getTotalBytes());   // 文件大小
                                System.out.println("current bytes: " + progress.getCurrentBytes());   // 已下载字节数
                                System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");  // 已下载百分比
                                if (progress.isDone()) {   // 是否下载完成
                                    System.out.println("--------   Main Img Download Completed!   --------");
                                }
                            }, data.main_img);//下载路径为main_img

                    System.out.println("main_img_path:"+data.main_img);

                    tmp = new Smoke();//临时烟雾存储
                    tmp.url = getExternalFilesDir(null).getPath() + "/" + op_sd + String.valueOf(offset) + ".jpg";
                    //System.out.println(getExternalFilesDir(null).getPath());
                    System.out.println("op_sd="+op_sd);//
                    tmp.level = "-1";//识别后图片


                    SmokeList.add(tmp);
                    offset++;

                    for (int i = 0, len = data.sub_img.length; i < len; i++) {//下载syb_img
                        String downloadPath = data.sub_img[i].url;
                        System.out.println("downLoadPath="+downloadPath);
                        Log.v("tts", downloadPath);
                        op_sd = Method.getTimeStr();
                        file = myClient.downloadFile(
                                getExternalFilesDir(null).getPath(),
                                op_sd + String.valueOf(offset) + ".jpg",
                                progress -> {
                                    System.out.println("total bytes: " + progress.getTotalBytes());   // 文件大小
                                    System.out.println("current bytes: " + progress.getCurrentBytes());   // 已下载字节数
                                    System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");  // 已下载百分比
                                    if (progress.isDone()) {   // 是否下载完成
                                        System.out.println("--------   Sub Img Download Completed!   --------");
                                    }
                                },
                                downloadPath);

                        tmp = new Smoke();
                        tmp.url = getExternalFilesDir(null).getPath() + "/" + op_sd + String.valueOf(offset) + ".jpg";
                        tmp.level = data.sub_img[i].level;
                        SmokeList.add(tmp);
                        offset++;
                    }



                    runOnUiThread(() -> {
                        Uri downloadUri = Uri.parse("file://" + SmokeList.get(0).url);
                        System.out.println("main_img_path:"+downloadUri);
                        shapeLoadingDialog.dismiss();
                        final Intent intent2  =  new Intent(PhotoDisplay.this,ShowResult.class);//转展示结果界面
                        intent2.putExtra("adjust",adjust);
                        final Gson gson2 = new Gson();
                        intent2.putExtra("data",gson2.toJson(SmokeList));//把服务器返回的结果传递给Showresult
                        startActivity(intent2);
                        shapeLoadingDialog.setLoadingText("Calcing... ");
                    });



                }catch(Exception  e){
                    shapeLoadingDialog.dismiss();
                    Looper.prepare();
                    Toast.makeText(PhotoDisplay.this, "网络无连接！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return ;
                }
                }).start();
        });


        Button button2 = (Button) findViewById(R.id.go_edit);
        button2.setOnClickListener((view) -> {
            String sd = Method.getTimeStr();
            newUri = "file://" + getExternalFilesDir(null).getPath() + "/" + sd + ".jpg";

            UCrop uCrop = UCrop.of( photoUri,  Uri.parse(newUri));
            UCrop.Options options = new UCrop.Options();

            //设置toolbar颜色
            options.setToolbarColor(ActivityCompat.getColor(PhotoDisplay.this, R.color.colorPrimary));
            //设置状态栏颜色
            options.setStatusBarColor(ActivityCompat.getColor(PhotoDisplay.this, R.color.colorPrimary));
            options.setFreeStyleCropEnabled(true);

            uCrop.withOptions(options);
            uCrop.start(PhotoDisplay.this);
        });
    }

    protected void compress(String path) {
        try {
            File new_file = new Compressor(PhotoDisplay.this).compressToFile(new File(path));
            FileOutputStream fos = new FileOutputStream(new File(path));
            Bitmap bitmap = BitmapFactory.decodeFile(new_file.getPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            photoUri = resultUri;
            photo.setImageURI(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }
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