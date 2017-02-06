package com.example.phompang.thermalfeedback;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.phompang.thermalfeedback.adapter.SummaryAdapter;
import com.example.phompang.thermalfeedback.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SummaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "uid";
    private static final String ARG_PARAM2 = "notiType";

    private String uid;
    private int notiType;
    private int day;

    private OnFragmentInteractionListener mListener;

    public SummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SummaryFragment newInstance(String uid, int notiType) {
        SummaryFragment fragment = new SummaryFragment();
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
        setHasOptionsMenu(true);
        notifications = new ArrayList<>();
    }

    private Unbinder unbinder;
    private SummaryAdapter adapter;
    private List<Notification> notifications;
    private DatabaseReference reference;
    @BindView(R.id.day)
    Spinner spinner;
    @BindView(R.id.list)
    RecyclerView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_summary, container, false);
        unbinder = ButterKnife.bind(this, v);

        ((ExperimentActivity) getActivity()).showTab();

        reference = FirebaseDatabase.getInstance().getReference();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, Arrays.asList(new String[]{"Day 1", "Day 2", "Day 3"}));
        spinner.setAdapter(arrayAdapter);
        day = spinner.getSelectedItemPosition() + 1;
        Log.d("day", String.valueOf(day));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day = position + 1;
                retrieveNotifications();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        retrieveNotifications();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        adapter = new SummaryAdapter(getContext(), notifications);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);

        return v;
    }

    private Query notificationRef;
    private ValueEventListener listener;

    private void retrieveNotifications() {
        notificationRef = reference.child("notifications").child(uid).child(String.valueOf(day));
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notifications.clear();
                for (DataSnapshot noti: dataSnapshot.getChildren()) {
                    try {
                        Notification notification = noti.getValue(Notification.class);
                        switch (notiType) {
                            case 1:
                                if (notification.getIsReal()) {
                                    notifications.add(notification);
                                }
                                break;
                            case 2:
                                if (!notification.getIsReal()) {
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
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_stop).setVisible(false);
        menu.findItem(R.id.action_play).setVisible(false);
        super.onPrepareOptionsMenu(menu);
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
