package com.example.phompang.thermalfeedback;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.phompang.thermalfeedback.adapter.NotificationAdapter;
import com.example.phompang.thermalfeedback.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExperimentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExperimentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExperimentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = ExperimentFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";

    private String uid;

    private OnFragmentInteractionListener mListener;

    public ExperimentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uid Parameter 1.
     * @return A new instance of fragment ExperimentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExperimentFragment newInstance(String uid) {
        ExperimentFragment fragment = new ExperimentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.uid = getArguments().getString(ARG_PARAM1);
        }
        notifications = new ArrayList<>();
    }

    private Unbinder unbinder;
    private NotificationAdapter adapter;
    private List<Notification> notifications;
    private DatabaseReference reference;
    @BindView(R.id.list)
    RecyclerView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_experiment, container, false);
        unbinder = ButterKnife.bind(this, v);

        Log.d("uid", uid);

        ((ExperimentActivity) getActivity()).showTab();

        reference = FirebaseDatabase.getInstance().getReference();
        retrieveNotifications();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new NotificationAdapter(getContext(), notifications);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);

        return v;
    }

    private DatabaseReference notificationRef;
    private ValueEventListener listener;

    private void retrieveNotifications() {
        notificationRef = reference.child("users").child(uid).child("notificationMap");
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notifications.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    notifications.add(snapshot.getValue(Notification.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        };
        notificationRef.addValueEventListener(listener);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        notificationRef.removeEventListener(listener);
        unbinder.unbind();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
