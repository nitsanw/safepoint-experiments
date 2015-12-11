package safepoint.fair;

import java.lang.management.ManagementFactory;

public class SafepointUsingFindDeadlocks extends SafepointUsingThreadsDump {
  @Override
  protected Object safepointMethod() {
    return ManagementFactory.getThreadMXBean().findDeadlockedThreads();
  }

}
