package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import models.Rendering;
import utils.LogUtils;

/**
 *
 * @author Kenny
 */
public class FileParser {

    private BufferedReader br;
    private final String logFilepath;
    private final ArrayList<Rendering> renderings;

    // rendering specific variables, needed for the correct parsing of the log
    private Rendering rendering = null;
    private int currentStartRequestThread = -1;
    private String currentReturnUID = "-1";
    private int srDocId = -1;
    private int srPageNum = -1;

    public FileParser(String logFilepath, ArrayList<Rendering> renderings) {
        this.logFilepath = logFilepath;
        this.renderings = renderings;
    }

    public void parseFile() {
        LogUtils.printMessage("in progress of parsing the log..");

        try {
            this.br = new BufferedReader(new FileReader(this.logFilepath));

            // variables for updating the parsing progress
            long fileLength = new File(this.logFilepath).length();
            long readLength = 0;

            String currentLine;

            // start reading the file
            while ((currentLine = this.br.readLine()) != null) {
                readLength += (currentLine + "\r\n").getBytes().length;
                System.out.print("\rprogress parsing: " + updateProgress(readLength, fileLength) + "%");

                // send the read line to be parsed
                parseLine(currentLine);
            }

            this.br.close();

            System.out.println();
            LogUtils.printMessage("Done parsing the log.");

        } catch (FileNotFoundException e) {
            LogUtils.printMessage("ERROR", e.getMessage());
        } catch (IOException e) {
            LogUtils.printMessage("ERROR", e.getMessage());
        }
    }

    private void parseLine(String line) {
        if (line.contains("Executing request startRendering")) {
            parseStartRenderRequest(line);
        }

        if (line.contains("Service startRendering returned")) {
            parseStartRenderReturned(line);
        }

        if (line.contains("Executing request getRendering")) {
            parseGetRenderRequest(line);
        }
    }

    private void parseStartRenderRequest(String line) {
        String[] lineParts = line.split(" ");

        this.currentStartRequestThread = Integer.parseInt(lineParts[2].replaceAll("\\D", ""));
        this.srDocId = Integer.parseInt(lineParts[11].replaceAll("\\D", ""));
        this.srPageNum = Integer.parseInt(lineParts[12].replaceAll("\\D", ""));
    }

    private void parseStartRenderReturned(String line) {
        String[] lineParts = line.split(" ");

        int returnThread = Integer.parseInt(lineParts[2].replaceAll("\\D", ""));

        if (returnThread == this.currentStartRequestThread) {
            // means this start return thread corresponds with the start request thread
            String timestamp = lineParts[0].concat(" " + lineParts[1]);
            String returnUID = lineParts[9];

            if (returnUID.equals(this.currentReturnUID)) {
                // this means its a duplicate startRender so add timestamp to current rendering
                this.rendering.addStartRenderTimestamp(timestamp);
            } else {
                // store the UID for the new rendering
                this.currentReturnUID = lineParts[9];

                // create a rendering with current information
                this.rendering = new Rendering(this.srDocId, this.srPageNum, this.currentReturnUID);
                this.rendering.addStartRenderTimestamp(timestamp);

                // add created rendering to collection
                this.renderings.add(this.rendering);
            }

            // reset the currentStartRequestThread since a corresponding one was found
            this.currentStartRequestThread = -1;
        }
    }

    private void parseGetRenderRequest(String line) {
        String[] lineParts = line.split(" ");

        String getRenderTimestamp = lineParts[0].concat(" " + lineParts[1]);

        String getRenderUID = lineParts[11].substring(1, lineParts[11].length() - 1);

        if (this.currentReturnUID.equals(getRenderUID)) {
            // part of the same rendering so add the timestamp
            this.rendering.addGetRenderTimestamp(getRenderTimestamp);
        } else {
            // if the currentReturnUID is not the same, still check if
            // the getRenderUID is already in the collection just in case
            for (Rendering render : this.renderings) {
                if (render.getStartRenderUID().equals(getRenderUID)) {
                    render.addGetRenderTimestamp(getRenderTimestamp);
                }
            }
        }
    }

    private double updateProgress(long readLength, long fileLength) {
        return Math.ceil(((double) readLength / fileLength) * 100);
    }
}
