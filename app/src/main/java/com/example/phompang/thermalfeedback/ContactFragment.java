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
import com.example.phompang.thermalfeedback.model.Contact;
import com.getbase.floatingactionbutton.FloatingActionButton;

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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    private Unbinder unbinder;
    private List<Contact> contacts;
    private ContactAdapter adapter;
    @BindView(R.id.list)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact, container, false);
        unbinder = ButterKnife.bind(this, v);
        ((ExperimentActivity) getActivity()).showFab();

        makeList();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new ContactAdapter(getContext(), contacts);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

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

    private void makeList() {
        contacts = new ArrayList<>();
        Contact c1 = new Contact();
        c1.setName("John Doe");
        c1.setPhone("081-2345-678");
        Contact c2 = new Contact();
        c2.setName("AAA AAA");
        c2.setPhone("080-1234-567");
        contacts.add(c1);
        contacts.add(c2);
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
                        contacts.add(contact);
                        adapter.notifyItemInserted(contacts.size());
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
        ((ExperimentActivity) getActivity()).hideFab();
    }
}
