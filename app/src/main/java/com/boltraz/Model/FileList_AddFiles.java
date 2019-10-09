package com.boltraz.Model;

import android.net.Uri;

public class FileList_AddFiles {

    public String list_fileName;
    public Uri file_uri;

    public FileList_AddFiles() {
    }

    public FileList_AddFiles(String list_fileName, Uri file_uri) {
        this.list_fileName = list_fileName;
        this.file_uri = file_uri;
    }

    public String getList_fileName() {
        return list_fileName;
    }

    public void setList_fileName(String list_fileName) {
        this.list_fileName = list_fileName;
    }

    public Uri getFile_uri() {
        return file_uri;
    }

    public void setFile_uri(Uri file_uri) {
        this.file_uri = file_uri;
    }
}