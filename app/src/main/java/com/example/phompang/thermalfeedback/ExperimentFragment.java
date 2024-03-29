package com.example.phompang.thermalfeedback;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.phompang.thermalfeedback.adapter.NotificationAdapter;
import com.example.phompang.thermalfeedback.app.ServiceUtils;
import com.example.phompang.thermalfeedback.model.Notification;
import com.example.phompang.thermalfeedback.services.ServiceIO1;
import com.example.phompang.thermalfeedback.view.ClearDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
public class ExperimentFragment extends Fragment implements ClearDialog.OnDialogListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = ExperimentFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "uid";
    private static final String ARG_PARAM2 = "notiType";
    private static final String ARG_PARAM3 = "day";

    private String uid;
    private int notiType;
    private int day;
	private boolean isDialogShow = false;

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
    public static ExperimentFragment newInstance(String uid, int notiType, int day) {
        ExperimentFragment fragment = new ExperimentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, uid);
        args.putInt(ARG_PARAM2, notiType);
        args.putInt(ARG_PARAM3, day);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.uid = getArguments().getString(ARG_PARAM1);
            this.notiType = getArguments().getInt(ARG_PARAM2);
            this.day = getArguments().getInt(ARG_PARAM3);
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
//                reference.child("notifications").child(uid).getRef().push().setValue(0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        retrieveNotifications();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        adapter = new NotificationAdapter(getContext(), notifications);
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
                if (!isDialogShow && dataSnapshot.getChildrenCount() > 0) {
	                isDialogShow = true;
                    ClearDialog dialog = new ClearDialog.Builder().build();
                    dialog.show(getChildFragmentManager(), "");
                }
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
	    if (timer != null) {
		    timer.cancel();
	    }
    }

    private CountDownTimer timer;
    private ProgressDialog progressDialog;

    @Override
    public void onResume() {
        super.onResume();
        showProgressDialog();
        timer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                Intent intent = new Intent(getActivity(), ServiceIO1.class);
                intent.putExtra("uid", uid);
                intent.putExtra("day", day);
                Log.d("expFrag", "resume");
                if (!ServiceUtils.isRunning(getContext(), "com.example.phompang.thermalfeedback.services.ServiceIO1")) {
                    getActivity().startService(intent);
                }
                hideProgressDialog();
            }
        }.start();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading User Data");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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

    @Override
    public void onNegativeClick() {

    }

    @Override
    public void onPositiveClick() {
        reference.child("notifications").child(uid).child(String.valueOf(day)).setValue(null);
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
