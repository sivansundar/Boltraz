package com.boltraz.Model;

public class NoteModel {

    public String fileName;
    public String fileID;
    public long fileSize;
    public String fileURL;

    public NoteModel() {
    }

    public NoteModel(String fileName, String fileID, long fileSize, String fileURL) {
        this.fileName = fileName;
        this.fileID = fileID;
        this.fileSize = fileSize;
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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }
}
