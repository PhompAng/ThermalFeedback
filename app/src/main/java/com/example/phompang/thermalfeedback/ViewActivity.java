package com.example.phompang.thermalfeedback;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

public class ViewActivity extends AppCompatActivity implements SummaryFragment.OnFragmentInteractionListener, TabLayout.OnTabSelectedListener {

    private static final String TAG = ViewActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private FloatingActionsMenu floatingActionsMenu;
    private String uid;
    private int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_experiment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent() != null) {
            uid = getIntent().getStringExtra("uid");
        }


        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        floatingActionsMenu.setVisibility(View.GONE);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Real"));
        tabLayout.addTab(tabLayout.newTab().setText("Simulated"));

        tabLayout.addOnTabSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, SummaryFragment.newInstance(uid, 0), "summary").commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.experiment, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("uid", uid);
        outState.putInt("day", day);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        uid = savedInstanceState.getString("uid");
        day = savedInstanceState.getInt("day");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Fragment summary = getSupportFragmentManager().findFragmentByTag("summary");
        if (summary != null && summary.isVisible()) {
            ((SummaryFragment) summary).setNotiType(tab.getPosition());
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
