package rencanakan.id.talentpool.util;

import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;

/**
 * NonSingletonLogger dengan konsumsi memory yang tinggi untuk menunjukkan perbedaan dengan Singleton
 */
public class NonSingletonLogger {
    private final Logger logger;
    private final ConsoleHandler consoleHandler;
    private final SimpleFormatter formatter;

    // Tambahkan beberapa data memory-intensive untuk memperjelas perbedaan
    private final List<String> logHistory;
    private final byte[] bufferMemory;

    public NonSingletonLogger(String name) {
        // Buat logger baru dengan nama unik
        logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);

        // Simpan history log (memory-intensive)
        logHistory = new ArrayList<>();

        // Alokasikan buffer memory untuk simulasi penggunaan resource
        bufferMemory = new byte[1024 * 50]; // 50KB per instance

        // Inisialisasi buffer dengan beberapa data
        for (int i = 0; i < bufferMemory.length; i++) {
            bufferMemory[i] = (byte)(i % 256);
        }

        // Tambahkan custom handler untuk format log
        formatter = new SimpleFormatter() {
            @Override
            public String format(java.util.logging.LogRecord record) {
                return String.format("[%s] %s: %s%n",
                        record.getLevel(),
                        record.getLoggerName(),
                        record.getMessage());
            }
        };

        consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.INFO);
    }

    public void log(String message) {
        logger.info(message);

        // Simpan log ke history (meningkatkan penggunaan memory)
        logHistory.add(message);
    }
}