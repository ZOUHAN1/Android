package com.example.jlu.myapplication;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Database {
    private static final String JDBC_URL = "jdbc:mysql://10.67.77.126:3306/android";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "root";
    private Context context;
    // JDBC connection
    private Connection connection;

    public Database(Context context) {
        this.context = context;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            if(connection==null){
                System.out.println("数据库驱动异常");
            }else{
                System.out.println("数据库驱动成功");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("数据库驱动加载失败");
            e.printStackTrace();
            showToastAndExit("Database connection failed. Exiting in 3 seconds.");
        }
    }

    private void showToastAndExit(String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Exit the application
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                }, 3000);  // 3 seconds delay
            }
        });
    }

    public boolean checkUserCredentials(String username, String password) {
        try {
            String sql = "SELECT * FROM client WHERE user = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertUser(String username, String password) {
        try {
            String sql = "INSERT INTO client (user, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add other database operations as needed (update, delete, etc.)

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
