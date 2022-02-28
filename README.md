# Java-thread-downloader
这是一个利用Java原生代码实现的简易多线程下载器

#### 文件切分关键代码

```java
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
```

#### 文件合并关键代码

```java
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
```

#### 清空临时文件关键代码

```java
public boolean clearTemp(String fileName) {
        for (int i = 0; i < Constant.THREAD_NUM; i++) {
            File file = new File(fileName + ".temp" + i);
            file.delete();
        }
        return true;
    }
```

#### 文件下载核心代码

```java
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
```
