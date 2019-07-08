package com.boltraz.Model;

public class TimetableModel {

    private String title;
    private String ccode;
    private String startsAt;
    private String endsAt;
    private String prof;

    public TimetableModel() {
    }

    public TimetableModel(String title, String ccode, String startsAt, String endsAt, String prof) {
        this.title = title;
        this.ccode = ccode;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
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

    public String getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(String startsAt) {
        this.startsAt = startsAt;
    }

    public String getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(String endsAt) {
        this.endsAt = endsAt;
    }
}