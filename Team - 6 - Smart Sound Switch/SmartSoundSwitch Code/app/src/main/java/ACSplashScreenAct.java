package se.astacus.smartsoundswitch_raju;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ACSplashScreenAct extends AppCompatActivity implements View.OnClickListener
{
    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;
    private final int MY_PERMISSIONS_ACCESS_FINE_LOCATION_CODE = 102;
    private final int MY_PERMISSIONS_REQUEST_READ_CALL_LOG = 103;
    private final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE= 104;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 112;
    private static final int MY_PERMISSIONS_REQUEST_Calender = 113;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        i.helper.hideTitleAndTitleBar(this);
        i.helper.changeStatusBarColor(this); /* Add this line for change status bar color i.e. top of the screen*/

        setContentView(R.layout.activity_acsplash_screen);

        requestPermissionToAccessContacts();

        TextView txt_tired = (TextView)findViewById(R.id.txt_tired);
        TextView txt_setup = (TextView)findViewById(R.id.txt_setup);

        String pText1 = "Tired of changing sound modes manually?";
        String pText2 = "Set up your preferences right now and relax!";

        txt_tired.setText(pText1);
        txt_setup.setText(pText2);

        Button btn_started = (Button)findViewById(R.id.btn_started);
        btn_started.setOnClickListener(this);

        Button btn_know = (Button)findViewById(R.id.btn_know);
        btn_know.setOnClickListener(this);
    }

    private void requestPermissionToAccessContacts()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else
        {
            requestPermissionToUseLocation();
        }
    }

    private void requestPermissionToUseLocation()
    {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION_CODE);
        }
        else
        {
            //requestPermissionToAccessCallLogs();
            requestPermissionToAccessCalls();
        }
    }

    private void requestPermissionToAccessCalls()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, this.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
        else
        {
            setPermissionsToCreateDirectories();
        }
    }

    private void setPermissionsToCreateDirectories()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, this.MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
        else
        {
            setPermisionsToCalender();
        }
    }

    private void setPermisionsToCalender()
    {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_CALENDAR, android.Manifest.permission.READ_CALENDAR}, this.MY_PERMISSIONS_REQUEST_Calender);
        }
        else
        {
            setPermissionsForEnabledNotifications();
        }
    }

    private void setPermissionsForEnabledNotifications()
    {
        if (!NotificationManagerCompat.getEnabledListenerPackages (getApplicationContext()).contains(getApplicationContext().getPackageName()))
        {
            Toast.makeText(this, "Please Enable Notification Access", Toast.LENGTH_LONG).show();
            //service is not enabled try to enabled by calling...
            getApplicationContext().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        else
        {
            //Your own logic
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                    /*ACFavourites pACFavourites = new ACFavourites(this);
                    i.helper.favouriteNumbersList = pACFavourites.getFavouriteContactNumbers();*/
                    requestPermissionToUseLocation();
                }
                break;

            case MY_PERMISSIONS_ACCESS_FINE_LOCATION_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                    requestPermissionToAccessCalls();
                }
                break;

            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: //MY_PERMISSIONS_REQUEST_READ_CALL_LOG:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                    setPermissionsToCreateDirectories();
                }
                break;

            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                    setPermisionsToCalender();
                }
                break;

            case MY_PERMISSIONS_REQUEST_Calender:
                setPermissionsForEnabledNotifications();
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.btn_know)
        {
            Intent pIntent = new Intent(getApplicationContext(), ACKnowMoreAct.class);
            startActivity(pIntent);
        }
        else if (id == R.id.btn_started)
        {
            Intent pIntent = new Intent(getApplicationContext(), ACSettingsAct.class);
            startActivity(pIntent);
        }
    }



    private void requestPermissionToAccessCallLogs() //Android 9.0
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CALL_LOG}, this.MY_PERMISSIONS_REQUEST_READ_CALL_LOG);
        }
        else
        {
            setPermissionsToCreateDirectories();
        }
    }
}
