package com.example.phompang.thermalfeedback;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.phompang.thermalfeedback.app.FirebaseUtils;
import com.example.phompang.thermalfeedback.model.User;
import com.example.phompang.thermalfeedback.view.ProfileInput;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_UID = "uid";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String uid;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uid Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String uid, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    private Unbinder unbinder;
    private DatabaseReference reference;
    @BindView(R.id.id)
    ProfileInput id;
    @BindView(R.id.session)
    ProfileInput session;
    @BindView(R.id.name)
    ProfileInput name;
    @BindView(R.id.surname)
    ProfileInput surname;
    @BindView(R.id.age)
    ProfileInput age;
    @BindView(R.id.gender)
    RadioGroup gender;
    @BindView(R.id.submit)
    Button submit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, v);

        Log.d("profile", uid);
        id.setEnabled(false);
        session.setEnabled(false);

        reference = FirebaseDatabase.getInstance().getReference();
        retrieveProfile();

        return v;
    }

    private User user;

    private void retrieveProfile() {
        reference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                setValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.submit)
    public void validate() {
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(age.getText().toString())) {
            age.setError(getString(R.string.required));
            focusView = age;
            cancel = true;
        }
        if (TextUtils.isEmpty(surname.getText().toString())) {
            surname.setError(getString(R.string.required));
            focusView = surname;
            cancel = true;
        }
        if (TextUtils.isEmpty(name.getText().toString())) {
            name.setError(getString(R.string.required));
            focusView = name;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            updateUser();
        }
    }

    private void updateUser() {
        user.setName(name.getText().toString());
        user.setSurname(surname.getText().toString());
        user.setAge(Integer.parseInt(age.getText().toString()));
        user.setGender((String) gender.findViewById(gender.getCheckedRadioButtonId()).getTag());

        FirebaseUtils.updateUser(uid, user);
        getFragmentManager().popBackStack();
    }

    private void setValue() {
        id.setText(user.getUid());
        session.setText(Integer.toString(user.getNumberOfSession()));
        name.setText(user.getName());
        surname.setText(user.getSurname());
        age.setText(Integer.toString(user.getAge()));
        ((RadioButton) gender.findViewWithTag(user.getGender())).setChecked(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_play).setVisible(false);
        menu.findItem(R.id.action_stop).setVisible(false);
        menu.findItem(R.id.action_bluetooth).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
