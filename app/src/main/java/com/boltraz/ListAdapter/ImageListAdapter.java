package com.boltraz.ListAdapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.boltraz.Model.ImageViews_AddAnnouncement;
import com.boltraz.R;

import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    // private ArrayList<ImageViews_AddAnnouncement> arrayList;
    private ArrayList<ImageViews_AddAnnouncement> mCustomObjects;


    public ImageListAdapter(ArrayList<ImageViews_AddAnnouncement> arrayList) {
        mCustomObjects = arrayList;
    }

    @NonNull
    @Override
    public ImageListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.add_announ_image_recycleview_item, parent, false);


        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ImageViews_AddAnnouncement myListData = mCustomObjects.get(position);

        holder.setImage(myListData.getImageButtonUri());

    }


    @Override
    public int getItemCount() {
        return mCustomObjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public ImageButton imgButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setImage(Uri imgUri) {

            imgButton = mView.findViewById(R.id.imgbutton_view);

            imgButton.setImageURI(imgUri);

        }


    }

}
