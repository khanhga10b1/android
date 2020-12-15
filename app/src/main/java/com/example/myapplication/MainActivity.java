package com.example.myapplication;

import android.Manifest;
import android.accounts.AccountManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapter.RecyclerAdapter;
import com.example.myapplication.items.RecyclerViewClickListener;
import com.example.myapplication.items.SyncTask;
import com.example.myapplication.utils.DatabaseProcess;
import com.example.myapplication.utils.DividerItemDecoration;
import com.example.myapplication.entities.Events;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener
         {
    public static GoogleAccountCredential mCredential;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    private DatabaseProcess databaseProcess;

    private RecyclerView rvEvent;
    private List<Events> listViewItems;
    private Toolbar toolbar;
    private FloatingActionButton btnFab;
    private int currentVersionCode;
    private Drawer result;
    public static Context context;
    public static SharedPreferences sharedPreferences;
    public static RecyclerAdapter recyclerAdapter;

    private enum AppStart {
        FIRST_TIME, FIRST_TIME_VERSION, NORMAL
    }
    public static final String LAST_APP_VERSION = "last_app_version";
    public static final String IS_USE_SYNC = "is_use_sync";
    public static final String CAL_ID = "cal_id";
    public static final String DISPLAY_DAY = "display";



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
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        mCredential.setSelectedAccountName(getPreferences(Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null));
        result = new DrawerBuilder(this)
                .withRootView(R.id.drawer_container)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(false)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName("Sync")
                                .withDescription(mCredential.getSelectedAccountName())
                                .withIcon(GoogleMaterial.Icon.gmd_sync)
                                .withIdentifier(1),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem()
                                .withName("Display")
                                .withIcon(GoogleMaterial.Icon.gmd_perm_contact_calendar)
                                .withChecked(sharedPreferences.getBoolean(DISPLAY_DAY, true))
                                .withDescription("Day Count")
                                .withOnCheckedChangeListener(onCheckedChangeListener),
                        new DividerDrawerItem()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 1) {
                            getResultsFromApi();
                            return true;
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        if (savedInstanceState == null) {
            result.setSelection(21, false);
        }


        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddingEventActivity.class);
                startActivity(intent);
            }
        });

        databaseProcess = new DatabaseProcess(context);

        switch (checkAppStart()) {
            case NORMAL:
                Intent i = getIntent();
                if (i.getIntExtra("fromAddingEvent", 0) == 0) {

                    if (sharedPreferences.getBoolean(IS_USE_SYNC, true)) {
                        if (isDeviceOnline()) {
                            getResultsFromApi();
                            PrimaryDrawerItem a = ((PrimaryDrawerItem) result.getDrawerItem(1))
                                    .withDescription(mCredential.getSelectedAccountName());
                            result.updateItem(a);
                        }
                    }
                }
                break;
            case FIRST_TIME_VERSION:
                sharedPreferences.edit().putInt(LAST_APP_VERSION, currentVersionCode).apply();
                break;
            case FIRST_TIME:
                try {
                    databaseProcess.initializeFirstTime();
                    databaseProcess.addExample();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sharedPreferences.edit().putInt(LAST_APP_VERSION, currentVersionCode).apply();
                sharedPreferences.edit().putBoolean(IS_USE_SYNC, false).apply();
                sharedPreferences.edit().putBoolean(DISPLAY_DAY, true).apply();
                break;
            default:
                break;
        }

        listViewItems =databaseProcess.getAllEvent(-1);
        recyclerAdapter = new RecyclerAdapter(this, listViewItems, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvEvent.setLayoutManager(mLayoutManager);
        rvEvent.setItemAnimator(new DefaultItemAnimator());
        rvEvent.addItemDecoration(
                new DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.divider)));
        rvEvent.setAdapter(recyclerAdapter);
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                ((SwitchDrawerItem) drawerItem).withDescription("Day Count");
                sharedPreferences.edit().putBoolean(DISPLAY_DAY, true).apply();
            } else {
                ((SwitchDrawerItem) drawerItem).withDescription("Year, Month, Day");
                sharedPreferences.edit().putBoolean(DISPLAY_DAY, false).apply();
            }
            result.updateItem(drawerItem);
            recyclerAdapter.updateData(databaseProcess.getAllEvent(-1));
        }
    };

    @Override
    public void recyclerViewListClicked(int button, View v, int position) {
        if (button == 1) {
            Events listViewItem = listViewItems.get(position);
            Intent intent = new Intent(MainActivity.this, AddingEventActivity.class);
            intent.putExtra("id", listViewItem.getId());
            intent.putExtra("name", listViewItem.getName());
            intent.putExtra("loop", listViewItem.getLoop());
            intent.putExtra("spinner", listViewItem.getKind() - 1);
            intent.putExtra("date", listViewItem.getDate());
            intent.putExtra("img", listViewItem.getImg());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            databaseProcess.deleteWaitingEvent(listViewItems.get(position).getId());
            if (sharedPreferences.getBoolean(IS_USE_SYNC, false) && isDeviceOnline()) {
                new SyncTask(listViewItems.get(position).getIdSync()
                        , MainActivity.this).execute();
            }
            listViewItems = recyclerAdapter.removeAt(position);
        }
    }


    public AppStart checkAppStart() {
        PackageInfo pInfo;

        AppStart appStart = AppStart.NORMAL;
        try {
            int lastVersionCode = sharedPreferences.getInt(LAST_APP_VERSION, -1);
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersionCode = pInfo.versionCode;
            appStart = checkAppStart(currentVersionCode, lastVersionCode);

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "Unable to determine current app version from pacakge manager."
                    + " Defensively assuming normal app start.", Toast.LENGTH_SHORT).show();
        }
        return appStart;
    }

    public AppStart checkAppStart(int currentVersionCode, int lastVersionCode) {
        if (lastVersionCode == -1) {
            return AppStart.FIRST_TIME;
        } else if (lastVersionCode < currentVersionCode) {
            return AppStart.FIRST_TIME_VERSION;
        } else {
            return AppStart.NORMAL;
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
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
        //handle the back press
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(MainActivity.this,
                    "No network connection available.", Toast.LENGTH_LONG).show();
        } else {
            if (sharedPreferences.getBoolean(IS_USE_SYNC, false))
                sharedPreferences.edit().putBoolean(IS_USE_SYNC, true).apply();
            new SyncTask(MainActivity.this).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {

                    Toast.makeText(MainActivity.this,
                            "This app requires Google Play Services. Please install \" +\n" +
                                    " \"Google Play Services on your device and relaunch this app."
                            , Toast.LENGTH_LONG).show();

                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }


    public boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
}
