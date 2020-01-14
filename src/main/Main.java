package main;

import controllers.MainController;
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
        MainController mainController = new MainController(ui);

        mainController.run();
    }
}
