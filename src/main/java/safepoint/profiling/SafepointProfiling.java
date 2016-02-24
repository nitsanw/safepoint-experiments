package safepoint.profiling;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class SafepointProfiling {
  @Param("1000")
  int size;
  Thread[] pool;

  byte[] buffer;
  boolean result;

  @Setup
  public final void setup() {
    buffer = new byte[size];
  }

  @Benchmark
  public void blameSetResult() {
    byte b = 0;
    for (int i = 0; i < size; i++) {
      b += buffer[i];
    }
    setResult(b);
  }

  @Benchmark
  public void blameSetResultDeep() {
    byte b = 0;
    for (int i = 0; i < size; i++) {
      b += buffer[i];
    }
    setResult5(b);
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public void blameSetResultDeeper() {
    byte b = 0;
    for (int i = 0; i < size; i++) {
      b += buffer[i];
    }
    setResult8(b);
  }

  private void setResult8(byte b) {
    setResult7(b);
  }

  private void setResult7(byte b) {
    setResult6(b);
  }

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  private void setResult6(byte b) {
    setResult5(b);
  }

  private void setResult5(byte b) {
    setResult4(b);
  }

  private void setResult4(byte b) {
    setResult3(b);
  }

  private void setResult3(byte b) {
    setResult2(b);
  }

  private void setResult2(byte b) {
    setResult(b);
  }

  private void setResult(byte b) {
    setResult(b == 1);
  }

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  private void setResult(boolean b) {
    result = b;
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public void meSoHotNoInline() {
    byte b = 0;
    for (int i = 0; i < size; i++) {
      b += buffer[i];
    }
    result = b == 1;
  }

  @Benchmark
  public void meSoHotInline() {
    byte b = 0;
    for (int i = 0; i < size; i++) {
      b += buffer[i];
    }
    result = b == 1;
  }
}