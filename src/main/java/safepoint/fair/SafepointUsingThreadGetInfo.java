package safepoint.fair;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ThreadLocalRandom;

public class SafepointUsingThreadGetInfo extends LoveAtATimeOfSafepoints {
  @Override
  protected Object safepointMethod() {
    long[] threadIds = ManagementFactory.getThreadMXBean().getAllThreadIds();
    long tid = threadIds[ThreadLocalRandom.current().nextInt(threadIds.length)];
    return ManagementFactory.getThreadMXBean().getThreadInfo(tid, 256);
  }

}
