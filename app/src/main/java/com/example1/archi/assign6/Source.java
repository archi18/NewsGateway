package com.example1.archi.assign6;

import java.io.Serializable;

/**
 * Created by archi on 5/3/2017.
 */

public class Source implements Serializable{

    private String sourceID;
    private String sourceName;
    private String sourceURL;
    private String sourceCategory;

    public Source() {
    }

    public Source(String sourceID, String sourceName, String sourceURL, String sourceCategory) {
        this.sourceID = sourceID;
        this.sourceName = sourceName;
        this.sourceURL = sourceURL;
        this.sourceCategory = sourceCategory;
    }

    public String getSourceID() {
        return sourceID;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public String getSourceCategory() {
        return sourceCategory;
    }

    public void setSourceCategory(String sourceCategory) {
        this.sourceCategory = sourceCategory;
    }

    @Override
    public String toString(){
        return "ID "+sourceID+" Name "+sourceName+ " URL "+sourceURL+" category "+sourceCategory;
    }
}
