package core;

import constant.Constant;
import util.HttpUtils;
import util.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2022/02/21/10:36
 * @Description: 文件分块下载任务
 */
public class DownloadTask implements Callable<Boolean> {

    private String url;

    /**
     * 文件分块下载的开始位置
     */
    private long startPos;

    /**
     * 文件分块下载的结束位置
     */
    private long endPos;

    /**
     *
     */
    private int part;

    private CountDownLatch countDownLatch;

    public DownloadTask(String url, long startPos, long endPos, int part, CountDownLatch countDownLatch) {
        this.url = url;
        this.startPos = startPos;
        this.endPos = endPos;
        this.part = part;
        this.countDownLatch = countDownLatch;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Boolean call() throws Exception {
        // 获取文件名
        String httpFileName = HttpUtils.getHttpFileName(url);
        // 分块的文件名
        httpFileName = httpFileName + ".temp" + part;
        // 下载路径
        httpFileName = Constant.PATH + httpFileName;
        // 获取分块下载的链接
        HttpURLConnection httpURLConnection = HttpUtils.getHttpURLConnection(url, startPos, endPos);
        try(
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                RandomAccessFile accessFile = new RandomAccessFile(httpFileName,"rw")
        ) {
            byte[] buffer = new byte[Constant.BYTE_SIZE];
            int len = -1;
            // 循环读取数据
            while ((len=bis.read(buffer))!=-1){
                // 1秒内下载数据之和,通过原子类进行操作
                DownloadInfoThread.downSize.add(len);
                accessFile.write(buffer,0,len);
            }
        }catch (FileNotFoundException e) {
            LogUtils.error("下载的文件不存在{}", url);
            return false;
        } catch (IOException e) {
            LogUtils.error("下载失败");
            return false;
        } catch (Exception e) {
            LogUtils.error("下载失败");
            return false;
        }finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            countDownLatch.countDown();
        }
        return true;
    }
}
