package com.boltraz.Model;

public class UserModel {

    String Name;
    String Email;
    String ClassSection;
    int Semester;
    String UID;
    String USN;
    String token_id;
    String classrep;

    public UserModel() {
    }

    public UserModel(String name, String email, String classSection, int semester, String UID, String USN, String token_id, String classrep) {
        Name = name;
        Email = email;
        ClassSection = classSection;
        Semester = semester;
        this.UID = UID;
        this.USN = USN;
        this.token_id = token_id;
        this.classrep = classrep;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getClassSection() {
        return ClassSection;
    }

    public void setClassSection(String classSection) {
        ClassSection = classSection;
    }

    public int getSemester() {
        return Semester;
    }

    public void setSemester(int semester) {
        Semester = semester;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getClassrep() {
        return classrep;
    }

    public void setClassrep(String classrep) {
        this.classrep = classrep;
    }

    public String getUSN() {
        return USN;
    }

    public void setUSN(String USN) {
        this.USN = USN;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }
}
