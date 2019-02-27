package com.boltraz.ListAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boltraz.Model.ClassAnnouncementsModel;
import com.boltraz.Model.TimetableModel;
import com.boltraz.R;

import org.w3c.dom.Text;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TimetableListAdapter extends RecyclerView.Adapter<TimetableListAdapter.ViewHolder> {
    public List<TimetableModel> timetableList;

    public TimetableListAdapter(List<TimetableModel> timetableList) {
        this.timetableList = timetableList;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timetable_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.title.setText(timetableList.get(position).getTitle());
        holder.startsAt.setText(timetableList.get(position).getStartsAt());
        holder.endsAt.setText(timetableList.get(position).getEndsAt());
        holder.prof.setText("Prof. "+ timetableList.get(position).getProf());
        



    }


    @Override
    public int getItemCount() {
        return timetableList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView title;
        public TextView startsAt;
        public TextView endsAt;
        public TextView prof;
        public TextView hour;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            title = (TextView) mView.findViewById(R.id.subjectName_txt);
            startsAt = (TextView) mView.findViewById(R.id.startsAt_txt);
            endsAt = (TextView) mView.findViewById(R.id.endsAt_txt);
            prof = (TextView) mView.findViewById(R.id.profName_txt);
            hour = (TextView) mView.findViewById(R.id.hour_txt);


        }
    }


}
