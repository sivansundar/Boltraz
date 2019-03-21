package com.boltraz.ListAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.boltraz.ClassAnnouncementsActivity;
import com.boltraz.CreateUserActivity;
import com.boltraz.Model.ClassAnnouncementsModel;
import com.boltraz.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Text;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class ClassAnnouncementsListAdapter extends RecyclerView.Adapter<ClassAnnouncementsListAdapter.ViewHolder> {

    public List<ClassAnnouncementsModel> classAnnouncementsList;

    public Context context;

    public ClassAnnouncementsListAdapter(List<ClassAnnouncementsModel> classAnnouncementsList) {

        this.classAnnouncementsList = classAnnouncementsList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.classanoun_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.title_text.setText(classAnnouncementsList.get(i).getTitle());
        viewHolder.descriptio_text.setText(classAnnouncementsList.get(i).getDesc());

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ClassAnnouncementsActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classAnnouncementsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView title_text;
        public TextView descriptio_text;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            context = itemView.getContext();



            title_text = (TextView)mView.findViewById(R.id.title_txt_list);
            descriptio_text = (TextView)mView.findViewById(R.id.description_txt_list);
        }
    }


}
