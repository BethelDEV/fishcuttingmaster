package funs.gamez.fishz.helps;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 工具类
 */
public class Utils {
    // 生成随机数
    public static int getRand(int max) {
        return (int) (max*Math.random());
    }

    // 生成随机数
    public static double getRand(double min, double max) {
        return (max-min)*Math.random() + min;
    }

    public static int getRandInt(int min, int max) {
        return (int)((max-min)*Math.random() + min);
    }

    public static synchronized TimerTask asyncRepeat(final Runnable r, final int repeatmillis, final int times, final Runnable last) {
        final int[] counter = new int[1];
        counter[0]=0;
        final Timer t = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (counter[0]++ >= times) {
                        t.cancel();

                        if (last!=null) last.run();

                        return;
                    }
                    r.run();
                } catch (Exception | Error e) {
                    t.cancel();
                }
            }
        };
        t.schedule(task, repeatmillis, repeatmillis);

        return task;
    }

    public static void async(final Runnable r) {
        ThreadPool.execute(r);
    }
}
