package core;

import constant.Constant;
import util.FileUtils;
import util.HttpUtils;
import util.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2022/02/17/17:11
 * @Description: 下载器
 */
public class Downloader {

    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    public ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            Constant.THREAD_NUM,
            Constant.THREAD_NUM,
            0,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(Constant.THREAD_NUM));

    private CountDownLatch countDownLatch = new CountDownLatch(Constant.THREAD_NUM);


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

            // 切分任务
            ArrayList<Future> list = new ArrayList<>();
            split(url,list);

            /*list.forEach(future -> {
                try {
                    // 获取这个结果主要是为了线程在这里阻塞，方便后续合并文件
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });*/
            countDownLatch.await();

            if (merge(httpFileName)) {
                clearTemp(httpFileName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try (
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
        }*/ catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("\r");
            System.out.println("下载完成");
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

             // 关闭
            scheduledExecutorService.shutdownNow();
            threadPoolExecutor.shutdown();
        }

    }

    /**
     * 文件切分
     *
     * @param url
     * @param futureArrayList
     */
    public void split(String url, ArrayList<Future> futureArrayList){
        // 获取下载文件的大小
        try {
            long contentLength = HttpUtils.getHttpFileContentLength(url);
            // 计算切分后的大小
            long size = contentLength / Constant.THREAD_NUM;
            for (int i = 0; i < Constant.THREAD_NUM; i++) {
                // 计算下载起始位置
                long startPos = i * size;
                // 计算下载结束位置
                long endPos;
                if (i == Constant.THREAD_NUM - 1){
                    endPos = 0;
                }else{
                    endPos = startPos + size;
                }
                // 如果不是第一块，起始位置要+1
                if (startPos != 0){
                    startPos++;
                }
                DownloadTask downloadTask = new DownloadTask(url, startPos, endPos, i, countDownLatch);
                // 将任务提交到线程池中
                Future<Boolean> future = threadPoolExecutor.submit(downloadTask);
                futureArrayList.add(future);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 文件合并处理
     *
     * @param fileName
     * @return
     */
    public boolean merge(String fileName) {
        LogUtils.info("开始合并文件{}", fileName);
        byte[] buffer = new byte[Constant.BYTE_SIZE];
        int len = -1;
        try (RandomAccessFile accessFile = new RandomAccessFile(fileName, "rw")) {
            for (int i = 0; i < Constant.THREAD_NUM; i++) {
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName + ".temp" + i))) {
                    while ((len = bis.read(buffer)) != -1) {
                        accessFile.write(buffer, 0, len);
                    }
                }
            }
            LogUtils.info("合并文件完成{}", fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 清空临时文件
     *
     * @param fileName
     * @return
     */
    public boolean clearTemp(String fileName) {
        for (int i = 0; i < Constant.THREAD_NUM; i++) {
            File file = new File(fileName + ".temp" + i);
            file.delete();
        }
        return true;
    }




}
