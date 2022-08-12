public class VolatileMemoryBarrierJIT {

    /*
        JSR-133 Cookbook:
        1. Issue a StoreStore barrier before each volatile store.
        2. Issue a StoreLoad barrier after each volatile store.
            Note that you could instead issue one before each volatile load, but this would be slower for typical programs using volatiles in which reads greatly outnumber writes.
            Alternatively, if available, you can implement volatile store as an atomic instruction (for example XCHG on x86) and omit the barrier.
            This may be more efficient if atomic instructions are cheaper than StoreLoad barriers.
        3. Issue LoadLoad and LoadStore barriers after each volatile load.
            On processors that preserve data dependent ordering, you need not issue a barrier if the next access instruction is dependent on the value of the load.
            In particular, you do not need a barrier after a load of a volatile reference if the subsequent instruction is a null-check or load of a field of that reference.
    */

    private static int field1;
    private volatile static int field2;

    private static void write(int i) {
        field1 = i << 1;
        /* StoreStore */
        field2 = i << 2;
        /* StoreLoad */
    }

    private static void read() {
        int r1 = field2;
        /* LoadLoad + LoadStore */
        int r2 = field1;
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
