package org.openjdk.jcstress.tests.jmm_custom.plain;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

@JCStressTest
@Description("Asserts no memory reordering when using monitor lock")
@Outcome(id = "-1", expect = Expect.ACCEPTABLE, desc = "Not initialized yet")
@Outcome(id = "5", expect = Expect.ACCEPTABLE, desc = "Returned correct value")
@Outcome(id = "0", expect = Expect.FORBIDDEN, desc = "Initialized but returned default value")
public class JmmReorderingPlainSynchronizedTest {

    /*
      ----------------------------------------------------------------------------------------------------------
        Fully correct.

        Results across all configurations:

        On ARM64:
          RESULT         SAMPLES     FREQ      EXPECT  DESCRIPTION
              -1  10,196,346,746   47.96%  Acceptable  Not initialized yet
               0               0    0.00%   Forbidden  Initialized but returned default value
               5  11,065,958,534   52.04%  Acceptable  Returned correct value
    */

    @Actor
    public final void actor1(DataHolder dataHolder) {
        dataHolder.writer();
    }

    @Actor
    public final void actor2(DataHolder dataHolder, I_Result r) {
        r.r1 = dataHolder.reader();
    }

    @State
    public static class DataHolder {
        private final Object lock = new Object();

        private int x;
        private boolean initialized = false;

        public void writer() {
            synchronized (lock) {
                x = 5;
                initialized = true;
            }
        }

        public int reader() {
            synchronized (lock) {
                if (initialized) {
                    return x;
                }
                return -1; // return mock value if not initialized
            }
        }
    }
}
