package com.example.phompang.thermalfeedback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.example.phompang.thermalfeedback.app.FirebaseUtils;
import com.example.phompang.thermalfeedback.model.User;
import com.example.phompang.thermalfeedback.view.ProfileInput;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddUserActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.img)
    ImageView img;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.submit)
    public void submit() {
        //TODO validate
        addUser();
    }

    public void addUser() {
        //TODO img
        User u = new User();
        u.setUid(id.getText().toString());
        u.setNumberOfSession(Integer.parseInt(session.getText().toString()));
        u.setName(name.getText().toString());
        u.setSurname(surname.getText().toString());
        u.setAge(Integer.parseInt(age.getText().toString()));
        u.setGender((String) gender.findViewById(gender.getCheckedRadioButtonId()).getTag());

        FirebaseUtils.addUser(u);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
