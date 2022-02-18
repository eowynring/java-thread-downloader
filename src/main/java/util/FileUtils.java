package util;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2022/02/18/15:07
 * @Description:
 */
public class FileUtils {

    /**
     * 获取本地文件的大小
     *
     * @param path 文件路径
     * @return
     */
    public static long getFileContentLength(String path) {
        File file = new File(path);
        return file.exists() && file.isFile() ? file.length() : 0;
    }
}

