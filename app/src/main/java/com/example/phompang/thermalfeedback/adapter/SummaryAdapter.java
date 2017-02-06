package com.example.phompang.thermalfeedback.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.phompang.thermalfeedback.R;
import com.example.phompang.thermalfeedback.app.DateTimeUtils;
import com.example.phompang.thermalfeedback.model.Notification;
import com.example.phompang.thermalfeedback.view.NotificationDetail;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by phompang on 12/29/2016 AD.
 */

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.ViewHolder> {

    private Context context;
    private List<Notification> notifications;

    public SummaryAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_summary, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        if (notification.getType().equals("Incoming Call")) {
            //TODO show hidden view
        }

        holder.title.setText(notification.getType());
        holder.attemp.setText("Attemp " + notification.getAttempt());
        holder.stimuli.setText(stimuliToString(notification.getStimuli()));
        holder.isThermal.setText(Boolean.toString(notification.getIsThermal()));
        holder.isVibrate.setText(Boolean.toString(notification.getIsVibrate()));
        holder.startTime.setText(DateTimeUtils.toDateString(notification.getStartTime()));
        holder.responseTime.setText(DateTimeUtils.toDateString(notification.getResponseTime()));
    }

    private String stimuliToString(int stimuli) {
        switch (stimuli) {
            case 1:
                return "Very Hot";
            case 2:
                return "Hot";
            case 3:
                return "Cold";
            case 4:
                return "Very Cold";
            default:
                return "";
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.sub)
        TextView attemp;
        @BindView(R.id.contact)
        NotificationDetail contact;
        @BindView(R.id.stimuli)
        NotificationDetail stimuli;
        @BindView(R.id.is_contact)
        NotificationDetail isContact;
        @BindView(R.id.is_thermal)
        NotificationDetail isThermal;
        @BindView(R.id.is_vibrate)
        NotificationDetail isVibrate;
        @BindView(R.id.start_time)
        NotificationDetail startTime;
        @BindView(R.id.response_time)
        NotificationDetail responseTime;
        @BindView(R.id.end_time)
        NotificationDetail endTime;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
