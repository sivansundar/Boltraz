package com.boltraz.Model;

public class NoteModel {

    public String fileName;
    public String fileID;
    public String fileURL;
    public String fileSize;


    public NoteModel() {
    }

    public NoteModel(String fileName, String fileID, String fileURL, String fileSize) {
        this.fileName = fileName;
        this.fileID = fileID;
        this.fileURL = fileURL;
        this.fileSize = fileSize;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
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
