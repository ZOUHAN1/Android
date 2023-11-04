package com.example.jlu.myapplication;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
public class GetAdjust extends AppCompatActivity {
    private SeekBar seekBarSmokeLevel;
    private Button submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         System.out.println("sss");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weatherset);
        Button apply=findViewById(R.id.applyButton);
        Drawable draws=getResources().getDrawable(R.drawable.apply);
        draws.setBounds(0,0,90,90);
        apply.setCompoundDrawables(null,draws,null,null);
        System.out.println("跳转成功1");
        System.out.println("跳转成功2");

        System.out.println("跳转成功3");

        System.out.println("Before findViewById");
        seekBarSmokeLevel = findViewById(R.id.seekbar);
        System.out.println("After findViewById");
        submitButton = findViewById(R.id.applyButton);
        System.out.println("After applyButton findViewById");

        if (seekBarSmokeLevel == null) {
            System.out.println("SeekBar is null");
        } else {
            System.out.println("SeekBar is not null");
        }
        if (submitButton == null) {
            System.out.println("Button is null");
        } else {
            System.out.println("Button is not null");
        }
        System.out.println("over");
        //滑动条的监听
        seekBarSmokeLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float smokeLevel = mapProgressToSmokeLevel(progress);
                TextView smokeLevelTextView=findViewById(R.id.smokeLevelTextView);
                smokeLevelTextView.setText("等级调整: " + String.valueOf(smokeLevel));
                // Do something with the smokeLevel if needed
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }
        });
        //提交按钮的监听
        submitButton.setOnClickListener(v -> {
            int progress = seekBarSmokeLevel.getProgress();
            System.out.println("progress==="+progress);
            float smokeLevel = mapProgressToSmokeLevel(progress);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("smokeLevel", smokeLevel);
            // 设置返回结果，并关闭当前界面
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
            // Do something with the smokeLevel obtained when the button is clicked
        });


    }

    private float mapProgressToSmokeLevel(int progress) {
        // Map SeekBar's progress to smoke level in the range of -1.5 to 1.5
        // SeekBar's range is 0 to 30, so we map it to -1.5 to 1.5
        return ((float) progress - 15) / 10;
    }
}
