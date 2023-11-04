package com.example.jlu.myapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
public class MainActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private TextView countdownTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            // 获取当前网络连接类型
            int networkType = activeNetworkInfo.getType();
            try {
                if (networkType == ConnectivityManager.TYPE_WIFI) {
                    // 如果是Wi-Fi连接
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

                    int ipAddress = dhcpInfo.ipAddress;

                    // 将IP地址转换为IPv4格式
                    String ipv4Address = String.format(
                            "%d.%d.%d.%d",
                            (ipAddress & 0xff),
                            (ipAddress >> 8 & 0xff),
                            (ipAddress >> 16 & 0xff),
                            (ipAddress >> 24 & 0xff)
                    );

                    Log.d("IPAddress", "Wi-Fi IPv4 Address: " + ipv4Address);
                } else if (networkType == ConnectivityManager.TYPE_MOBILE) {
                    // 如果是Mobile网络连接
                    Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                    while (networkInterfaces.hasMoreElements()) {
                        NetworkInterface networkInterface = networkInterfaces.nextElement();
                        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                        while (inetAddresses.hasMoreElements()) {
                            InetAddress inetAddress = inetAddresses.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                // 找到IPv4地址
                                String ipv4Address = inetAddress.getHostAddress();
                                Log.d("IPAddress", "Mobile IPv4 Address: " + ipv4Address);
                            }
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        countdownTextView = findViewById(R.id.countdownTextView);

        // Start the countdown
        startCountdown();
    }

    private void startCountdown() {
        new CountDownTimer(SPLASH_DISPLAY_LENGTH, 1000) {

            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                countdownTextView.setText("倒计时: " + secondsRemaining + " 秒");
            }
            public void onFinish() {
                // When the countdown is finished, start the ChoosePhoto activity
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
