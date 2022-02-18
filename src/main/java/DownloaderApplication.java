import core.Downloader;

import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2022/02/17/16:39
 * @Description:
 */
public class DownloaderApplication {
    public static void main(String[] args) {
        String url = null;
        if (args == null || args.length == 0) {
            for (; ; ) {
                System.out.println("请输入下载链接");
                Scanner scanner = new Scanner(System.in);
                url = scanner.next();
                if (url != null) {
                    break;
                }
            }
        } else {
            url = args[0];
        }
        Downloader downloader = new Downloader();
        downloader.download(url);
    }
}
