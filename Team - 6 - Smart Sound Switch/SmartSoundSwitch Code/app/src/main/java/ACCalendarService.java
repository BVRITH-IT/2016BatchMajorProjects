package se.astacus.smartsoundswitch_raju;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


import static android.view.accessibility.AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;


public class ACCalendarService extends AccessibilityService
{
    private long mEventTime;

    public ACCalendarService(Context pContext) {
        super();

        Log.e("ACCalendarService", "ACCalendarService");
    }

    public ACCalendarService() {
        super();
    }

    void init(ACCalendarService event) {
        mEventTime = event.mEventTime;
    }

    public long getEventTime() {
        return mEventTime;
    }

    protected ACCalendarService(Parcel in) {
        Log.e("ACCalendarService", "Parcel");

        mEventTime = in.readLong();
        Log.e("writeToParcel", String.valueOf(mEventTime));

    }

    public void setEventTime(long eventTime) {
        //enforceNotSealed();
        mEventTime = eventTime;
    }

    /*public static final Creator<ACCalendarService> CREATOR = new Creator<ACCalendarService>()
    {

        @Override
        public ACCalendarService createFromParcel(Parcel in)
        {
            Log.e("ACCalendarService", "createFromParcel");

            return new ACCalendarService(in);
        }

        @Override
        public ACCalendarService[] newArray(int size)
        {
            Log.e("ACCalendarService", "newArray");
            return new ACCalendarService[size];
        }
    };*/

    @Override
    protected void onServiceConnected()
    {
        super.onServiceConnected();
        Log.e("ACCalendarService", "Service Connected");

        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);  //uncomment on 20190731
        startActivity(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        Log.e("ACCalendarService", "onAccessibilityEvent 2");

        if (event.getEventType() == TYPE_NOTIFICATION_STATE_CHANGED)//Done by asha
        {
            if (event.getPackageName().toString().equals("com.samsung.android.calendar"))
            {
                String pText = (String) event.getText().get(0);
                Parcelable pParcelable = event.getParcelableData();

                try {
                    if (event.getParcelableData() != null)
                    {
                        Location pLocation = getLocationFromAddress(pText);

                        float pDistanceToAddress = i.helper.getDistanceInKMsBetweenTwoLocations(i.helper.currentLocation, pLocation);

                        android.support.v7.app.AlertDialog.Builder pAlert2 = new android.support.v7.app.AlertDialog.Builder(this);
                        pAlert2.setTitle("Distance of Destination Location");
                        pAlert2.setMessage("The shortest distance is " + pDistanceToAddress + " from Current location");

                        pAlert2.setPositiveButton("OK", null);

                        pAlert2.show();
                    }

                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                //Date dateTime = new Date(event.getEventTime());

                //Log.e("dateTime",dateTime.toString());

                dateConvertDouble(event);

                Long tag_callback_time = System.currentTimeMillis();
            }
        }


        //Notification notification = (Notification) event.getParcelableData();

        /*DateTime start = event.getEventTime() getStart().getDateTime();

        if (start == null)
        {
            // All-day events don't have start times, so just use
            // the start date.
            start = event.getStart().getDate();
        }

        eventStrings.add(String.format("%s (%s) location: %s", event.getSummary(), start, event.getLocation()));*/
        Log.e("ACCalendarService", "onAccessibilityEvent 3");

    }

    @Override
    public void onInterrupt() {

    }

    public void dateConvertDouble(AccessibilityEvent event)
    {
        Date todate = new Date(event.getEventTime());
        long myLong = todate.getTime();
        //Log.e("dateTime",myLong.toString());

        String pString = DateFormat.getDateInstance().format(todate);
        Log.e("dateTime", pString);

        /*double myDouble = (double)myLong;
        System.out.println(myDouble);*/

        /*String myDateStr = new SimpleDateFormat("dd-MM-yyyy").format(myLong);
        Log.e("dateTime", myDateStr);*/

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(event.getEventTime()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        //Here you say to java the initial timezone. This is the secret
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        //Log.e("dateTime1", myDateStr);
        //Will print in UTC
        System.out.println(sdf.format(calendar.getTime()));

        //Here you set to your timezone
        sdf.setTimeZone(TimeZone.getDefault());
        //Will print on your default Timezone
        System.out.println(sdf.format(calendar.getTime()));
    }

    public Location getLocationFromAddress(String pAddress) throws IOException
    {
        String[] splitArray = pAddress.split("-");
        String address = splitArray[1];
        Log.e("address", address);

        /*if (pAddress.equals("Calendars synced.") || pAddress.equals("Cancel synced."))
        {
            Log.e("ACCalenderService", "getLocationFromAddress");
            return;
        }*/

        Location pLocation = null;
        Geocoder pGeocoder = new Geocoder(this);

        if (pGeocoder.isPresent())
        {
            if (address != "")
            {
               /*List<Address> list = gc.getFromLocationName(pAddress, 1);
               Address address1 = list.get(0);
               double lat = address1.getLatitude();
               double lng = address1.getLongitude();*/

                List<Address> list = pGeocoder.getFromLocationName(address, 1);
                Address address1 = list.get(0);
                double lat = address1.getLatitude();
                double lng = address1.getLongitude();

                Log.e("lat", String.valueOf(lat));
                Log.e("lng", String.valueOf(lng));

                pLocation = new Location("destination");
                pLocation.setLatitude(lat);
                pLocation.setLongitude(lng);
            }
        }

        return (pLocation);
    }
}
