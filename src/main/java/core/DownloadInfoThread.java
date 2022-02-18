package core;

import constant.Constant;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2022/02/18/14:47
 * @Description: 展示下载信息
 */
public class DownloadInfoThread implements Runnable {

    /**
     * 下载文件总大小
     */
    private long httpFileContentLength;

    /**
     * 本地已下载文件的大小
     */
    private double finishedSize;

    /**
     * 本次累计下载的大小
     */
    public volatile double downSize;

    /**
     * 前一次下载的大小
     */
    public double prevSize;

    public DownloadInfoThread(long httpFileContentLength) {
        this.httpFileContentLength = httpFileContentLength;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        // 计算文件总大小 单位：MB
        String httpFileContent = String.format("%.2f", httpFileContentLength / Constant.MB);

        // 计算每秒下载速度 单位：kb
        int speed = (int) ((downSize - prevSize) / 1024d);
        prevSize = downSize;

        // 剩余文件的大小
        double remainSize = httpFileContentLength - finishedSize - downSize;

        // 计算剩余时间
        String remainTime = String.format("%.1f", remainSize / 1024d / speed);
        if ("Infinity".equalsIgnoreCase(remainTime)) {
            remainTime = "-";
        }

        // 已下载大小
        String currentFileSize = String.format("%.2f", (downSize - finishedSize) / Constant.MB);

        String downloadInfo = String.format("已下载 %smb/%smb，速度 %skb/s，剩余时间 %ss",
                currentFileSize,
                httpFileContent,
                speed,
                remainTime);
        System.out.print("\r");
        System.out.print(downloadInfo);
    }
}
