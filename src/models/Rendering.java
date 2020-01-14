package models;

import java.util.ArrayList;

/**
 *
 * @author Kenny
 */
public class Rendering {

    private final int documentId;
    private final int pageNumber;
    private final String startRenderUID;
    private final ArrayList<String> startRenderTimes;
    private final ArrayList<String> getRenderTimes;

    public Rendering(int docId, int pageNum, String UID) {
        this.documentId = docId;
        this.pageNumber = pageNum;
        this.startRenderUID = UID;
        this.startRenderTimes = new ArrayList<>();
        this.getRenderTimes = new ArrayList<>();
    }

    public void addStartRenderTimestamp(String timestamp) {
        this.startRenderTimes.add(timestamp);
    }

    public void addGetRenderTimestamp(String timestamp) {
        this.getRenderTimes.add(timestamp);
    }

    public ArrayList<String> getStartRenders() {
        return this.startRenderTimes;
    }

    public ArrayList<String> getGetRenders() {
        return this.getRenderTimes;
    }

    public String getStartRenderUID() {
        return this.startRenderUID;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public int getDocumentId() {
        return this.documentId;
    }
}
