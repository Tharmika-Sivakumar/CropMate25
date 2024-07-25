package com.example.cropmate25;

public class DataClassForum {
    private String dataTitle;
    private String dataDesc;
    private String dataName;
    private String dataImage;

    private String key;
    public String getKey() {
        return key; }

    public void setKey(String key) { this.key = key; }
    public String getDataTitle() {
        return dataTitle;
    }
    public String getDataDesc() {
        return dataDesc;
    }
    public String getDataName() {
        return dataName;
    }
    public String getDataImage() {
        return dataImage;
    }
    public DataClassForum(String dataTitle, String dataDesc,String dataName, String dataImage) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataName = dataName;
        this.dataImage = dataImage;
    }
    public DataClassForum() {

    }
}
