package com.boltraz.Model;

public class UserModel {

    public String name;
    public String email;
    public int semester;
    public String usn;
    public String classrep;
    public String classsection;

    public UserModel() {
    }

    public UserModel(String name, String email, int semester, String usn, String classrep, String classsection) {
        this.name = name;
        this.email = email;
        this.semester = semester;
        this.usn = usn;
        this.classrep = classrep;
        this.classsection = classsection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getUSN() {
        return usn;
    }

    public void setUSN(String usn) {
        this.usn = usn;
    }

    public String getClassrep() {
        return classrep;
    }

    public void setClassrep(String classrep) {
        this.classrep = classrep;
    }

    public String getClasssection() {
        return classsection;
    }

    public void setClasssection(String classsection) {
        this.classsection = classsection;
    }
}
