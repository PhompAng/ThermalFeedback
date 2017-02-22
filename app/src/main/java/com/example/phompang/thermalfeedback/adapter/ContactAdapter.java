package com.example.phompang.thermalfeedback.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.phompang.thermalfeedback.R;
import com.example.phompang.thermalfeedback.model.Contact;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by phompang on 12/29/2016 AD.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Context context;
    private List<Contact> contacts;
    private ViewHolder.OnContactDeleteListener listener;

    public ContactAdapter(Context context, List<Contact> contacts, ViewHolder.OnContactDeleteListener listener) {
        this.context = context;
        this.contacts = contacts;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_contact, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.name.setText(contact.getName());
        holder.phone.setText(contact.getPhone());
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.img)
        CircularImageView img;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.phone)
        TextView phone;
        @BindView(R.id.delete)
        ImageView delete;

        OnContactDeleteListener listener;

        ViewHolder(View itemView, OnContactDeleteListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.listener = listener;
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onContactDelete(getAdapterPosition());
            }
        }

        public interface OnContactDeleteListener {
            void onContactDelete(int position);
        }
    }
}
