package kireiko.dev.millennium.ml.logic;

public class Logger {
    public static void info(String msg) {
        System.out.println("[MILLENNIUM] [Info] " + msg);
    }

    public static void warn(String msg) {
        System.out.println("[MILLENNIUM] [Warn] " + msg);
    }

    public static void error(String msg) {
        System.out.println("[MILLENNIUM] [Error] " + msg);
    }
}
