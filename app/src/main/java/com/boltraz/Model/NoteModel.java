package com.boltraz.Model;

public class NoteModel {

    public String fileName;
    public String fileID;
    public String fileURL;

    public NoteModel() {
    }

    public NoteModel(String fileName, String fileID, String fileURL) {
        this.fileName = fileName;
        this.fileID = fileID;
        this.fileURL = fileURL;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }



    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }
}
