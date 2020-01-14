package utils;

/**
 *
 * @author Kenny Veys
 */
public class LogUtils {

    public static void printMessage(String code, String message) {
        switch (code) {
            case "INFO":
                System.out.println("\n [INFO] - " + message + "\n");
                break;
            case "ERROR":
                System.out.println("\n [ERROR] - " + message + "\n");
                break;
            case "DEBUG":
                System.out.println("\n [DEBUG] - " + message + "\n");
                break;
            default:
                break;
        }
    }

    public static void printMessage(String message) {
        System.out.println("\n-----");
        System.out.println(message);
        System.out.println("-----\n");
    }
}
