package com.gizwits.opensource.appkit.DiyClass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * post方法
 */
public class HttpUtils {
    private static String addChengYuan = "http://www.520mylove.cn/chengyuan/addChengYuan.php";
    private static String delectChengYuan = "http://www.520mylove.cn/chengyuan/deleteChengYuan.php";
    private static String updataChengYuan = "http://www.520mylove.cn/chengyuan/updateChengYuan.php";

    private static URL url, addurl, delecturl, updataurl;

    public HttpUtils() {
        // TODO Auto-generated constructor stub
    }

    static {
        try {
            addurl = new URL(addChengYuan);
            delecturl = new URL(delectChengYuan);
            updataurl = new URL(updataChengYuan);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param params  填写的url的参数
     * @param encode  字节编码
     * @param operate 0 添加成员  1 删除成员  2 更新成员
     * @return
     */
    public static String sendPostMessage(Map<String, String> params, String encode, int operate) {
        StringBuffer buffer = new StringBuffer();
        try {//把请求的主体写入正文！！
            if (params != null && !params.isEmpty()) {
                //迭代器
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    buffer.append(entry.getKey()).append("=").
                            append(URLEncoder.encode(entry.getValue(), encode)).
                            append("&");
                }
            }
            //删除最后一个字符&，多了一个;主体设置完毕
            buffer.deleteCharAt(buffer.length() - 1);
            byte[] mydata = buffer.toString().getBytes();
            if (operate == 0) {
                url = addurl;
            } else if (operate == 1) {
                url = delecturl;
            } else if (operate == 2) {
                url = updataurl;
            } else {

            }
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setDoInput(true);//表示从服务器获取数据
            connection.setDoOutput(true);//表示向服务器写数据
            //获得上传信息的字节大小以及长度
            connection.setRequestMethod("POST");
            //是否使用缓存
            connection.setUseCaches(false);
            //表示设置请求体的类型是文本类型
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(mydata.length));

            connection.connect();   //连接，不写也可以。。？？有待了解

            //获得输出流，向服务器输出数据
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(mydata, 0, mydata.length);
            //获得服务器响应的结果和状态码
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return changeInputeStream(connection.getInputStream(), encode);

            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 将一个输入流转换成字符串
     *
     * @param inputStream
     * @param encode
     * @return
     */
    private static String changeInputeStream(InputStream inputStream, String encode) {
        //通常叫做内存流，写在内存中的
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if (inputStream != null) {
            try {
                while ((len = inputStream.read(data)) != -1) {
                    data.toString();

                    outputStream.write(data, 0, len);
                }
                //result是在服务器端设置的doPost函数中的
                result = new String(outputStream.toByteArray(), encode);
                outputStream.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }
}
