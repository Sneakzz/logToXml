package views;

import java.util.Scanner;

/**
 *
 * @author Kenny
 */
public class ConsoleUI {

    private final Scanner scanner;

    public ConsoleUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void printWelcome() {
        // welcome message
        System.out.println("---------------------");
        System.out.println("Log to XML converter");
        System.out.println("---------------------");
    }

    public String getInput() {
        System.out.println("\nEnter the valid filepath of the log you wish to convert or enter 'quit' to stop the application");
        System.out.print("-> ");
        return this.scanner.nextLine();
    }
}
