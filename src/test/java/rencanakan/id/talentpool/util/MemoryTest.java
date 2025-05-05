package rencanakan.id.talentpool.util;

public class MemoryTest {
    public static void main(String[] args) {
        System.gc();

        printMemoryUsage("Baseline");

        System.out.println("\n=== TESTING NON-SINGLETON LOGGER ===");
        nonSingletonTest();

        System.out.println("\n=== TESTING SINGLETON LOGGER ===");
        singletonTest();
    }

    private static void nonSingletonTest() {
        System.gc();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long startMemory = getUsedMemory();

        NonSingletonLogger[] loggers = new NonSingletonLogger[10_000];

        for (int i = 0; i < 10_000; i++) {
            loggers[i] = new NonSingletonLogger("Logger-" + i);
            loggers[i].log("Test " + i);
        }

        System.out.println(">>> All NonSingletonLogger instances created. Waiting for logs to finish...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.gc();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endMemory = getUsedMemory();
        System.out.println("[Non-Singleton Logger] Memory used: " + endMemory + " MB");
        System.out.println("[Non-Singleton Logger] Memory increase: " + (endMemory - startMemory) + " MB");
    }

    private static void singletonTest() {
        System.gc();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long startMemory = getUsedMemory();

        SingletonLogger singletonLogger = SingletonLogger.getInstance();
        for (int i = 0; i < 10_000; i++) {
            singletonLogger.log("Test " + i);
        }

        System.out.println(">>> All SingletonLogger calls completed. Waiting for logs to finish...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.gc();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endMemory = getUsedMemory();
        System.out.println("[Singleton Logger] Memory used: " + endMemory + " MB");
        System.out.println("[Singleton Logger] Memory increase: " + (endMemory - startMemory) + " MB");
    }

    private static void printMemoryUsage(String scenario) {
        long usedMemory = getUsedMemory();
        System.out.println("[" + scenario + "] Memory used: " + usedMemory + " MB");
    }

    private static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
    }
}