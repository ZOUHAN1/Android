
/*
package com.example.jlu.myapplication;
import android.app.Activity;

import com.dtflys.forest.config.ForestConfiguration;
import com.example.jlu.myapplication.Database;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement register functionality
                register();
            }
        });

        Button buttonExit = findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement exit functionality
                finish();
            }
        });
    }

    private void login() {
        boolean isValidCredentials=false;
        EditText edituser = findViewById(R.id.editTextUsername);
        String username = edituser.getText().toString().trim();
        EditText editpassword = findViewById(R.id.editTextPassword);
        String password = editpassword.getText().toString().trim();
        ForestConfiguration forest = ForestConfiguration.configuration();//配置Forest网络请求框架的相关参数和选项
        MyClient myClient = forest.createInstance(MyClient.class);//通过Forest框架可以将接口转换为可发送HTTP请求的实例，便于进行网络请求

// 发送登录请求
        String response ="1";// myClient.login(username, password);

// 处理服务器响应

        if (response.equals("1")) {
            isValidCredentials = true;
            // 账号密码正确，执行登录成功后的操作
        } else {
            isValidCredentials = false;
            // 账号密码不正确，执行登录失败后的操作
        }


       System.out.println("infoma"+username+" "+password);
        if (isValidCredentials) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();

            // Redirect to MainActivity after successful login
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Optional: Close the LoginActivity so it's not in the back stack
        } else {
            Toast.makeText(this, "登录失败，请检查用户名或者密码", Toast.LENGTH_SHORT).show();
        }
    }

    private void register() {
        Intent intent=new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }
}

*/


package com.example.jlu.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dtflys.forest.config.ForestConfiguration;
import com.example.jlu.myapplication.MyClient;

import java.util.HashMap;
import java.util.Map;

public class Login extends Activity {
    private EditText editUsername;
    private EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("hello");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editUsername = findViewById(R.id.editTextUsername);
        editPassword = findViewById(R.id.editTextPassword);

        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(v -> login());

        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(v -> register());

        Button buttonExit = findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(v -> finish());
    }

    private void login() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        if(!Method.isNetworkConnected(Login.this))
        {
            Toast.makeText(Login.this, "网络未连接或服务器状态异常！", Toast.LENGTH_SHORT).show();
            return ;
        }else {
            new LoginTask().execute(username, password);
        }
    }

    private void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            System.out.println("username"+username+"password"+password);
            System.out.println("error");
            ForestConfiguration forest = ForestConfiguration.configuration();
            MyClient myClient = forest.createInstance(MyClient.class);

             /*   String re ="7";
                        re=myClient.login(username, password);*/
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);

            String response = myClient.login(requestBody);


            System.out.println("re"+response);
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response.equals("1")) {
                // 账号密码正确，执行登录成功后的操作
                Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, ChoosePhoto.class);
                startActivity(intent);
                finish(); // Optional: Close the LoginActivity so it's not in the back stack
            } else {
                // 账号密码不正确，执行登录失败后的操作
                Toast.makeText(Login.this, "登录失败，请检查用户名或者密码", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
