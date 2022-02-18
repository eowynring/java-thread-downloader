package core;

import constant.Constant;
import util.HttpUtils;

import java.io.*;
import java.net.HttpURLConnection;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2022/02/17/17:11
 * @Description: 下载器
 */
public class Downloader {
    public void download(String url) {
        // 获取文件名
        String httpFileName = HttpUtils.getHttpFileName(url);
        // 文件下载路径
        httpFileName = Constant.PATH + httpFileName;

        // 获取链接对象
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = HttpUtils.getHttpURLConnection(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                FileOutputStream fos = new FileOutputStream(httpFileName);
                BufferedOutputStream bos = new BufferedOutputStream(fos)
        ) {
            int len = -1;
            while ((len = bis.read()) != -1) {
                bos.write(len);
            }
        } catch (FileNotFoundException e) {
            System.out.println("下载的文件不存在");
        } catch (IOException e) {

        } catch (Exception e) {
            System.out.println("下载失败");
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

    }
}
