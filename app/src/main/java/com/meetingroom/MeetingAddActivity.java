package com.meetingroom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.meetingroom.services.MeetingAddService;
import com.meetingroom.variables.MainVariables;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MeetingAddActivity extends AppCompatActivity {

    private Button mAddMeetingButton;
    private Firebase mRef;
    private String key = "";
    private NotificationCompat.Builder mBuilder;

    private EditText mEditTitle;
    private EditText mEditDesc;
    private EditText mEditDateBegin;
    private EditText mEditTimeBegin;
    private EditText mEditDateEnd;
    private EditText mEditTimeEnd;
    private EditText mEditPriority;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    private String title = "";
    private String desc = "";
    private String beginDate = "";
    private String beginTime = "";
    private String endDate = "";
    private String endTime = "";
    private String priority = "";

    private boolean isEmpty = false;
    private boolean parseDate = true;
    private boolean parseTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_add);

        mAddMeetingButton = (Button) findViewById(R.id.add_button);
        mEditTitle = (EditText) findViewById(R.id.add_title);
        mEditDesc = (EditText) findViewById(R.id.add_desc);
        mEditDateBegin = (EditText) findViewById(R.id.date_begin);
        mEditTimeBegin = (EditText) findViewById(R.id.time_begin);
        mEditDateEnd = (EditText) findViewById(R.id.date_end);
        mEditTimeEnd = (EditText) findViewById(R.id.time_end);
        mEditPriority = (EditText) findViewById(R.id.add_priority);


        MeetingAddBroadcastReceiver meetingBroadcast = new MeetingAddBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(
                MeetingAddService.ACTION_MYINTENTSERVICE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(meetingBroadcast, intentFilter);


         mEditDateBegin.setOnClickListener(new View.OnClickListener()
         {
             @Override
             public void onClick(View view)
             {
                 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                 imm.hideSoftInputFromWindow(mEditDateBegin.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                 onClickDatePicker(mEditDateBegin);
             }

         });

        mEditDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditDateEnd.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                onClickDatePicker(mEditDateEnd);
            }
        });

        mEditTimeBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditTimeBegin.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                onClickTimePicker(mEditTimeBegin);
            }
        });

        mEditTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditTimeEnd.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                onClickTimePicker(mEditTimeEnd);
            }
        });

        //получаем уникальный key отдельной встречи (реалиовано рандомом)
        key = MainVariables.getKey();
        mRef = new Firebase("https://meeting-room-3a41e.firebaseio.com/Meetings/");

        mAddMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    title = mEditTitle.getText().toString();
                    desc = mEditDesc.getText().toString();
                    beginDate = mEditDateBegin.getText().toString();
                    beginTime = mEditTimeBegin.getText().toString();
                    endDate = mEditDateEnd.getText().toString();
                    endTime = mEditTimeEnd.getText().toString();
                    priority = mEditPriority.getText().toString();

                    isEmpty = (title.equals("") || desc.equals("") || beginTime.equals("") || beginDate.equals("")
                        || endDate.equals("") || endTime.equals("") || priority.equals(""));

                    parseDate = (isValidDate(beginDate,"dd.MM.yyyy") && isValidDate(endDate, "dd.MM.yyyy"));

                    parseTime = (isValidTime(beginTime, "HH:mm") && isValidTime(endTime, "HH:mm"));

                    if(isEmpty || !parseDate || !parseTime)
                    {
                        throw  new Exception();
                    }

                    addMeeting();

                    Intent intent= new Intent(MeetingAddActivity.this, MeetingListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                catch (Exception e)
                {
                    //если что то заполнено не верно, выбрасываем ошибку и обрабатываем ее
                    Toast.makeText(MeetingAddActivity.this, "Проверьте правильность заполнения полей", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public class MeetingAddBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("NETWORK").equals("0"))
                Toast.makeText(context, "Network not found!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_meeting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_back_add) {
            startActivity(new Intent(MeetingAddActivity.this, MeetingListActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean isValidDate(String value, String datePattern) {

        //метод проверки правильнлсти ввода даты
        if (value == null || datePattern == null || datePattern.length() <= 0) {
            return false;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        formatter.setLenient(false);

        try {
            formatter.parse(value);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private boolean isValidTime(String value, String datePattern) {

        //метод проверки правильнлсти ввода времени
        if (value == null || datePattern == null || datePattern.length() <= 0) {
            return false;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        formatter.setLenient(false);

        try {
            formatter.parse(value);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }


    private void onClickDatePicker(final EditText editDate)
    {
        final Calendar calendar = Calendar.getInstance();

        LayoutInflater layoutInflater = LayoutInflater.from(MeetingAddActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.date_picker_activity, null);
        mDatePicker = (DatePicker) promptView.findViewById(R.id.dp);
        mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MeetingAddActivity.this);
        alertDialogBuilder.setTitle("Choose data");
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                editDate.setText(new StringBuilder()
                                        .append(mDatePicker.getDayOfMonth()).append(".")
                                        .append(mDatePicker.getMonth() + 1).append(".")
                                        .append(mDatePicker.getYear()));
                          //      summary = prefDate.getSummary();
                          //      dpf = false;
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void onClickTimePicker(final EditText editTime) {

        final Calendar calendar = Calendar.getInstance();

        LayoutInflater layoutInflater = LayoutInflater.from(MeetingAddActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.time_picker_activity, null);
        mTimePicker = (TimePicker) promptView.findViewById(R.id.tp);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MeetingAddActivity.this);
        alertDialogBuilder.setTitle("Choose data");
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                editTime.setText(new StringBuilder()
                                        .append(mTimePicker.getCurrentHour()).append(":")
                                        .append(mTimePicker.getCurrentMinute()));
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private  void addMeeting()
    {
        Intent intent = new Intent(MeetingAddActivity.this, MeetingAddService.class);
        intent.putExtra(MeetingAddService.TITLE, title);
        intent.putExtra(MeetingAddService.DESC, desc);
        intent.putExtra(MeetingAddService.BEGIN, beginDate + " " + beginTime);
        intent.putExtra(MeetingAddService.END, endDate + " " + endTime);
        intent.putExtra(MeetingAddService.PRIORITY, priority);
        startService(intent);

    }

}