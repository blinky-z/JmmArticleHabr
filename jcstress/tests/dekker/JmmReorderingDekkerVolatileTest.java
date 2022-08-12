package org.openjdk.jcstress.tests.jmm_custom.dekker;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

@JCStressTest
@Description("Asserts no memory reordering when using volatile")
@Outcome(id = "1, 1", expect = Expect.ACCEPTABLE, desc = "Have seen both writes")
@Outcome(id = {"0, 1", "1, 0"}, expect = Expect.ACCEPTABLE, desc = "Have seen one of the writes")
@Outcome(id = "0, 0", expect = Expect.FORBIDDEN, desc = "Have not seen any write")
public class JmmReorderingDekkerVolatileTest {

    /*
      ----------------------------------------------------------------------------------------------------------
        Fully correct.

        Results across all configurations:

        On ARM64:
          RESULT         SAMPLES     FREQ      EXPECT  DESCRIPTION
            0, 0               0    0.00%   Forbidden  Have not seen any write
            0, 1  12,062,185,785   50.58%  Acceptable  Have seen one of the writes
            1, 0  11,521,290,503   48.31%  Acceptable  Have seen one of the writes
            1, 1     262,964,672    1.10%  Acceptable  Have seen both writes
    */

    @Actor
    public final void actor1(DataHolder dataHolder, II_Result r) {
        r.r1 = dataHolder.actor1();
    }

    @Actor
    public final void actor2(DataHolder dataHolder, II_Result r) {
        r.r2 = dataHolder.actor2();
    }

    @State
    public static class DataHolder {
        private volatile int x;
        private volatile int y;

        public int actor1() {
            x = 1;
            return y;
        }

        public int actor2() {
            y = 1;
            return x;
        }
    }
}
