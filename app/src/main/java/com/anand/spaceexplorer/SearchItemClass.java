package com.anand.spaceexplorer;

public class SearchItemClass {

    private String nasaId;
    private String title;

    private String mediaType;
    private String href;

    public SearchItemClass(String nasaId,String title, String mediaType, String href){
        this.nasaId=nasaId;
        this.title=title;
        this.mediaType=mediaType;
        this.href=href;
    }

    public String getNasaId() {
        return nasaId;
    }

    public String getTitle() {
        return title;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getHref() {
        return href;
    }
}
