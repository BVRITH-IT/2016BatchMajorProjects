package se.astacus.smartsoundswitch_raju;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;
import java.util.List;

public class ACGoogleCalenderEvents extends Activity
{
    com.google.api.services.calendar.Calendar mService;
    GoogleAccountCredential credential;
    private TextView mStatusText;
    private TextView mResultsText;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    int MY_CAL_WRITE_REQ = 1003;
    int MY_CAL_READ_REQ = 1004;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };
    //private static final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

    public Intent mServiceIntent;
    public ACCalendarService mCalenderService;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e("ACTestCalenderEvents", "onCreate");

        super.onCreate(savedInstanceState);

        //temp();

        LinearLayout activityLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);

        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mStatusText = new TextView(this);
        mStatusText.setLayoutParams(tlp);
        mStatusText.setTypeface(null, Typeface.BOLD);
        mStatusText.setText("Retrieving data...");
        activityLayout.addView(mStatusText);

        mResultsText = new TextView(this);
        mResultsText.setLayoutParams(tlp);
        mResultsText.setPadding(16, 16, 16, 16);
        mResultsText.setVerticalScrollBarEnabled(true);
        mResultsText.setMovementMethod(new ScrollingMovementMethod());
        activityLayout.addView(mResultsText);

        setContentView(activityLayout);

        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff()).setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        mService = new com.google.api.services.calendar.Calendar.Builder(transport, jsonFactory, credential).setApplicationName("Google Calendar API Android Quickstart").build();

        mCalenderService = new ACCalendarService(this);  //service
        mServiceIntent = new Intent(ACGoogleCalenderEvents.this, mCalenderService.getClass());

        /*if (!(isMyServiceRunning(mSensorService.getClass())))
        {
            startService(mServiceIntent);
        }*/

        updateCalender();
        //getResults();
        //navigateToSettings();
    }

    private void updateCalender()
    {
        Log.e("ACTestCalenderEvents", "updateCalender");

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_CAL_WRITE_REQ);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, MY_CAL_READ_REQ);
        }*/

        ContentResolver cr = this.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        values.put(CalendarContract.Calendars.VISIBLE, 1);
        cr.update(ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, 0), values, null, null);
    }

    private void navigateToSettings()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Accessability Not Active");
        builder.setMessage("Please enable Accessability");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface,
                                        int i) {
                        // Show location settings when the user acknowledges
                        // the alert dialog
                        startActivityForResult(new Intent(
                                        Settings.ACTION_ACCESSIBILITY_SETTINGS),
                                101);

                    }
                });
        Dialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void getResults()
    {
        Log.e("ACTestCalenderEvents", "getResults");

        if (isGooglePlayServicesAvailable())
        {
            refreshResults();
        }
        else
        {
            //mStatusText.setText("Google Play Services required: " + "after installing, close and relaunch this app.");
            i.helper.singleButtonAlert(this, "alert", "No network connection available", this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.e("ACTestCalenderEvents", "onActivityResult");

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case REQUEST_GOOGLE_PLAY_SERVICES:

                if (resultCode == RESULT_OK)
                {
                    refreshResults();
                }
                else
                {
                    isGooglePlayServicesAvailable();
                }

                break;

            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null)
                {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                    if (accountName != null)
                    {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                        refreshResults();
                    }
                }
                else if (resultCode == RESULT_CANCELED)
                {
                    mStatusText.setText("Account unspecified.");
                }

                break;

            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK)
                {
                    refreshResults();
                }
                else
                {
                    chooseAccount();
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume()
    {
        Log.e("ACTestCalenderEvents", "onResume");

        super.onResume();

        if (isGooglePlayServicesAvailable())
        {
            refreshResults();
        }
        else
        {
            //mStatusText.setText("Google Play Services required: " + "after installing, close and relaunch this app.");
            i.helper.singleButtonAlert(this, "alert", "No network connection available", this);
        }
    }

    private boolean isGooglePlayServicesAvailable()
    {
        Log.e("ACTestCalenderEvents", "isGooglePlayServicesAvailable");

        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode))
        {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);

            return false;
        }
        else if (connectionStatusCode != ConnectionResult.SUCCESS )
        {
            return false;
        }

        return true;
    }

    public void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode)
    {
        Log.e("ACTestCalenderEvents", "showGooglePlayServicesAvailabilityErrorDialog");

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        ACGoogleCalenderEvents.this, REQUEST_GOOGLE_PLAY_SERVICES);

                dialog.show();
            }
        });
    }

    private void refreshResults()
    {
        Log.e("ACTestCalenderEvents", "refreshResults");

        if (credential.getSelectedAccountName() == null)
        {
            chooseAccount();
        }
        else
        {
            if (isDeviceOnline())
            {
                new ApiAsyncTask(this).execute();
            }
            else
            {
                //mStatusText.setText("No network connection available.");
                i.helper.singleButtonAlert(this, "alert", "No network connection available", this);
            }
        }
    }

    private void chooseAccount()
    {
        Log.e("ACTestCalenderEvents", "chooseAccount");

        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    private boolean isDeviceOnline()
    {
        Log.e("ACTestCalenderEvents", "isDeviceOnline");

        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    public void clearResultsText()
    {
        Log.e("ACTestCalenderEvents", "clearResultsText");

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mStatusText.setText("Retrieving data…");
                mResultsText.setText("");
            }
        });
    }

    public void updateStatus(final String message)
    {
        Log.e("ACTestCalenderEvents", "updateStatus");

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mStatusText.setText(message);
            }
        });
    }

    public void updateResultsText(final List<String> dataStrings) {
        Log.e("ACTestCalenderEvents", "updateResultsText");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dataStrings == null) {
                    mStatusText.setText("Error retrieving data!");
                } else if (dataStrings.size() == 0) {
                    mStatusText.setText("No data found.");
                } else {
                    mStatusText.setText("Data retrieved using" + " the Google Calendar API:");
                    mResultsText.setText(TextUtils.join("\n\n", dataStrings));
                }
            }
        });
    }
}
