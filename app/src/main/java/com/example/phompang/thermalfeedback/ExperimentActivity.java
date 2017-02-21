package com.example.phompang.thermalfeedback;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.phompang.thermalfeedback.ExperimentFragment.OnFragmentInteractionListener;
import com.example.phompang.thermalfeedback.services.Receiver.ReceiverManager;
import com.example.phompang.thermalfeedback.services.ServiceIO1;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ExperimentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnFragmentInteractionListener, SummaryFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, TabLayout.OnTabSelectedListener {

    private static final String TAG = ExperimentActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private FloatingActionsMenu floatingActionsMenu;
    private String uid;
    private int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent() != null) {
            uid = getIntent().getStringExtra("uid");
            day = getIntent().getIntExtra("day", 1);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        floatingActionsMenu.setVisibility(View.GONE);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Real"));
        tabLayout.addTab(tabLayout.newTab().setText("Simulated"));

        tabLayout.addOnTabSelectedListener(this);

        Intent intent = new Intent(getApplicationContext(), ServiceIO1.class);
        intent.putExtra("uid", uid);
        intent.putExtra("day", day);
        startService(intent);

        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, ExperimentFragment.newInstance(uid, 0, day), "exp").commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private MenuItem bluetoothMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.experiment, menu);
        bluetoothMenu = menu.findItem(R.id.action_bluetooth);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_play:
                boolean isPause = ReceiverManager.getInstance().isPause();
                if (isPause) {
                    ReceiverManager.getInstance().setPause(false);
                    item.setIcon(R.drawable.ic_pause_white_24dp);
                } else {
                    ReceiverManager.getInstance().setPause(true);
                    item.setIcon(R.drawable.ic_play_arrow_white_24dp);
                }
                return true;
            case R.id.action_stop:
                showAdminDialog();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void showAdminDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_signin, null);
        builder.setTitle("Admin Login")
                .setView(dialogView)
                .setPositiveButton("Log in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = ((EditText) dialogView.findViewById(R.id.username)).getText().toString();
                        String password = ((EditText) dialogView.findViewById(R.id.password)).getText().toString();
                        //TODO Validate
                        stopService(new Intent(getApplicationContext(), ServiceIO1.class));
                        dialog.dismiss();
                        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, SummaryFragment.newInstance(uid, 0), "summary").commit();
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(String state) {
        if (state.equals("CONNECTED")) {
            bluetoothMenu.setIcon(R.drawable.ic_bluetooth_white_24dp);
        } else {
            bluetoothMenu.setIcon(R.drawable.ic_bluetooth_disabled_white_24dp);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        stopService(new Intent(getApplicationContext(), ServiceIO1.class));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.action_home:
                fragmentTransaction.replace(R.id.flContent, ExperimentFragment.newInstance(uid, 0, day), "exp").commit();
                break;
            case R.id.action_profile:
                tabLayout.setVisibility(View.GONE);
                fragmentTransaction.replace(R.id.flContent, ProfileFragment.newInstance(uid, "test")).addToBackStack(null).commit();
                break;
            case R.id.action_contact:
                tabLayout.setVisibility(View.GONE);
                fragmentTransaction.replace(R.id.flContent, ContactFragment.newInstance(uid, "test")).addToBackStack(null).commit();
                break;
            case R.id.action_summary:
                showAdminDialog();
                break;
            case R.id.action_connect:
                startActivity(new Intent(ExperimentActivity.this, ConnectionActivity.class));
                break;
            case R.id.action_calibrate:
                startActivity(new Intent(ExperimentActivity.this, AdminActivity.class));
                break;
            case R.id.action_logout:
                //TODO logout
                stopService(new Intent(getApplicationContext(), ServiceIO1.class));
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showTab() {
        tabLayout.setVisibility(View.VISIBLE);
    }

    public void showFab() {
        floatingActionsMenu.setVisibility(View.VISIBLE);
    }

    public void hideFab() {
        floatingActionsMenu.setVisibility(View.GONE);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Fragment exp = getSupportFragmentManager().findFragmentByTag("exp");
        if (exp != null && exp.isVisible()) {
            ((ExperimentFragment) exp).setNotiType(tab.getPosition());
        }
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
