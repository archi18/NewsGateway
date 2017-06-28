package com.example1.archi.assign6;

import java.io.Serializable;

/**
 * Created by archi on 5/3/2017.
 */

public class Artical implements Serializable{
    private String articalAuthor;
    private String articalTitle;
    private String articalDescrption;
    private String articalURL;
    private String articalUrlToImag;
    private String articalPublishedAt;

    public Artical() {
    }

    public Artical(String articalAuthor, String articalTitle, String articalDescrption, String articalURL, String articalUrlToImag, String articalPublishedAt) {
        this.articalAuthor = articalAuthor;
        this.articalTitle = articalTitle;
        this.articalDescrption = articalDescrption;
        this.articalURL = articalURL;
        this.articalUrlToImag = articalUrlToImag;
        this.articalPublishedAt = articalPublishedAt;
    }

    public String getArticalAuthor() {
        return articalAuthor;
    }

    public void setArticalAuthor(String articalAuthor) {
        this.articalAuthor = articalAuthor;
    }

    public String getArticalTitle() {
        return articalTitle;
    }

    public void setArticalTitle(String articalTitle) {
        this.articalTitle = articalTitle;
    }

    public String getArticalDescrption() {
        return articalDescrption;
    }

    public void setArticalDescrption(String articalDescrption) {
        this.articalDescrption = articalDescrption;
    }

    public String getArticalURL() {
        return articalURL;
    }

    public void setArticalURL(String articalURL) {
        this.articalURL = articalURL;
    }

    public String getArticalUrlToImag() {
        return articalUrlToImag;
    }

    public void setArticalUrlToImag(String articalUrlToImag) {
        this.articalUrlToImag = articalUrlToImag;
    }

    public String getArticalPublishedAt() {
        return articalPublishedAt;
    }

    public void setArticalPublishedAt(String articalPublishedAt) {
        this.articalPublishedAt = articalPublishedAt;
    }

    public String toString(){
        return "articalTitle => "+articalTitle;
    }
}
