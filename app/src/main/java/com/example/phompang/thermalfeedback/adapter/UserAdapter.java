package com.example.phompang.thermalfeedback.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.phompang.thermalfeedback.R;
import com.example.phompang.thermalfeedback.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by phompang on 12/29/2016 AD.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> users;
    private ViewHolder.OnUserClickListener listener;

    public UserAdapter(Context context, List<User> users, ViewHolder.OnUserClickListener listener) {
        this.context = context;
        this.users = users;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_contact, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User u = users.get(position);
        holder.name.setText(u.getName() + " " + u.getSurname());
        holder.phone.setText(u.getUid());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.phone)
        TextView phone;
        @BindView(R.id.delete)
        ImageButton delete;

        OnUserClickListener listener;
        ViewHolder(View itemView, OnUserClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            this.listener = listener;
            delete.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                switch (v.getId()) {
                    case R.id.delete:
                        listener.onDelete(getAdapterPosition());
                        break;
                    default:
                        listener.onView(getAdapterPosition());
                        break;
                }
            }
        }

        public interface OnUserClickListener {
            void onDelete(int position);
            void onView(int position);
        }
    }
}
