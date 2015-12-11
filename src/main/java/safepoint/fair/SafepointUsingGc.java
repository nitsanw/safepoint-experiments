package safepoint.fair;

public class SafepointUsingGc extends LoveAtATimeOfSafepoints {
  @Override
  protected Object safepointMethod() {
    System.gc();
    return null;
  }
}
