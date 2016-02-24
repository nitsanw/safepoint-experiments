package safepoint.hang;

public class WhenWillItExitIntInfinite {
    public static void main(String[] argc) throws InterruptedException {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                long l = 0;
                for (int i = 0; i < Integer.MAX_VALUE; i += 2) {
                    if ((i & 4) == 1)
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
