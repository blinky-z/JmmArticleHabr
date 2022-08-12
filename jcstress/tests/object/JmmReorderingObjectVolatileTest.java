package org.openjdk.jcstress.tests.jmm_custom.object;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

@JCStressTest
@Description("Asserts no memory reordering when using volatile")
@Outcome(id = "-1", expect = Expect.ACCEPTABLE, desc = "Object is not seen")
@Outcome(id = "5", expect = Expect.ACCEPTABLE, desc = "Object is observed in full")
@Outcome(id = "0", expect = Expect.FORBIDDEN, desc = "Object's data is null")
@Outcome(id = "1", expect = Expect.FORBIDDEN, desc = "Returned null object")
public class JmmReorderingObjectVolatileTest {

    /*
      ----------------------------------------------------------------------------------------------------------
        Fully correct.

        Results across all configurations:

        On ARM64:
          RESULT         SAMPLES     FREQ      EXPECT  DESCRIPTION
              -1  24,673,210,973   66.98%  Acceptable  Object is not seen
               0               0    0.00%   Forbidden  Object's data is null
               1               0    0.00%   Forbidden  Returned null object
               5  12,165,301,667   33.02%  Acceptable  Object is observed in full
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

        private volatile Foo instance;

        public void writer() {
            instance = new Foo();
        }

        public Foo reader() {
            if (instance == null) {
                return Foo.mock; // return mock value if not initialized
            }
            return instance;
        }
    }
}
