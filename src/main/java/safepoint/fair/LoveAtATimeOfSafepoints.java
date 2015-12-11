package safepoint.fair;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public abstract class LoveAtATimeOfSafepoints {
  @Param("32768")
  int size;
  @Param("10")
  int k;
  @Param("100")
  int intervalMs;

  @Param({ "0" })
  int idleThreads;
  Thread[] pool;

  byte[] soggyBottoms;

  @Setup
  public final void bakeACake() {
    soggyBottoms = new byte[size];
    if (idleThreads != 0) {
      pool = new Thread[idleThreads];
      for (int i = 0; i < idleThreads; i++) {
        pool[i] = new Thread(() -> {
          parkDeep(256);
        });
        pool[i].setDaemon(true);
        pool[i].start();
      }
    }
  }

  static void parkDeep(int i) {
    if (i == 0) {
      LockSupport.park();
    } else {
      parkDeep(i - 1);
    }
  }

  @Benchmark
  @Group("run_together")
  public boolean contains1ToK() {
    byte[] haystack = soggyBottoms;
    for (int needle = 1; needle <= k; needle++) {
      if (containsNeedle2(needle, haystack)) {
        return true;
      }
    }
    return false;
  }

  @Benchmark
  @Group("run_together")
  public boolean contains1() {
    int needle = 1;
    byte[] haystack = soggyBottoms;
    return containsNeedle1(needle, haystack);
  }

  private static boolean containsNeedle1(int needle, byte[] haystack) {
    for (int i = 0; i < haystack.length - 3; i++) {
      if (((haystack[i] << 24) | (haystack[i + 1] << 16) | (haystack[i + 2] << 8) | haystack[i + 3]) == needle) {
        return true;
      }
    }
    return false;
  }

  private static boolean containsNeedle2(int needle, byte[] haystack) {
    for (int i = 0; i < haystack.length - 3; i++) {
      if (((haystack[i] << 24) | (haystack[i + 1] << 16) | (haystack[i + 2] << 8) | haystack[i + 3]) == needle) {
        return true;
      }
    }
    return false;
  }

  @Benchmark
  @Group("run_together")
  public Object safepoint() {
    if (intervalMs == 0)
      return null;
    LockSupport.parkNanos(intervalMs * 1_000_000);
    return safepointMethod();
  }

  protected abstract Object safepointMethod();

  @Benchmark
  @Group("run_together")
  public void park() {
    if (intervalMs == 0)
      return;
    LockSupport.parkNanos(intervalMs * 1_000_000);
  }
}
