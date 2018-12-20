package com.example.zmx.facerecognitionattendancemanager;

public class History {

    private String stuName;

    private String rgstTime;


    public History(String stuName, String rgstTime) {
        this.stuName = stuName;
        this.rgstTime = rgstTime;
    }

    public String getStuName() {
        return stuName;
    }

    public String getRgstTime() {
        return rgstTime;
    }

}
