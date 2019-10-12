package com.boltraz.ListAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.boltraz.Model.FileList_AddFiles;
import com.boltraz.R;

import java.util.ArrayList;

import static com.boltraz.Fragments.DashboardFragment.TAG;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {

    private ArrayList<FileList_AddFiles> mFileObject;

    public FilesAdapter(ArrayList<FileList_AddFiles> mFileObject) {
        this.mFileObject = mFileObject;
    }

    @NonNull
    @Override
    public FilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.add_announ_files_recycleview_item, parent, false);

        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FilesAdapter.ViewHolder holder, int position) {

        FileList_AddFiles fileObject = mFileObject.get(position);

        holder.setFileName(fileObject.getList_fileName());
    }

    @Override
    public int getItemCount() {

        Log.d(TAG, "getItemCount: inside adapter : " + mFileObject.size());

        return mFileObject.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setFileName(String fname) {

            TextView fileTextView = mView.findViewById(R.id.file_fileName);
            fileTextView.setText(fname);

        }
    }
}
