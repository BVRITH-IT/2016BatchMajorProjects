package se.astacus.smartsoundswitch_raju;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.provider.CalendarContract.Calendars;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private AudioManager amanager;
    //private EditText et_volume;

    private static final int READ_CALENDAR_EVENTS = 113;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        i.helper.changeStatusBarColor(MainActivity.this);

        getScreenResolution();
        //this.setPermissionsToCreateDirectories();

        //this.getCalendarEvents();

        Log.v("locationsListUser.size", String.valueOf(i.helper.locationsListUser.size()));

        //retriveSharedPreferencesData();

        //i.helper.allData.readPlist();

        addTempLocations();

        //i.helper.volumeBar = (SeekBar) findViewById(R.id.sb_volumebar);

        setHeader();
        //i.helper.updateCategories();

        //i.helper.updateLocationsListTemp(); //temp
        //i.helper.updateVolumeRanges();

        test();

        /*ACFavourites pACFavourites = new ACFavourites();
        i.helper.favouriteNumbersList = pACFavourites.getFavouriteContactNumbers(this, MainActivity.this);*/

        /*Log.e("Categories: ", i.helper.categoriesList.toString());
        Log.e("Locations user: ", i.helper.locationsListUser.toString());
        Log.e("FavouriteNumbersList: ", i.helper.favouriteNumbersList.toString());*/

        //testStreamVolume();
        //setRingVolume();

        //createTimerToUpdateRingerVolume();  //temp
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    private void getScreenResolution()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.e("Width", "" + width);  //1080
        Log.e("height", "" + height);  //1776
    }

    private void test()
    {
        Location currentLocation = new Location("Trigeo Technologies");
        currentLocation.setLatitude(17.7133);
        currentLocation.setLongitude(83.3151);

        Location pLocation1 = new Location("KGH");
        pLocation1.setLatitude(17.708967);
        pLocation1.setLongitude(83.306043);

        Location pLocation2 = new Location("MY Home");
        pLocation2.setLatitude(17.749811);
        pLocation2.setLongitude(83.262718);

        float pDistanceToAddress = i.helper.getDistanceInKMsBetweenTwoLocations(currentLocation, pLocation2);

        android.support.v7.app.AlertDialog.Builder pAlert2 = new android.support.v7.app.AlertDialog.Builder(this);
        pAlert2.setTitle("Distance of Destination Location");
        pAlert2.setMessage("The shortest distance is " + pDistanceToAddress + " from Current location");

        pAlert2.setPositiveButton("OK", null);

        pAlert2.show();
    }

    public void getCalendarEvents()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, this.READ_CALENDAR_EVENTS);
        }

        ContentResolver pContentResolver = getContentResolver();

        Cursor pCursor = pContentResolver.query(Uri.parse("content://com.android.calendar/events"),
                new String[]{ "calendar_id",
                        "title",
                        "description",
                        "dtstart",
                        "dtend",
                        "eventLocation" }, null, null, null);//Keys must give as it is. If we modify, we can't get events and app will get crash!!!

        int totalEvents = pCursor.getCount();

        pCursor.moveToFirst();

        for (int index = 0; index < totalEvents; index++)
        {
            ACEvent pACEvent = new ACEvent();
            pACEvent.idEvent = pCursor.getInt(0);
            pACEvent.titleEvent = pCursor.getString(1);
            pACEvent.descriptionEvent = pCursor.getString(2);
            pACEvent.dateStart = new Date(pCursor.getLong(3)).toString();
            pACEvent.dateEnd = new Date(pCursor.getLong(4)).toString();
            pACEvent.locationDescription = pCursor.getString(5);

            i.helper.eventsCalendarList.add(pACEvent);

            pCursor.moveToNext();
        }

        pCursor.close();

        Log.e("eventsCalendarList", i.helper.eventsCalendarList.toString());
    }

    public void createTimerToUpdateRingerVolume()
    {
        Timer timer = new Timer();

        final int FPS = 100;
        ACTimerTask pACTimerTask = new ACTimerTask(this);
        timer.scheduleAtFixedRate(pACTimerTask, 0, 1000/FPS);
    }

    public void setHeader()
    {
        //TextView txt_left = (TextView)findViewById(R.id.id_txtLeft);
        TextView txt_Header = (TextView)findViewById(R.id.id_txtHeading);
        TextView txt_right = (TextView)findViewById(R.id.id_txtRight);

        //txt_left.setText("Settings");
        txt_right.setText("Settings");
        txt_right.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        if (id == R.id.btn_hotel)
        {
            //To delete
            //createTimerToUpdateRingerVolume();

            Log.v("onClick", "btn_hotel");
            i.helper.currentLocation = new Location("Novotel");
            i.helper.currentLocation.setLatitude(17.7110);
            i.helper.currentLocation.setLongitude(83.3160);
        }
        else if (id == R.id.btn_office)
        {
            Log.v("onClick", "btn_office");
            i.helper.currentLocation = new Location("Trigeo Technologies");
            i.helper.currentLocation.setLatitude(17.7133);
            i.helper.currentLocation.setLongitude(83.3151);
        }
        else if (id == R.id.btn_temple)
        {
            Log.v("onClick", "btn_temple");
            i.helper.currentLocation = new Location("Kalimatha Temple");
            i.helper.currentLocation.setLatitude(17.712603);
            i.helper.currentLocation.setLongitude(83.318791);
        }
        else if (id == R.id.btn_hosp)
        {
            Log.v("onClick", "btn_hosp");
            i.helper.currentLocation = new Location("KGH");
            i.helper.currentLocation.setLatitude(17.708967);
            i.helper.currentLocation.setLongitude(83.306043);
        }
        /*else if (id == R.id.id_txtLeft)
        {
            Log.v("onClick", "navigation 1");

            Intent pIntent = new Intent(MainActivity.this, ACSettingsAct.class);
            startActivity(pIntent);
        }*/
        else if (id == R.id.id_txtRight)
        {
            Log.v("onClick", "navigation 2");

            Intent pIntent = new Intent(MainActivity.this, ACSettingsAct.class);
            startActivity(pIntent);
        }
        else if (id == R.id.btn_ring)  //2
        {
            int mode = AudioManager.RINGER_MODE_NORMAL;
            testRingerMode(mode);
        }
        else if (id == R.id.btn_vibration)  //1
        {
            int mode = AudioManager.RINGER_MODE_VIBRATE;
            testRingerMode(mode);
        }
        else if (id == R.id.btn_silent)  //0
        {
            int mode = AudioManager.RINGER_MODE_SILENT;
            testRingerMode(mode);
        }
        else if (id == R.id.btn_change)
        {
            EditText et_volume = (EditText)findViewById(R.id.et_volumeRange);
            String volume = et_volume.getText().toString();

            AudioManager audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (volume != null)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, Integer.parseInt(volume), AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
                }
            }

            et_volume.getText().clear();
        }
    }

    private void testRingerMode(int mode)
    {
        Log.e("ringerMode:", String.valueOf(mode));

        AudioManager audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            audioManager.setRingerMode(mode);
        }
    }

    private void testStreamVolume()
    {
        Log.e("testStreamVolume", "testStreamVolume");
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
    }

    public void addTempLocations()
    {
        Button btn_hotel = (Button)findViewById(R.id.btn_hotel);
        Button btn_office = (Button)findViewById(R.id.btn_office);
        Button btn_temple = (Button)findViewById(R.id.btn_temple);
        Button btn_hospital = (Button)findViewById(R.id.btn_hosp);

        btn_hotel.setOnClickListener(this);
        btn_office.setOnClickListener(this);
        btn_temple.setOnClickListener(this);
        btn_hospital.setOnClickListener(this);


        Button btn_ring = (Button)findViewById(R.id.btn_ring);
        Button btn_silent = (Button)findViewById(R.id.btn_silent);
        Button btn_vibration = (Button)findViewById(R.id.btn_vibration);


        Button btn_change = (Button)findViewById(R.id.btn_change);

        btn_ring.setOnClickListener(this);
        btn_silent.setOnClickListener(this);
        btn_vibration.setOnClickListener(this);
        btn_change.setOnClickListener(this);
    }
}
