package controllers;

import java.util.ArrayList;
import services.FileParser;
import models.Rendering;
import services.XmlCreator;
import utils.FileUtils;
import views.ConsoleUI;

/**
 *
 * @author Kenny
 */
public class MainController {

    private final ConsoleUI ui;
    private final ArrayList<Rendering> renderings;

    public MainController(ConsoleUI ui) {
        this.ui = ui;
        this.renderings = new ArrayList<>();
    }

    public void run() {
        this.ui.printWelcome();

        String logFilepath;
        // keep asking for a valid filepath until it results in a valid file for parsing
        // or the user decides to quit the application
        while (true) {
            logFilepath = this.ui.getInput();

            if (logFilepath.equalsIgnoreCase("quit")) {
                return;
            }

            if (FileUtils.isValidFile(logFilepath)) {
                break;
            }
        }

        // initiate the FileParser
        FileParser fileParser = new FileParser(logFilepath, this.renderings);
        fileParser.parseFile();

        // initiate the XmlCreator
        XmlCreator xmlCreator = new XmlCreator(this.renderings);
        xmlCreator.run();
    }
}
