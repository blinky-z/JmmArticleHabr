public class SynchronizedMemoryBarrierJIT {

    /*
        JSR-133 Cookbook (simplified):
        1. Issue an StoreLoad barrier either before each MonitorEnter or after each MonitorExit.
            StoreLoad is a no-op if either MonitorExit or MonitorEnter uses an atomic instruction that supplies the equivalent of a StoreLoad barrier.
            Similarly for others involving Enter and Exit in the remaining steps.
        2. Issue LoadLoad and LoadStore barriers after each MonitorEnter.
        3. Issue StoreStore and LoadStore barriers before each MonitorExit.
    */

    private static final Object lock = new Object();

    private static int field1;
    private static int field2;

    private static void write(int i) {
        /* StoreLoad */
        synchronized (lock) { /* MonitorEnter */
            /* LoadLoad + LoadStore */
            field1 = i << 1;
            field2 = i << 2;
            /* StoreStore + LoadStore */
        } /* MonitorExit */
    }

    private static void read() {
        /* StoreLoad */
        synchronized (lock) { /* MonitorEnter */
            /* LoadLoad + LoadStore */
            int r1 = field1;
            int r2 = field2;
            /* StoreStore + LoadStore */
        } /* MonitorExit */
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
