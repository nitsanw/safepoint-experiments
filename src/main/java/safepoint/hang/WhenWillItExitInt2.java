package safepoint.hang;

public class WhenWillItExitInt2 {
    public static void main(String[] argc) throws InterruptedException {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                long l = countOdds(Integer.MAX_VALUE);
                System.out.println("How Odd:" + l);
            }
        });
        t.setDaemon(true);
        t.start();
        Thread.sleep(5000);
    }

    private static long countOdds(int limit) {
        long l = 0;
        for (int i = 0; i < limit; i++) {
            for (int j = 0; j < limit; j++) {
                if ((j & 1) == 1)
                    l++;
            }
        }
        return l;
    }
}
