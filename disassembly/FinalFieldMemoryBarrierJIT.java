public class FinalFieldMemoryBarrierJIT {

    /*
        JSR-133 Cookbook:
        1. Issue a StoreStore barrier after all stores but before return from any constructor for any class with a final field.
        2. If on a processor that does not intrinsically provide ordering on indirect loads, issue a LoadLoad barrier before each load of a final field
    */

    private static class Foo {
        private final int x;

        Foo(int x) {
            this.x = x;
            /* StoreStore */
        }
    }

    private static Foo foo;

    private static void write(int i) {
        foo = new Foo(i);
    }

    private static void read() {
        Foo localFoo = foo;
        /* LoadLoad */
        int r1 = localFoo.x;
    }

    public static void main(String[] args) throws Exception {
        // invoke JIT
        for (int i = 0; i < 10000; i++) {
            write(i);
            read();
        }
        Thread.sleep(1000);
    }
}
