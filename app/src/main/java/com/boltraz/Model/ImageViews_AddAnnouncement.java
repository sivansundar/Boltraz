package com.boltraz.Model;

import android.net.Uri;

public class ImageViews_AddAnnouncement {

    private Uri imageButtonUri;

    public ImageViews_AddAnnouncement() {
    }

    public ImageViews_AddAnnouncement(Uri imageButtonUri) {
        this.imageButtonUri = imageButtonUri;
    }

    public Uri getImageButtonUri() {
        return imageButtonUri;
    }

    public void setImageButtonUri(Uri imageButtonUri) {
        this.imageButtonUri = imageButtonUri;
    }
}
