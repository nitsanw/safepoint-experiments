package safepoint.hang;
public class WhenWillItExitInt3 {
  public static void main(String[] argc) throws InterruptedException {
    for (int i=0;i<100000;i++) {
      countOdds(10);
    }
    Thread t = new Thread(() -> {
      long l = countOdds(Integer.MAX_VALUE);
      System.out.println("How Odd:" + l);
    });
    t.setDaemon(true);
    t.start();
    Thread.sleep(5000);
  }

  private static long countOdds(int limit) {
    long l = 0;
    int i = 0;
    while (i++ < limit) {
      for (int j = 0; j < limit; j++) {
        if ((j & 1) == 1)
          l++;
      }
    }
    return l;
  }
}
