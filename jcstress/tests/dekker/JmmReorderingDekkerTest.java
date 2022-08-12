package org.openjdk.jcstress.tests.jmm_custom.dekker;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

/**
 * Classic test that demonstrates (StoreLoad) memory reordering
 * <p>
 * This reordering can be observed in the
 * <a href="https://en.wikipedia.org/wiki/Dekker%27s_algorithm">Dekker's algorithm</a> hence the name of this test.
 * <p>
 * It's an especially good test for Total Store Order micro-architecture like x86 where we encounter (0, 0) case
 * because of StoreLoad memory reordering caused by Store Buffer.
 */
@JCStressTest
@Description("Classic test that demonstrates memory reordering")
@Outcome(id = "1, 1", expect = Expect.ACCEPTABLE, desc = "Have seen both writes")
@Outcome(id = {"0, 1", "1, 0"}, expect = Expect.ACCEPTABLE, desc = "Have seen one of the writes")
@Outcome(id = "0, 0", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Have not seen any write")
public class JmmReorderingDekkerTest {

    /*
      ----------------------------------------------------------------------------------------------------------
        Results across all configurations:

        On x86:
          RESULT         SAMPLES     FREQ       EXPECT  DESCRIPTION
            0, 0   2,188,517,311   18,91%  Interesting  Have not seen any write
            0, 1   4,671,980,718   40,36%   Acceptable  Have seen one of the writes
            1, 0   4,708,890,866   40,68%   Acceptable  Have seen one of the writes
            1, 1       5,569,185    0,05%   Acceptable  Have seen both writes

        On ARM64:
          RESULT         SAMPLES     FREQ       EXPECT  DESCRIPTION
            0, 0  11,498,581,625   26.20%  Interesting  Have not seen any write
            0, 1  17,019,572,342   38.77%   Acceptable  Have seen one of the writes
            1, 0  15,374,126,442   35.03%   Acceptable  Have seen one of the writes
            1, 1       1,510,311   <0.01%   Acceptable  Have seen both writes
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
        private int x;
        private int y;

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
