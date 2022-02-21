package util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2022/02/17/16:55
 * @Description: http 相关工具类
 */
public class HttpUtils {


    /**
     * 获取下载文件的大小
     *
     * @param url url
     * @return
     * @throws IOException
     */
    public static long getHttpFileContentLength(String url) throws IOException {
        int contentLength = 0;
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = getHttpURLConnection(url);
            contentLength = httpURLConnection.getContentLength();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return contentLength;
    }


    /**
     * 文件分块下载
     *
     * @param url      请求地址
     * @param startPos 下载文件的起始位置
     * @param endPos   下载文件的结束位置
     * @return
     * @throws IOException
     */
    public static HttpURLConnection getHttpURLConnection(String url, long startPos, long endPos) throws IOException {
        HttpURLConnection httpURLConnection = getHttpURLConnection(url);
        LogUtils.info("下载的区间是:{} - {}", startPos, endPos);
        if (endPos != 0) {
            httpURLConnection.setRequestProperty("RANGE", "bytes=" + startPos + "-" + endPos);
        } else {
            httpURLConnection.setRequestProperty("RANGE", "bytes=" + startPos + "-");
        }
        return httpURLConnection;
    }


    /**
     * 获取HttpURLConnection链接对象
     *
     * @param url 文件的地址
     * @return
     */
    public static HttpURLConnection getHttpURLConnection(String url) throws IOException {
        URL httpUrl = new URL(url);
        HttpURLConnection httpURLConnection = ((HttpURLConnection) httpUrl.openConnection());
        // 想文件所在的服务器发送标识信息
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 6.0; Windows 2000)");
        return httpURLConnection;
    }

    /**
     * 获取下载文件的名称
     *
     * @param url
     * @return
     */
    public static String getHttpFileName(String url) {
        int index = url.lastIndexOf("/");
        return url.substring(index + 1);
    }

}
