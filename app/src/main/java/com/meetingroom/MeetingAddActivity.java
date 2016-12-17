package com.meetingroom;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.meetingroom.adapter.GoogleCalendar;
import com.meetingroom.variables.MainVariables;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MeetingAddActivity extends AppCompatActivity {

    private Button mAddMeeting;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_add);

        mAddMeeting = (Button) findViewById(R.id.add_button);
        mEditTitle = (EditText) findViewById(R.id.add_title);
        mEditDesc = (EditText) findViewById(R.id.add_desc);
        mEditDateBegin = (EditText) findViewById(R.id.date_begin);
        mEditTimeBegin = (EditText) findViewById(R.id.time_begin);
        mEditDateEnd = (EditText) findViewById(R.id.date_end);
        mEditTimeEnd = (EditText) findViewById(R.id.time_end);
        mEditPriority = (EditText) findViewById(R.id.add_priority);


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

        mAddMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    //создаем ссылку на встречу
                    Firebase mChildRef = mRef.child("n_" + key);

                    //проверяем, чтобы все поля были не пустые
                    boolean isEnptyFlag = (mEditTitle.getText().toString().equals("") || mEditDesc.getText().toString().equals("")
                            || mEditDateBegin.getText().toString().equals("") || mEditTimeBegin.getText().toString().equals("")
                            || mEditDateEnd.getText().toString().equals("")  || mEditTimeEnd.getText().toString().equals("")
                            || mEditPriority.getText().toString().equals(""));
                    //правильность ввода даты
                    boolean parseDateFlag = (isValidDate(mEditDateBegin.getText().toString(), "dd.MM.yyyy") &&
                                                isValidDate(mEditDateEnd.getText().toString(), "dd.MM.yyyy"));
                    //правильность ввода времени
                    boolean parseTimeFlag = (isValidTime(mEditTimeBegin.getText().toString(), "HH:mm") &&
                            isValidTime(mEditTimeEnd.getText().toString(), "HH:mm"));

                    if (isEnptyFlag || !parseDateFlag || !parseTimeFlag)
                    {
                        throw new Exception();
                    }

                    //создаем новую встречу
                    //создаем новый ключ для текущей ссылки и добавляем значение

                    String beginDate = mEditDateBegin.getText().toString();
                    String beginTime = mEditTimeBegin.getText().toString();
                    String endDate = mEditDateEnd.getText().toString();
                    String endTime = mEditTimeEnd.getText().toString();
                    String title = mEditTitle.getText().toString();
                    String desc = mEditDesc.getText().toString();

                    Firebase mChildRefTitle = mChildRef.child("title");
                    mChildRefTitle.setValue(title);

                    Firebase mChildRefDesc = mChildRef.child("desc");
                    mChildRefDesc.setValue(desc);

                    Firebase mChildRefBegin = mChildRef.child("begin");
                    mChildRefBegin.setValue(beginDate+" "+beginTime);

                    Firebase mChildRefEnd = mChildRef.child("end");
                    mChildRefEnd.setValue(endDate + " "+ endTime);

                    Firebase mChildRefPrior = mChildRef.child("priority");
                    mChildRefPrior.setValue(mEditPriority.getText().toString());

                    Firebase mChildRefKey = mChildRef.child("key");
                    mChildRefKey.setValue(key);

                    getNotification("Добавлена новая встреча!", mEditTitle.getText().toString());

                    GoogleCalendar.init(MeetingAddActivity.this, beginDate, beginTime, endDate, endTime, title, desc);

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

    public  void getNotification(String title, String message)
    {
        mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setTicker(title)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL);

        Intent resultIntent = new Intent(this, MeetingListActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MeetingListActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());

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
}