package safepoint.hang;

public class WhenWillItExitInt {
    public static void main(String[] argc) throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                long l = 0;
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    for (int j = 0; j < Integer.MAX_VALUE; j++) {
                        if ((j & 1) == 1)
                            l++;
                    }
                }
                System.out.println("How Odd:" + l);
            }
        });
        t.setDaemon(true);
        t.start();
        Thread.sleep(5000);
    }
}
