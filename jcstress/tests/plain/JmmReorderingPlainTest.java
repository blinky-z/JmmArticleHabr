package org.openjdk.jcstress.tests.jmm_custom.plain;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

/**
 * This test fails because of either StoreStore or LoadLoad reordering
 * <p>
 * It fails on x86 only because of compiler's Instruction Scheduling, but not because of CPU's reordering
 * as x86 is a Total Store Order micro-architecture which allows only StoreLoad memory reordering.
 */
@JCStressTest
@Description("Triggers memory reordering")
@Outcome(id = "-1", expect = Expect.ACCEPTABLE, desc = "Not initialized yet")
@Outcome(id = "5", expect = Expect.ACCEPTABLE, desc = "Returned correct value")
@Outcome(id = "0", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Initialized but returned default value")
public class JmmReorderingPlainTest {

    /*
      ----------------------------------------------------------------------------------------------------------
        Results across all configurations:

        On x86:
          RESULT         SAMPLES     FREQ       EXPECT  DESCRIPTION
              -1   5,004,050,680   38,73%   Acceptable  Not initialized yet
               0         168,651   <0,01%  Interesting  Initialized but returned default value
               5   7,916,756,029   61,27%   Acceptable  Returned correct value

        On ARM64:
          RESULT         SAMPLES     FREQ       EXPECT  DESCRIPTION
              -1  24,335,425,974   51.19%   Acceptable  Not initialized yet
               0     131,604,572    0.28%  Interesting  Initialized but returned default value
               5  23,076,941,294   48.54%   Acceptable  Returned correct value
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
        private boolean initialized = false;

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
