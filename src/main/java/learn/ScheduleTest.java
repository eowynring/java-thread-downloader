package learn;

import sun.awt.image.ToolkitImage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2022/02/18/14:16
 * @Description:
 */
public class ScheduleTest {
    public static void main(String[] args) {
        scheduleWithFixedDelay();
    }

    /**
     * scheduleWithFixedDelay
     * 该方法的作用是按照指定的时间延迟执行，并且每间隔一段时间再继续执行
     * 在执行任务的时候，无论耗时多久，任务执行结束之后都会等待间隔时间之后再继续执行下次任务
     */
    public static void scheduleWithFixedDelay(){
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        // 延迟2s后开始执行任务，每间隔3S再执行任务
        scheduledExecutorService.scheduleWithFixedDelay(()-> {
                    System.out.println(System.currentTimeMillis());
                    try {
                        TimeUnit.SECONDS.sleep(6);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                },
                2,
                3,
                TimeUnit.SECONDS);
    }


    /**
     * scheduleAtFixedRate
     * 该方法的作用是按照指定的时间延迟执行，并且每间隔一段时间再继续执行
     * 倘若在执行任务的时候，耗时超过了间隔时间，则任务执行结束后直接再次执行，而不是再等待间隔时间执行
     */
    public static void scheduleAtFixedRate(){
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        // 延迟2s后开始执行任务，每间隔3S再执行任务
        scheduledExecutorService.scheduleAtFixedRate(()-> {
                    System.out.println(System.currentTimeMillis());
                    try {
                        TimeUnit.SECONDS.sleep(6);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                },
                2,
                3,
                TimeUnit.SECONDS);
    }

    public static void schedule(){
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        // 延迟2s之后再执行
        scheduledExecutorService.schedule(
                () -> System.out.println(Thread.currentThread().getName()),
                2,
                TimeUnit.SECONDS);
    }
}
