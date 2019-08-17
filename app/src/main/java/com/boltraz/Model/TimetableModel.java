package com.boltraz.Model;

public class TimetableModel {

    private String title;
    private String ccode;
    private String startTime;
    private String endTime;
    private String prof;

    public TimetableModel() {
    }

    public TimetableModel(String title, String ccode, String startTime, String endTime, String prof) {
        this.title = title;
        this.ccode = ccode;
        this.startTime = startTime;
        this.endTime = endTime;
        this.prof = prof;
    }

    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCcode() {
        return ccode;
    }

    public void setCcode(String ccode) {
        this.ccode = ccode;
    }

    public String getstartTime() {
        return startTime;
    }

    public void setStartsAt(String startsAt) {
        this.startTime = startTime;
    }

    public String getendTime() {
        return endTime;
    }

    public void setendTime(String endTime) {
        this.endTime = endTime;
    }
}