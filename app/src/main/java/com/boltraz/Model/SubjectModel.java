package com.boltraz.Model;

public class SubjectModel {

    private String title;
    private String description;
    private String ccode;
    private String prof;
    private String sub_id;
    private int credits;

    public SubjectModel() {
    }

    public SubjectModel(String title, String description, String ccode, String prof, String sub_id, int credits) {
        this.title = title;
        this.description = description;
        this.ccode = ccode;
        this.prof = prof;
        this.sub_id = sub_id;
        this.credits = credits;
    }

    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    public String getSub_id() {
        return sub_id;
    }

    public void setSub_id(String sub_id) {
        this.sub_id = sub_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCcode() {
        return ccode;
    }

    public void setCcode(String ccode) {
        this.ccode = ccode;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
