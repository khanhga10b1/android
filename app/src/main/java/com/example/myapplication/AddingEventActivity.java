package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;


import android.os.Bundle;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.ImageAdapter;
import com.example.myapplication.adapter.SpinnerAdapter;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.DatabaseProcess;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class AddingEventActivity extends AppCompatActivity {


    private DatabaseProcess databaseProcess;
    private EditText edtName;
    private TextView textDate;
    private ToggleButton toggleButton;
    private TextView textCategory;
    private Spinner spinnerCategory;
    private ImageView imageBackground;
    private Context context;
    private int currentImage;
    private RelativeLayout categoryContainer;
    private LinearLayout mainContainer;
    private RelativeLayout toggleHolder;
    private RelativeLayout backgroundHolder;
    private boolean isFromIntent = false;
    private Intent intent;
    public  AddingEventActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        context = getApplication().getApplicationContext();
        getSupportActionBar().setTitle("Add event");
        edtName =  findViewById(R.id.add_name);
        textDate =  findViewById(R.id.add_date);
        textCategory = findViewById(R.id.add_text_category);
        spinnerCategory =  findViewById(R.id.add_spinner_category);
        toggleButton =  findViewById(R.id.add_toggle);
        imageBackground =  findViewById(R.id.add_background);
        categoryContainer = findViewById(R.id.add_category_container);
        mainContainer =  findViewById(R.id.add_main_container);
        toggleHolder = findViewById(R.id.add_toggle_holder);
        backgroundHolder = findViewById(R.id.add_background_holder);
        databaseProcess = new DatabaseProcess(MainActivity.context);
        Cursor cur = databaseProcess.query("select * from kind order by kind_id");
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(MainActivity.context, cur);
        spinnerCategory.setAdapter(spinnerAdapter);
        Random random = new Random();
        currentImage = random.nextInt(Constants.background.length);
        imageBackground.setImageResource(Constants.background[currentImage]);
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        textDate.setText(sdf.format(today));
        Rect displayRectangle = new Rect();
        Window window = AddingEventActivity.this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        intent = getIntent();
        if (intent.getStringExtra("name") != null) {
            isFromIntent = true;
            edtName.setText(intent.getStringExtra("name"));
            textDate.setText(intent.getStringExtra("date"));
            imageBackground.setImageResource(Constants.background[intent.getIntExtra("img", 0)]);
            spinnerCategory.setSelection(intent.getIntExtra("spinner", 0));
            if (intent.getIntExtra("loop", -1) == 1)
                toggleButton.setChecked(true);
        }
        categoryContainer.setBackgroundColor(ContextCompat.getColor(
                context, Constants.eventColor[spinnerCategory.getSelectedItemPosition()]));
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView
                    , int position, long id) {
                categoryContainer.setBackgroundColor(ContextCompat.getColor(
                        context, Constants.eventColor[spinnerCategory.getSelectedItemPosition()]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });



        textDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar mCurrentDate = Calendar.getInstance();
                int mYear = mCurrentDate.get(Calendar.YEAR);
                int mMonth = mCurrentDate.get(Calendar.MONTH);
                int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(AddingEventActivity.this
                        , new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedYear
                            , int selectedMonth, int selectedDay) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"
                                , Locale.getDefault());
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedYear, selectedMonth, selectedDay);
                        textDate.setText(sdf.format(newDate.getTime()));
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
        });

        backgroundHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupWindow mpopup;
                final View popUpView = getLayoutInflater()
                        .inflate(R.layout.add_background_layout, null, false);

                mpopup = new PopupWindow(popUpView
                        , (int) (displayRectangle.width() * 0.9f)
                        , (int) (displayRectangle.height() * 0.2f)
                        , true);

                mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
                mpopup.showAtLocation(popUpView, Gravity.CENTER, 0, 0);

                RecyclerView recyclerView =  popUpView.findViewById(
                        R.id.grid_view);
                ImageAdapter recyclerAdapter = new ImageAdapter();
                recyclerView.addItemDecoration(new
                        DividerItemDecoration(context, 0));
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                recyclerView.setAdapter(recyclerAdapter);
                // mainContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.dim_color));
                recyclerAdapter.setOnItemCLickListener(new ImageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        imageBackground.setImageResource(Constants.background[position]);
                        currentImage = position;
                        mainContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    }
                });
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_1:
                if (edtName.getText().toString().length() > 0
                        && textDate.getText().toString().length() > 0) {
                    int loop = 0;
                    if (toggleButton.isChecked()) {
                        loop = 1;
                    }
                    if (!isFromIntent) {
                        databaseProcess.insertEvent(edtName.getText().toString(),
                                spinnerCategory.getSelectedItemPosition() + 1,
                                textDate.getText().toString(),
                                loop,
                                currentImage);


                    } else {
                                 databaseProcess.modifyEvent(false
                                , intent.getIntExtra("id", -1)
                                , edtName.getText().toString()
                                , spinnerCategory.getSelectedItemPosition() + 1
                                , textDate.getText().toString()
                                , loop
                                , currentImage);

                    }
                    onBackPressed();
                }
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddingEventActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
