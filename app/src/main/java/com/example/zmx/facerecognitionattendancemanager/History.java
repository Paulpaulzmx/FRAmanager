package com.example.zmx.facerecognitionattendancemanager;

public class History {

    private String rgstTime;

    private String stuName;

    public History(String rgstTime, String stuName) {
        this.rgstTime = rgstTime;
        this.stuName = stuName;
    }

    public String getRgstTime() {
        return rgstTime;
    }

    public String getStuName() {
        return stuName;
    }

    public void setRgstTime(String rgstTime) {
        this.rgstTime = rgstTime;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }


}
