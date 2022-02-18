package core;

import constant.Constant;
import util.FileUtils;
import util.HttpUtils;
import util.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2022/02/17/17:11
 * @Description: 下载器
 */
public class Downloader {

    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);


    public void download(String url) {
        // 获取文件名
        String httpFileName = HttpUtils.getHttpFileName(url);
        // 文件下载路径
        httpFileName = Constant.PATH + httpFileName;


        // 获取本地文件的大小
        long localFileLength = FileUtils.getFileContentLength(httpFileName);


        // 获取链接对象
        HttpURLConnection httpURLConnection = null;
        DownloadInfoThread downloadInfoThread = null;
        try {
            httpURLConnection = HttpUtils.getHttpURLConnection(url);
            // 获取下载文件的总大小
            int contentLength = httpURLConnection.getContentLength();
            // 判断文件是否已经下载过
            if (localFileLength >= contentLength){
                LogUtils.info("{}已下载完毕，无需重新下载", httpFileName);
                return;
            }

            // 创建获取下载信息的任务对象
            downloadInfoThread = new DownloadInfoThread(contentLength);
            // 将任务交给线程执行，每隔一秒执行
            scheduledExecutorService.scheduleAtFixedRate(downloadInfoThread,1,1, TimeUnit.SECONDS);

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
            byte[] buffer = new byte[2014 * 100];

            while ((len = bis.read(buffer)) != -1) {
                downloadInfoThread.downSize += len;
                bos.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            System.out.println("下载的文件不存在");
            LogUtils.error("下载的文件不存在{}", url);
        } catch (IOException e) {

        } catch (Exception e) {
            LogUtils.error("下载失败");
        } finally {
            System.out.println("\r");
            System.out.println("下载完成");
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

             // 关闭
            scheduledExecutorService.shutdownNow();
        }

    }
}
