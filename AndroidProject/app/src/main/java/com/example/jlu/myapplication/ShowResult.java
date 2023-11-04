package com.example.jlu.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mingle.widget.ShapeLoadingDialog;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowResult extends AppCompatActivity {
    private float adjust=0;
    private ListView listView;
    private List<Smoke> SmokeList;
    private ImageView main_photo;
    private void init()
    {

        ImageView scale = (ImageView)findViewById(R.id.scale);//图片展示
        TextView tv_hint = (TextView)findViewById(R.id.hint);
        tv_hint.setOnTouchListener(new View.OnTouchListener() {//触摸显示
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {//按下可见
                    case MotionEvent.ACTION_DOWN:
                        scale.bringToFront();
                        scale.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP://抬起隐藏
                        scale.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });
        //图标设置
        Drawable drawableSearch = getResources().getDrawable(R.drawable.hint);
        drawableSearch.setBounds(0, 0, 25, 25);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        tv_hint.setCompoundDrawables(drawableSearch, null, null, null);//只放上面


    }
private float getlevel(String s){
        String temp="";
        int flag=0;
        for(int i=0;i<s.length();i++){
            if(flag==1){
                temp+=s.charAt(i);
            }
            if(s.charAt(i)==':'){
                flag=1;
            }
        }
        return Float.parseFloat(temp);
}

private  String getstr(String str){
        String temp="";
        for(int i=0;i<str.length();i++){
            if(str.charAt(i)!=':'){
                temp+=str.charAt(i);
            }else{
                temp+=':';
                return temp;
            }
        }
        return "";
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ActionBar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        setContentView(R.layout.activity_show_result);
        init();
        Intent intents=getIntent();
        adjust= intents.getFloatExtra("adjust", 0.0f);
        System.out.println("adjustssss="+adjust);
        listView = findViewById(R.id.listview);
        Intent intent = getIntent();
        //获取传递的检测结果
        String data = intent.getStringExtra("data");
        SmokeList = new ArrayList<>();
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(data).getAsJsonArray();
        for (JsonElement user : jsonArray) {
            System.out.println("bb=:"+user);
            Smoke userBean = gson.fromJson(user, Smoke.class);
            SmokeList.add(userBean);
        }
        main_photo = (ImageView) findViewById(R.id.imageView);
        //展示识别后的图片
        main_photo.setImageURI(Uri.parse("file://" + SmokeList.get(0).url));//显示图片


        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        //为每一个结果创建一个map存储URL路径及其烟雾等级
        //裁切后的烟雾图片
        for (int i = 1; i < SmokeList.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("ItemImage", new File(SmokeList.get(i).url));
            String lev=SmokeList.get(i).level;
            String temp=getstr(lev);
            float le=getlevel(lev);
            le+=adjust;
            long ts=Math.round(le);
            if(ts<0){
                ts=0;
            }
            if(ts>5){
                ts=5;
            }
            map.put("ItemText", temp+ts);
            listItem.add(map);
        }

        //适配器，显示检测结果
        SimpleAdapter adapter = new SimpleAdapter(ShowResult.this,
            listItem,
            R.layout.item,
            new String[] {"ItemImage", "ItemText"},
            new int[] {R.id.imageView, R.id.textView});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent  =  new Intent(ShowResult.this,ShowDetailInfor.class);
                Gson gson = new Gson();
                intent.putExtra("data",gson.toJson(SmokeList.get(i+1)));
                startActivity(intent);
            }
        });

        Button creat_btn = (Button) findViewById(R.id.creatReport);
        creat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowResult.this,SharePage.class);
                intent.putExtra("path",SmokeList.get(0).url);
                double num = SmokeList.size() - 1;
                int over_num = 0;
                double sum = 0;
                for (int i = 1; i < SmokeList.size(); i++) {
                    String dt[] = SmokeList.get(i).level.split(":");
                    String val = dt[dt.length - 1];
                    float temp=Float.valueOf(val);
                    temp+=adjust;
                    int level=Math.round(temp);
                    if(level<0)level=0;
                    if(level>5)level=5;
                    sum +=level;
                    if(level >= 3) over_num ++;
                }
                intent.putExtra("num",String.valueOf((new Double(num)).intValue()));
                intent.putExtra("over_num",String.valueOf(over_num));
                intent.putExtra("avg",String.valueOf(sum/num));
                startActivity(intent);
            }
        });
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