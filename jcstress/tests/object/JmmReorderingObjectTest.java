package org.openjdk.jcstress.tests.jmm_custom.object;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

/**
 * <ul>
 *     <li>We observe inconsistent object's data because of StoreStore reordering, i.e. reference was published before
 *     performing constructor writes</li>
 *     <li>We observe null object because of LoadLoad reordering, i.e. independent reads reordering</li>
 * </ul>
 * <p>
 * This test barely reproduces the case of observing null object on the second read.
 * Please see {@code JmmReorderingObjectSameReadNullTest} for better reproduced results.
 */
@JCStressTest
@Description("Triggers memory reordering")
@Outcome(id = "-1", expect = Expect.ACCEPTABLE, desc = "Object is not seen")
@Outcome(id = "5", expect = Expect.ACCEPTABLE, desc = "Object is observed in full")
@Outcome(id = "0", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Object's data is null")
@Outcome(id = "1", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Returned null object")
public class JmmReorderingObjectTest {

    /*
      ----------------------------------------------------------------------------------------------------------
        We can't observe inconsistent object's data on x86 because of strong enough guarantees.
        But it's easily reproduced on ARM 64.

        Results across all configurations:

        On x86:
          RESULT         SAMPLES     FREQ       EXPECT  DESCRIPTION
              -1   7,892,727,553   51,78%   Acceptable  Object is not seen
               0               0    0,00%  Interesting  Object's data is null
               1               0    0,00%  Interesting  Returned null object
               5   7,349,082,367   48,22%   Acceptable  Object is observed in full

        On ARM64:
          RESULT         SAMPLES     FREQ       EXPECT  DESCRIPTION
              -1  38,403,169,617   85.37%   Acceptable  Object is not seen
               0       2,546,496   <0.01%  Interesting  Object's data is null
               1               0    0.00%  Interesting  Returned null object
               5   6,576,893,807   14.62%   Acceptable  Object is observed in full
    */

    @Actor
    public final void actor1(DataHolder dataHolder) {
        dataHolder.writer();
    }

    @Actor
    public final void actor2(DataHolder dataHolder, I_Result r) {
        r.r1 = map(dataHolder.reader());
    }

    private int map(Foo obj) {
        if (obj == Foo.mock) {
            return -1;
        }
        if (obj == null) {
            return 1;
        }
        return obj.x;
    }

    private static class Foo {

        static final Foo mock = new Foo();

        private int x;

        Foo() {
            this.x = 5;
        }
    }

    @State
    public static class DataHolder {

        private Foo instance;

        public void writer() {
            instance = new Foo();
        }

        public Foo reader() {
            if (instance == null) {
                return Foo.mock; // return mock value if not initialized
            }
            return instance; // can return null
        }
    }
}
