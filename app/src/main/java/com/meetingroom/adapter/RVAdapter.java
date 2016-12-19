package com.meetingroom.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meetingroom.MeetingDescActivity;
import com.meetingroom.variables.MeetingRow;
import com.meetingroom.R;

import java.util.ArrayList;

/**
 * Created by Ксю on 14.12.2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MeetingViewHolder>
{


    ArrayList<MeetingRow> meetings = new ArrayList<>();
    Context context;

    public RVAdapter(ArrayList<MeetingRow> meetings, Context context)
    {
        this.meetings = meetings;
        this.context = context;
    }

    @Override
    public MeetingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View mVewCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_row, parent, false);
        MeetingViewHolder mMeetingViewHolder = new MeetingViewHolder(mVewCard);

        return mMeetingViewHolder;
    }

    @Override
    public void onBindViewHolder(MeetingViewHolder holder, final int position) {
        holder.mTitle.setText(meetings.get(position).getTitle());
        holder.mDesc.setText(meetings.get(position).getDesc());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MeetingDescActivity.KEY = meetings.get(position).getKey();

                Intent intent= new Intent(context, MeetingDescActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return meetings.size();
    }
    public  class MeetingViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mDesc;
        MeetingViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView)itemView.findViewById(R.id.meeting_title);
            mDesc = (TextView)itemView.findViewById(R.id.meeting_desc);
        }
    }

}
