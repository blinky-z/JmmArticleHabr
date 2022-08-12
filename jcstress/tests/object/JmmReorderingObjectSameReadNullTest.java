package org.openjdk.jcstress.tests.jmm_custom.object;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

/**
 * We observe null object on the second read because of LoadLoad memory reordering
 * <p>
 * This test shows that independent reads can be reordered even for the same variable.
 * <p>
 * It fails on x86 only because of compiler's Instructions Scheduling.
 */
@JCStressTest
@Description("Triggers memory reordering")
@Outcome(id = "1, 1", expect = Expect.ACCEPTABLE, desc = "Performed both reads late")
@Outcome(id = "0, 0", expect = Expect.ACCEPTABLE, desc = "Performed both reads too early")
@Outcome(id = "0, 1", expect = Expect.ACCEPTABLE, desc = "Performed first read too early, but seen on the second read")
@Outcome(id = "1, 0", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Have seen null object on the second read")
@State
public class JmmReorderingObjectSameReadNullTest {

    /*
      ----------------------------------------------------------------------------------------------------------
        Results across all configurations:

        On x86:
          RESULT        SAMPLES     FREQ       EXPECT  DESCRIPTION
            0, 0  2,922,797,251   31,12%   Acceptable  Performed both reads too early
            0, 1     67,622,753    0,72%   Acceptable  Performed first read too early, but seen on the second read
            1, 0     10,121,930    0,11%  Interesting  Have seen null object on the second read
            1, 1  6,392,896,786   68,06%   Acceptable  Performed both reads late
    */

    private DataHolder h1 = new DataHolder();
    private DataHolder h2 = h1;

    @Actor
    public final void actor1() {
        h1.instance = new Foo();
    }

    @Actor
    public final void actor2(II_Result r) {
        DataHolder h1 = this.h1;
        DataHolder h2 = this.h2;

        /* helping to trigger reordering */
        h1.trap = 0;
        h2.trap = 0;

        r.r1 = map(h1.instance);
        r.r2 = map(h2.instance);
    }

    private int map(Foo obj) {
        if (obj == null) {
            return 0;
        }
        return 1;
    }

    private static class Foo {
    }

    public static class DataHolder {
        private Foo instance;
        private int trap; /* for internal use */
    }
}
