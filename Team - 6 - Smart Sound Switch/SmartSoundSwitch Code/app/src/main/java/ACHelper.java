package se.astacus.smartsoundswitch_raju;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.LOCATION_SERVICE;

public class ACHelper
{
    private static ACHelper _instance = null;

    public static ACHelper Instance()
    {
        if (_instance == null)
        {
            _instance = new ACHelper();
        }

        return (_instance);
    }

    public Ringtone m_ringtone;
    public String appTitle = "Smart Sound Switch";
    public ArrayList<String> categoriesList = new ArrayList<String>();
    public ArrayList<ACLocationInfo> locationsListUser = new ArrayList<ACLocationInfo>();
    public ArrayList<String> favouriteNumbersList = new ArrayList<String>();
    public ArrayList<String> volumeRanges = new ArrayList<String>();
    public ArrayList<ACNumber> incomingCallsNumbersList = new ArrayList<ACNumber>();
    public double minDistanceToFind = 100;//Meters
    //public SeekBar volumeBar;
    public Location currentLocation = new Location("LocationOnMap");
    public Switch vibrationModeSwitch;
    public File pathFileLoginCredentialsPlist;
    public File pathProjectFiles;
    public ACReadAndWritePlist allData = new ACReadAndWritePlist();
    public ArrayList<ACEvent> eventsCalendarList = new ArrayList<ACEvent>();

    public String volume_mode_vibration = "Vib";
    public String volume_mode_silent = "Slnt";
    public int userDefaultVolume = 0;
    public int actualDeviceMode;

    public int editLocationIndex = 0;
    public boolean isDebug = false; //Un-comment while delivery
    //public  boolean isStartServiceAfterCloseApp = false;
    public PhonecallReceiver mPhonecallReceiver;
    public boolean isSensorServiceOn = false;

    public TextView locationName;
    //public String nameOfLocation = "";
    public String completeAddress = "";

    List<String> eventsList = null;

    public ACHelper()
    {
        File pExternalStorageDirectory = Environment.getExternalStorageDirectory();
        this.pathProjectFiles = new File(pExternalStorageDirectory + "/SmartSoundSwitch/ProjectFiles/");
        this.pathFileLoginCredentialsPlist = new File(this.pathProjectFiles +"/LocationsData.plist");

        createPathIfNotExist(this.pathProjectFiles);
    }

    public void hideKeyboard(Activity pActivity)
    {
        if (pActivity.getCurrentFocus() != null)
        {
            InputMethodManager pInputMethodManager = (InputMethodManager) pActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            pInputMethodManager.hideSoftInputFromWindow(pActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void createPathIfNotExist(File pFile)
    {
        if (!(pFile.exists()))
        {
            pFile.mkdirs();
        }
    }

    public String getLocationName(String completeAddress)
    {
        String name = "";

        if (completeAddress != null)
        {
            if (completeAddress.contains(","))
            {
                String[] address = completeAddress.split(",");
                name = address[1];
            }
            else
            {
                name = completeAddress;
            }
        }

        return (name);
    }

    public void showToast(Context context, String msg)
    {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void updateCategories()
    {
        if (i.helper.categoriesList.size() == 0)
        {
            this.categoriesList.add("Hospital");
            this.categoriesList.add("Temple");
            this.categoriesList.add("Office");
            this.categoriesList.add("Home");
            this.categoriesList.add("School");
            this.categoriesList.add("Hotel");
            this.categoriesList.add("Event");
            this.categoriesList.add("Meeting");
            this.categoriesList.add("Outing");
            this.categoriesList.add("Others");
        }
    }

    public void updateVolumeRanges()
    {
        if (i.helper.volumeRanges.size() == 0)
        {
            for (int i = 1; i <= 15; i++)
            {
                this.volumeRanges.add(String.valueOf(i));
            }
        }
    }

    public Location getCurrentLocation(Context pContext)
    {
        ACGPSTracker tracker = new ACGPSTracker(pContext);
        Location pLocation = tracker.getLocation();

        return (pLocation);
    }

    public ACLocationInfo getNearerLocationInfoFromUsersLocations(Location pCurrentLocation)
    {
        Log.e("pLocationInfo: ", "getNearerLocationInfoFromUsersLocations");

        Log.e("locationsListUser.size", String.valueOf(i.helper.locationsListUser.size()));

        for (ACLocationInfo pLocationInfo: i.helper.locationsListUser)
        {
            if ((pLocationInfo != null) && (pCurrentLocation != null))
            {
                float distance = this.getDistanceInMetersBetweenTwoLocations(pCurrentLocation, pLocationInfo.location);

                if (distance < 100)//100 meters
                {
                    return (pLocationInfo);
                }
            }
        }

        return (null);
    }

    public float getDistanceInMetersBetweenTwoLocations(Location fromLocation, Location toLocation)
    {
        if (toLocation != null)
        {
            return  (fromLocation.distanceTo(toLocation));
        }

        return (0);
    }

    public float getDistanceInKMsBetweenTwoLocations(Location fromLocation, Location toLocation)
    {
        return  (fromLocation.distanceTo(toLocation) / 1000);
    }

    public void updateiHelperCurrentLocation(Location pLocation)
    {
        if (pLocation != null)
        {
            i.helper.currentLocation.setLatitude(pLocation.getLatitude());
            i.helper.currentLocation.setLongitude(pLocation.getLongitude());
        }
    }

    public void hideTitleAndTitleBar(AppCompatActivity pActivity)
    {
        pActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        pActivity.getSupportActionBar().hide(); //hide the title bar
    }

    public void changeStatusBarColor(Activity pActivity)
    {
        Window pWindow = pActivity.getWindow();
        pWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        pWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        pWindow.setStatusBarColor(pActivity.getResources().getColor(R.color.statusBarColor));
    }

    public void updateRingerVolume(Context pContext, Boolean isNotificationPost)
    {
        if (i.helper.currentLocation == null)
        {
            Log.e("pLocationInfo: ", "currentLocation == null");
            Location pLocation = i.helper.locationsListUser.get(0).location;

            this.updateiHelperCurrentLocation(pLocation);
        }

        Location pCurrentLocation = null;

        if (i.helper.isDebug)
        {
            pCurrentLocation.setLatitude(i.helper.currentLocation.getLatitude());
            pCurrentLocation.setLongitude(i.helper.currentLocation.getLongitude());
        }
        else
        {
            pCurrentLocation = i.helper.getCurrentLocation(pContext);
        }

        ACLocationInfo pLocationInfo = i.helper.getNearerLocationInfoFromUsersLocations(pCurrentLocation);

        if (pLocationInfo != null)
        {
            Log.e("pLocationInfo: ", "pLocationInfo != null");
            //i.helper.volumeBar.setProgress(new Integer(pLocationInfo.volume));
            //i.helper.volumeBar.refreshDrawableState();

            Log.e("pLocationInfo.volume: ", pLocationInfo.volume);

            AudioManager pAudioManager = (AudioManager) pContext.getSystemService(pContext.AUDIO_SERVICE);

            if (pLocationInfo.volume.equalsIgnoreCase(i.helper.volume_mode_silent))
            {
                Log.e("pLocationInfoVolmslnt", i.helper.volume_mode_silent);

                int mode = AudioManager.RINGER_MODE_SILENT;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    Log.e("pLocationInfoVolmslnt2", i.helper.volume_mode_silent);

                    pAudioManager.setRingerMode(mode);
                }
            }
            else if (pLocationInfo.volume.equalsIgnoreCase(i.helper.volume_mode_vibration))
            {
                Log.e("pLocationInfoVolmVbr", i.helper.volume_mode_vibration);

                int mode = AudioManager.RINGER_MODE_VIBRATE;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    Log.e("pLocationInfoVolmVbr2", i.helper.volume_mode_vibration);

                    pAudioManager.setRingerMode(mode);
                }
            }
            else
            {
                Log.e("pLocationInfoVolumeRng", "Normal ring");

                int mode = AudioManager.RINGER_MODE_NORMAL;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    Log.e("pLocationInfoVolmRng2", "Normal ring");

                    pAudioManager.setRingerMode(mode);
                }

                if (isNotificationPost)
                {
                    pAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, Integer.parseInt(pLocationInfo.volume), AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
                }
                else //ringTone
                {
                    pAudioManager.setStreamVolume(AudioManager.STREAM_RING, Integer.parseInt(pLocationInfo.volume), AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);

                    Uri alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    this.m_ringtone = RingtoneManager.getRingtone(pContext, alertUri);

                    if ((this.m_ringtone != null) && (!(this.m_ringtone.isPlaying())))
                    {
                        this.m_ringtone.play();
                    }
                }
            }

            Log.e("Category: ", pLocationInfo.category);
            Log.e("Volume: ", String.valueOf(pLocationInfo.volume));
        }
        else
        {
            Log.e("pLocationInfo else: ", "pLocationInfo == null");
        }
    }

    public boolean isDeviceModeInVibrateOrSilent(Context pContext)
    {
        AudioManager pAudioManager = (AudioManager)pContext.getSystemService(Context.AUDIO_SERVICE);

        switch (pAudioManager.getRingerMode())
        {
            case AudioManager.RINGER_MODE_SILENT:
                return (true);
            case AudioManager.RINGER_MODE_VIBRATE:
                return (true);
            case AudioManager.RINGER_MODE_NORMAL:
                return (false);
        }

        return (false);
    }

    public int getDeviceRingerMode(Context pContext)
    {
        AudioManager pAudioManager = (AudioManager)pContext.getSystemService(Context.AUDIO_SERVICE);

        switch (pAudioManager.getRingerMode())
        {
            case AudioManager.RINGER_MODE_SILENT:
                return (AudioManager.RINGER_MODE_SILENT);
            case AudioManager.RINGER_MODE_VIBRATE:
                return (AudioManager.RINGER_MODE_VIBRATE);
            case AudioManager.RINGER_MODE_NORMAL:
                return (AudioManager.RINGER_MODE_NORMAL);
            default:
                return (AudioManager.RINGER_MODE_NORMAL);
        }
    }

    public void setDeviceStateToOriginal(int deviceMode, Context pContext)
    {
        Log.e("DeviceStateToOriginal", "setDeviceStateToOriginal");
        AudioManager pAudioManager = (AudioManager) pContext.getSystemService(pContext.AUDIO_SERVICE);

        switch (deviceMode)
        {
            case AudioManager.RINGER_MODE_SILENT:

                Log.e("DeviceStateToOriginal", "RINGER_MODE_SILENT");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    pAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }

                break;
            case AudioManager.RINGER_MODE_VIBRATE:

                Log.e("DeviceStateToOriginal", "RINGER_MODE_VIBRATE");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    pAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }

                break;
            case AudioManager.RINGER_MODE_NORMAL:

                Log.e("DeviceStateToOriginal", "RINGER_MODE_NORMAL");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    Log.e("DeviceStateToOriginal", "RINGER_MODE_NORMAL");
                    pAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }

                int volume = pAudioManager.getStreamVolume(AudioManager.STREAM_RING); //STREAM_RING replace STREAM_VOICE_CALL
                pAudioManager.setStreamVolume(AudioManager.STREAM_RING, volume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);

                break;
            default:
                break;
        }
    }

    public void getDeviceProfiles(Context pContext)
    {
        AudioManager pAudioManager = (AudioManager)pContext.getSystemService(Context.AUDIO_SERVICE);

        switch (pAudioManager.getRingerMode())
        {
            case AudioManager.RINGER_MODE_SILENT:
                pAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                pAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                pAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                //pAudioManager.se
                break;
        }
    }

    public static void putSharedPreferencesString(Context context, String key,
                                                  String val) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, val);
        edit.commit();
    }

    public static void putSharedPreferencesInt(Context context, String key,
                                                int val) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt(key, val);
        edit.commit();
    }

    public static void putSharedPreferencesFloat(Context context, String key,
                                                 float val) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putFloat(key, val);
        edit.commit();
    }

    public static void putSharedPreferencesBoolean(Context context, String key,
                                                 Boolean val) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(key, val);
        edit.commit();
    }

    public float getSharedPreferencesFloat(Context context, String key,
                                                  float _default) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return preferences.getFloat(key, _default);
    }

    public String getSharedPreferencesString(Context context, String key, String _default) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return preferences.getString(key, _default);
    }

    public int getSharedPreferencesInt(Context context, String key, int _default) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return preferences.getInt(key, _default);
    }

    public Boolean getSharedPreferencesBoolean(Context context, String key, Boolean _default) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return preferences.getBoolean(key, _default);
    }

    public void singleButtonAlert(final Context context, String title, String msg, final Activity pActivity)
    {
        final ACMapAct gpsTraker = new ACMapAct();

        AlertDialog.Builder pAlertDialog = new AlertDialog.Builder(context);
        pAlertDialog.setTitle(title);
        pAlertDialog.setMessage(msg);

        pAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                pActivity.finish();
                //dialog.cancel();
                //i.helper.getCurrentLocation(context); //by asha
                //gpsTraker.callLocationGpsTracker();

            }
        });

        pAlertDialog.show();
    }

    /*public void temp(Context pContext)
    {
        GPSTracker tracker = new GPSTracker(pContext);
        Location pLocation = tracker.getLocation();

        double latitude = pLocation.getLatitude();
        double longitude = pLocation.getLongitude();

        //double latitude = 17.712353;//pLocation.getLatitude();//Kali matha temple
        //double longitude = 83.318824;//pLocation.getLongitude();//Kali matha temple

        //double latitude = 17.717111;//pLocation.getLatitude();//Novotel
        //double longitude = 83.329895;//pLocation.getLongitude();//Novotel

        Geocoder geocoder = new Geocoder(pContext.getApplicationContext(), Locale.getDefault());

        try
        {
            List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 10);

            if (listAddresses != null && listAddresses.size() > 0)
            {
                Log.e("listAddresses: ", listAddresses.toString());

                String pLocationName = listAddresses.get(0).getAddressLine(0);
                Log.e("pLocationName: ", pLocationName);

                String pFeatureName = listAddresses.get(0).getFeatureName();
                Log.e("pFeatureName: ", pFeatureName);

                String getLocality = listAddresses.get(0).getLocality();
                Log.e("getLocality: ", getLocality);

                Bundle getExtras = listAddresses.get(0).getExtras();
                Log.e("getExtras: ", "" + getExtras);

                Locale pGetLocale = listAddresses.get(0).getLocale();
                Log.e("pGetLocale: ", "" + pGetLocale);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }*/

    public Boolean setPermissionsForLocation(Context context, Activity pActivity)
    {
        LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            Log.e("ACMapAct", "if");
            showAlert(context, pActivity);

            return (false);
        }

        return (true);
    }

    private void showAlert(Context context, final Activity pActivity)
    {
        final int m_GPSLOCATION_REQUEST_CODE = 100;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Location Services Not Active");
        builder.setMessage("Please enable Location Services and GPS");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                pActivity.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), m_GPSLOCATION_REQUEST_CODE);
            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void stopRingtone(boolean isSetNil)
    {
        if (this.m_ringtone != null)
        {
            this.m_ringtone.stop();

            if (isSetNil)
            {
                this.m_ringtone = null;
            }
        }
    }

    public void getAddress(double lat, double lng, Activity pActivity)
    {
        Geocoder pGeocoder = new Geocoder(pActivity);

        try {
            List<Address> addresses = pGeocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String addressOfLocation = obj.getAddressLine(0);

            addressOfLocation = addressOfLocation + "\n" + obj.getCountryName();
            addressOfLocation = addressOfLocation + "\n" + obj.getCountryCode();
            //addressOfLocation = addressOfLocation + "\n" + obj.getAdminArea();
            //addressOfLocation = addressOfLocation + "\n" + obj.getPostalCode();
            //addressOfLocation = addressOfLocation + "\n" + obj.getSubAdminArea();
            //addressOfLocation = addressOfLocation + "\n" + obj.getLocality();

            i.helper.completeAddress = addressOfLocation;
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();

            Toast.makeText(pActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
