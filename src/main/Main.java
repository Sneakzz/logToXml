package main;

import controllers.Controller;
import java.util.Scanner;
import views.ConsoleUI;

/**
 *
 * @author Kenny
 */
public class Main {

    // Entry point of the application
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleUI ui = new ConsoleUI(scanner);
        Controller controller = new Controller(ui);

        controller.run();
    }
}
