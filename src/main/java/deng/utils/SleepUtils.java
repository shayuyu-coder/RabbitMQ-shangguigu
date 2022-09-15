package deng.utils;

/**
 * 沉睡工具类
 * @author shayu
 * @Date 2022/9/7
 */
public class SleepUtils {
    public static void sleep(int second){
        try {
             Thread.sleep(1000*second);
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }
    }
}