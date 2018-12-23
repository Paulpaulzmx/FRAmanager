package com.example.zmx.facerecognitionattendancemanager;

public class History {

    private String register_time;

    private String user_id;

    public History(String register_time, String stuName) {
        this.register_time = register_time;
        this.user_id = stuName;
    }

    public String getRegister_time() {
        return register_time;
    }

    public String getStuName() {
        return user_id;
    }

    public void setRegister_time(String register_time) {
        this.register_time = register_time;
    }

    public void setStuName(String stuName) {
        this.user_id = stuName;
    }

    @Override
    public String toString() {
        return "History{" +
                "register_time='" + register_time + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
