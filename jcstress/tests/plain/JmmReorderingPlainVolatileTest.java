package org.openjdk.jcstress.tests.jmm_custom.plain;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

@JCStressTest
@Description("Asserts no memory reordering when using volatile")
@Outcome(id = "-1", expect = Expect.ACCEPTABLE, desc = "Not initialized yet")
@Outcome(id = "5", expect = Expect.ACCEPTABLE, desc = "Returned correct value")
@Outcome(id = "0", expect = Expect.FORBIDDEN, desc = "Initialized but returned default value")
public class JmmReorderingPlainVolatileTest {

    /*
      ----------------------------------------------------------------------------------------------------------
        Fully correct.

        Results across all configurations:

        On ARM64:
          RESULT         SAMPLES     FREQ      EXPECT  DESCRIPTION
              -1  20,108,360,565   59.47%  Acceptable  Not initialized yet
               0               0    0.00%   Forbidden  Initialized but returned default value
               5  13,703,484,555   40.53%  Acceptable  Returned correct value
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
        private int x;
        private volatile boolean initialized = false;

        public void writer() {
            x = 5;
            initialized = true;
        }

        public int reader() {
            if (initialized) {
                return x;
            }
            return -1; // return mock value if not initialized
        }
    }
}
