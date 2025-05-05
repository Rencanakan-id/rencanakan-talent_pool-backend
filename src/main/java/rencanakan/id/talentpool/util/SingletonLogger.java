package rencanakan.id.talentpool.util;

/**
 * SingletonLogger yang dioptimasi untuk penggunaan memory minimal
 */
public class SingletonLogger {
    // Instance singleton
    private static volatile SingletonLogger instance;

    // Minimal state untuk mencatat log
    private final String loggerName = "OptimizedSingletonLogger";

    // Konstruktor private
    private SingletonLogger() {
        // Tidak membuat instance logger Java standard
        // untuk menghindari overhead memori
    }

    public static SingletonLogger getInstance() {
        if (instance == null) {
            synchronized (SingletonLogger.class) {
                if (instance == null) {
                    instance = new SingletonLogger();
                }
            }
        }
        return instance;
    }

    public void log(String message) {
        // Implementasi sangat ringan, tanpa buffer atau handler kompleks
        System.out.println("[INFO] " + loggerName + ": " + message);
    }
}