package com.boltraz.ListAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.boltraz.ClassAnnouncement_ImageItem_FullscreenActivity;
import com.boltraz.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    public Context mContext;
    public SharedPreferences sharedPreferences;
    public String coe_image_url;
    FirebaseStorage mStorage;
    DatabaseReference databaseReference;
    private ArrayList<String> mOptionsList;
    private FirebaseDatabase mDatabase;


    /* public SettingsAdapter(ArrayList<String> mOptionsList, String coe_img_url) {
         this.mOptionsList = mOptionsList;
         coe_image_url = coe_img_url;
     }
 */
    public SettingsAdapter(ArrayList<String> mOptionsList) {
        this.mOptionsList = mOptionsList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.settings_dashboard_list_item, parent, false);

        mContext = parent.getContext();
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference();
        sharedPreferences = mContext.getSharedPreferences("sharedpref", Context.MODE_PRIVATE);

        return new ViewHolder(listItem);

    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setItemName(mOptionsList.get(position).toString());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.getAdapterPosition() == 0) {


                    Log.d("Settings Adapter : ", "onClick: coe url : " + coe_image_url);


                    String url = sharedPreferences.getString("coe_url", "XXX");

                    Intent intent = new Intent(mContext, ClassAnnouncement_ImageItem_FullscreenActivity.class);
                    intent.putExtra("coe_url", url);
                    mContext.startActivity(intent);
                }

                if (holder.getAdapterPosition() == 1) {
                    Log.d("Settings Adapter :", "onClick: Faculty List");


                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mOptionsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setItemName(String name) {
            TextView setting_title_item = mView.findViewById(R.id.setting_item_title);
            setting_title_item.setText(name);
        }

        public void calenderOfEvents() {

        }
    }
}
