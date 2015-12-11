package safepoint.fair;

import java.lang.management.ManagementFactory;

public class SafepointUsingThreadsDump extends LoveAtATimeOfSafepoints {
  
  
  @Override
  protected Object safepointMethod() {
    return ManagementFactory.getThreadMXBean().dumpAllThreads(false, false);
  }

}
