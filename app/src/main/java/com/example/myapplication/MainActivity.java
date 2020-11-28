package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapter.RecyclerAdapter;


import com.example.myapplication.entities.Events;
import com.example.myapplication.utils.DatabaseProcess;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import java.util.List;


public class MainActivity extends AppCompatActivity
         {


    private DatabaseProcess databaseProcess;
    private RecyclerView rvEvent;
    private List<Events> listViewItems;
    private Toolbar toolbar;
    private FloatingActionButton btnFab;
    private Drawer result;
    public static Context context;
    public static RecyclerAdapter recyclerAdapter;
    public static SharedPreferences sharedPreferences;
    private String ONETIME ="oneTime";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplication().getApplicationContext();
        rvEvent = findViewById(R.id.rv_event);
        toolbar = findViewById(R.id.toolbar);
        btnFab = findViewById(R.id.btn_fab);

        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("hi am khanh");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(ONETIME,true);
        result = new DrawerBuilder(this)
                .withRootView(R.id.drawer_container)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(false)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName("Option 1")
                                .withDescription("Hi Khanh")
                                .withIcon(GoogleMaterial.Icon.gmd_account_box)
                                .withIdentifier(1),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem()
                                .withName("Display")
                                .withIcon(GoogleMaterial.Icon.gmd_perm_contact_calendar)
                                .withDescription("Day Count")
                                .withOnCheckedChangeListener(onCheckedChangeListener),
                        new DividerDrawerItem()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();



        databaseProcess = new DatabaseProcess(context);
        if (sharedPreferences.getBoolean(ONETIME,true)){
            databaseProcess.initializeFirstTime();
            sharedPreferences.edit().putBoolean(ONETIME,false).apply();
        }
        listViewItems =databaseProcess.getAllEvent(-1);
        recyclerAdapter = new RecyclerAdapter(this, listViewItems);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvEvent.setLayoutManager(mLayoutManager);
        rvEvent.setAdapter(recyclerAdapter);


        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddingEventActivity.class);
                startActivity(intent);
            }
        });

    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                ((SwitchDrawerItem) drawerItem).withDescription("Day Count");
            } else {
                ((SwitchDrawerItem) drawerItem).withDescription("Year, Month, Day");
            }
            result.updateItem(drawerItem);
            recyclerAdapter.updateData(databaseProcess.getAllEvent(-1));
        }
    };







    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_main_0:
                recyclerAdapter.updateData(databaseProcess.getAllEvent(-1));
                return true;
            case R.id.menu_main_1:
                recyclerAdapter.updateData(databaseProcess.getAllEvent(1));
                return true;
            case R.id.menu_main_2:
                recyclerAdapter.updateData(databaseProcess.getAllEvent(2));
                return true;
            case R.id.menu_main_3:
                recyclerAdapter.updateData(databaseProcess.getAllEvent(3));
                return true;
            case R.id.menu_main_4:
                recyclerAdapter.updateData(databaseProcess.getAllEvent(4));
                return true;
            case R.id.menu_main_5:
                recyclerAdapter.updateData(databaseProcess.getAllEvent(5));
                return true;
            case R.id.menu_main_6:
                recyclerAdapter.updateData(databaseProcess.getAllEvent(6));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

}
