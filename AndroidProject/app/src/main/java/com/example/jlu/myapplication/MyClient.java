package com.example.jlu.myapplication;
import com.dtflys.forest.annotation.DataFile;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.extensions.DownloadFile;
import java.io.File;
import java.util.Map;
public interface MyClient {
    String ip="http://192.168.144.201:3000";
    @Request(//校园网登录ip
            //10.67.77.126
            url = ip,
            headers = "Accept: text/plain"
    )
    String sendRequest(@Query("myName") String username);

    @Post(url = ip+"/uploadss",timeout = 200000)
    String upload(@DataFile("image") String filePath, OnProgress onProgress);

    @Post(url = ip+"/downloadResult",timeout = 200000)
    @DownloadFile(dir = "${0}", filename = "${1}")
    File downloadFile(String dir, String filename, OnProgress onProgress, @JSONBody("downloadPath") String path);
    //存储路径，文件名，下载过程，下载路径

    @Post(url = ip+"/register", timeout = 200000)
    String register(@JSONBody Map<String, String> userData);

/*
    @Post(url = "http://10.67.44.91:3000/login", timeout = 200000) // 更改为您的登录端点
    String login(@Query("username") String username, @Query("password") String password);*/
@Post(url = ip+"/login", timeout = 200000)
String login(@JSONBody Map<String, String> requestBody);


}
