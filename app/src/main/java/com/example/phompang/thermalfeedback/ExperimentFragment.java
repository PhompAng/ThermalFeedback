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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private static final String ARG_PARAM1 = "uid";
    private static final String ARG_PARAM2 = "notiType";

    private String uid;
    private int notiType;

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
    public static ExperimentFragment newInstance(String uid, int notiType) {
        ExperimentFragment fragment = new ExperimentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, uid);
        args.putInt(ARG_PARAM2, notiType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.uid = getArguments().getString(ARG_PARAM1);
            this.notiType = getArguments().getInt(ARG_PARAM2);
        }
        notifications = new ArrayList<>();
    }

    private Unbinder unbinder;
    private NotificationAdapter adapter;
    private List<Notification> notifications;
    private DatabaseReference reference;
    private long session;
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
        reference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                session = dataSnapshot.child("numberOfSession").getValue(Long.class);
                dataSnapshot.child("numberOfSession").getRef().setValue(session+1);
                reference.child("notifications").child(uid).getRef().push().setValue(0);
                retrieveNotifications();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new NotificationAdapter(getContext(), notifications);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);

        return v;
    }

    private Query notificationRef;
    private ValueEventListener listener;

    private void retrieveNotifications() {
        notificationRef = reference.child("notifications").child(uid).orderByKey().limitToLast(1);
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notifications.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    for (DataSnapshot noti: snapshot.getChildren()) {
                        try {
                            Notification notification = noti.getValue(Notification.class);
                            switch (notiType) {
                                case 1:
                                    if (notification.isReal()) {
                                        notifications.add(notification);
                                    }
                                    break;
                                case 2:
                                    if (!notification.isReal()) {
                                        notifications.add(notification);
                                    }
                                    break;
                                default:
                                    notifications.add(notification);
                                    break;
                            }
                        } catch (Exception e) {
                            return;
                        }
                    }
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
    public void onPause() {
        super.onPause();
        reference.child("notifications").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    try {
                        Map<String, Notification> value = (Map<String, Notification>) snapshot.getValue();
                    } catch (Exception e) {
                        snapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        notificationRef.removeEventListener(listener);
        unbinder.unbind();
    }

    public void setNotiType(int notiType) {
        this.notiType = notiType;
        retrieveNotifications();
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
