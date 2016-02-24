package safepoint.hang;

public class WhenWillItExitLong {
    public static void main(String[] argc) throws InterruptedException {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                long l = 0;
                for (long i = 0; i < Long.MAX_VALUE; i++) {
                    if ((i & 1) == 1)
                        l++;
                }
                System.out.println("How Odd:" + l);
            }
        });
        t.setDaemon(true);
        t.start();
        Thread.sleep(5000);
    }
}
