package utils;

public class Utils {

    public static void println(Object obj) {
        System.out.println(obj);
    }

    public static void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String threadName() {
        return Thread.currentThread().getName();
    }

    public static ThreadLocal<String> suffix = new ThreadLocal<>();

    static {
        suffix.set("World");
    }


}
