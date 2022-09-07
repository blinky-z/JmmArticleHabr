public class JmmDekkerInstructionsReorderingJIT {

    private static int x;
    private static int y;

    private static int T1(int i) {
        x = i;
        return y;
    }

    private static int T2(int i) {
        y = i;
        return x;
    }

    public static void main(String[] args) throws Exception {
        // invoke JIT
        for (int i = 0; i < 10000; i++) {
            T1(i);
            T2(i);
        }
        Thread.sleep(1000);
    }
}
