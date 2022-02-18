package util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2022/02/17/16:55
 * @Description: http相关工具类
 */
public class HttpUtils {
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
