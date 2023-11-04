package com.example.jlu.myapplication;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dtflys.forest.config.ForestConfiguration;
import com.example.jlu.myapplication.Method;
import java.util.HashMap;
import java.util.Map;
/*
public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Initialize the Database object
        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve username and password from the EditText fields
                EditText editTextUsername = findViewById(R.id.editTextUsername);
                String username = editTextUsername.getText().toString().trim();
                EditText editTextPassword = findViewById(R.id.editTextPassword);
                String password = editTextPassword.getText().toString().trim();

                System.out.println("账号："+editTextUsername+"密码："+editTextPassword+"\n");

                ForestConfiguration forest = ForestConfiguration.configuration();//配置Forest网络请求框架的相关参数和选项
                MyClient myClient = forest.createInstance(MyClient.class);//通过Forest框架可以将接口转换为可发送HTTP请求的实例，便于进行网络请求
                String response = myClient.register(username, password);
                if(response.equals("1")){
                    Toast.makeText(getApplicationContext(),"注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),Login.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"注册失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}*/

/*
public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextUsername = findViewById(R.id.editTextUsername);
                String username = editTextUsername.getText().toString().trim();
                EditText editTextPassword = findViewById(R.id.editTextPassword);
                String password = editTextPassword.getText().toString().trim();
                if (isValidUsername(username) && isValidPassword(password)) {
                    // 用户名和密码都合法，可以继续处理
                    // 这里执行注册逻辑

                    // 封装用户名和密码到 Map 对象
                    Map<String, String> userData = new HashMap<>();
                    userData.put("username", username);
                    userData.put("password", password);

                    ForestConfiguration forest = ForestConfiguration.configuration();
                    MyClient myClient = forest.createInstance(MyClient.class);
                    String response = myClient.register(userData);
                    if (response.equals("1")) {
                        Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // 用户名或密码不合法，显示错误消息
                    if (!isValidUsername(username)) {
                        editTextUsername.setError("用户名不合法");
                    }
                    if (!isValidPassword(password)) {
                        editTextPassword.setError("密码不合法");
                    }
                }




            }
        });
    }


    private boolean isValidUsername(String username) {
        // 用户名只能以字母开头且以字母和数字构成，长度不超过20
        return username.matches("^[a-zA-Z][a-zA-Z0-9]{0,19}$");
    }

    private boolean isValidPassword(String password) {
        // 密码不能含有汉字，长度不超过20
        return !password.matches(".*[\\u4e00-\\u9fa5].*") && password.length() <= 20;
    }

}
*/


public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Method.isNetworkConnected(RegisterActivity.this)) {
                    Toast.makeText(RegisterActivity.this, "网络未连接或服务器异常！", Toast.LENGTH_SHORT).show();
                } else {

                    EditText editTextUsername = findViewById(R.id.editTextUsername);
                    String username = editTextUsername.getText().toString().trim();
                    EditText editTextPassword = findViewById(R.id.editTextPassword);
                    String password = editTextPassword.getText().toString().trim();

                    if (isValidUsername(username) && isValidPassword(password)) {
                        new RegisterTask().execute(username, password);
                    } else {
                        // 用户名或密码不合法，显示错误消息
                        if (!isValidUsername(username)) {
                            editTextUsername.setError("用户名不合法");
                        }
                        if (!isValidPassword(password)) {
                            editTextPassword.setError("密码不合法");
                        }
                    }
                }
            }
        });
    }

    private boolean isValidUsername(String username) {
        // 用户名只能以字母开头且以字母和数字构成，长度不超过20
        return username.matches("^[a-zA-Z][a-zA-Z0-9]{0,19}$");
    }

    private boolean isValidPassword(String password) {
        // 密码不能含有汉字，长度不超过20
        return !password.matches(".*[\\u4e00-\\u9fa5].*") && password.length() <= 20;
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            // 在这里执行网络请求，并返回注册结果
            ForestConfiguration forest = ForestConfiguration.configuration();
            MyClient myClient = forest.createInstance(MyClient.class);

            Map<String, String> userData = new HashMap<>();
            userData.put("username", username);
            userData.put("password", password);
            return myClient.register(userData);
        }

        @Override
        protected void onPostExecute(String response) {
            if (response.equals("1")) {
                Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "注册失败,用户名已经存在", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

