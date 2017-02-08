package com.example.phompang.thermalfeedback;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.phompang.thermalfeedback.adapter.ContactAdapter;
import com.example.phompang.thermalfeedback.app.FirebaseUtils;
import com.example.phompang.thermalfeedback.model.Contact;
import com.getbase.floatingactionbutton.FloatingActionButton;
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
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "uid";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String uid;
    private String mParam2;


    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uid Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String uid, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, uid);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    private Unbinder unbinder;
    private List<Contact> contacts;
    private ContactAdapter adapter;
    private DatabaseReference reference;
    @BindView(R.id.list)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact, container, false);
        unbinder = ButterKnife.bind(this, v);
        ((ExperimentActivity) getActivity()).showFab();

        contacts = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new ContactAdapter(getContext(), contacts);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference();

        retrieveContacts();

        FloatingActionButton addContact = (FloatingActionButton) getActivity().findViewById(R.id.add_contact);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
        FloatingActionButton importContact = (FloatingActionButton) getActivity().findViewById(R.id.import_contact);
        importContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "import", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    private Query contactRef;
    private ValueEventListener listener;

    private void retrieveContacts() {
        contactRef = reference.child(uid).child("contacts");
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contacts.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Contact contact = snapshot.getValue(Contact.class);
                    contacts.add(contact);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        contactRef.addValueEventListener(listener);
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_contact, null);
        builder.setTitle("Add Contact")
                .setView(dialogView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = ((EditText) dialogView.findViewById(R.id.name)).getText().toString();
                        String phone = ((EditText) dialogView.findViewById(R.id.phone)).getText().toString();
                        //TODO Validate

                        Contact contact = new Contact();
                        contact.setName(name);
                        contact.setPhone(phone);
                        FirebaseUtils.addContact(uid, contact);
                        Toast.makeText(getContext(), "Add Contact successful", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        builder.show();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_play).setVisible(false);
        menu.findItem(R.id.action_stop).setVisible(false);
        menu.findItem(R.id.action_bluetooth).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        contactRef.removeEventListener(listener);
        ((ExperimentActivity) getActivity()).hideFab();
    }
}
