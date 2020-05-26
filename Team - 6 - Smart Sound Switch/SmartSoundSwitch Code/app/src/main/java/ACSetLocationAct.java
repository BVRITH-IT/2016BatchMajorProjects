package se.astacus.smartsoundswitch_raju;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.List;
//import com.google.gson.Gson;

public class ACSetLocationAct extends AppCompatActivity implements View.OnClickListener
{
    ACLocationInfo m_LocationInfo = new ACLocationInfo();
    Spinner spn_volumeRanges, spn_categories;
    AudioManager audioManager;
    String m_addOrEdit = "";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.e("ACSetLocationAct", "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); //hide the title bar

        setContentView(R.layout.activity_acset_location);

        i.helper.hideKeyboard(this);
        i.helper.changeStatusBarColor(this);

        this.setTitle("Add Location");
        i.helper.changeStatusBarColor(ACSetLocationAct.this);
        this.setHeader();

        i.helper.locationName = (TextView)findViewById(R.id.id_txtName);
        this.audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        Intent pIntent = getIntent();
        String locationName = (String) pIntent.getStringExtra("Name");
        this.m_LocationInfo.category = (String) pIntent.getStringExtra("Category");
        this.m_LocationInfo.volume = (String) pIntent.getStringExtra("Volume");
        this.m_LocationInfo.location.setLatitude(pIntent.getDoubleExtra("Lat", 0));
        this.m_LocationInfo.location.setLongitude(pIntent.getDoubleExtra("Long", 0));
        this.m_LocationInfo.fromTime = (String) pIntent.getStringExtra("FromTime");
        this.m_LocationInfo.toTime = (String) pIntent.getStringExtra("ToTime");

        //this.m_LocationInfo.remarks = (String) pIntent.getStringExtra("Remarks");

        this.m_addOrEdit = (String) pIntent.getStringExtra("AddOrEdit");

        if (locationName != null)
        {
            i.helper.locationName.setText(locationName);
        }

        setControlsEvents();
        loadDropDownsInfo();

        TextView pTextFromTime = (TextView) findViewById(R.id.id_txtFromTime);
        ACSetTime pACSetFromTime = new ACSetTime(pTextFromTime, this);

        TextView pTextToTime = (TextView) findViewById(R.id.id_txtToTime);
        ACSetTime pACSetToTime = new ACSetTime(pTextToTime, this);
    }

    private void setControlsEvents()
    {
        Button btn_Addlocations = (Button)findViewById(R.id.btn_add);

        RadioButton rbtn_currentLocation = (RadioButton)findViewById(R.id.rdBtnCurrentLocation);
        RadioButton rbtn_LocationOnMap = (RadioButton)findViewById(R.id.rdBtnlocatnOnMap);

        TextView ptxt_locationName = (TextView)findViewById(R.id.id_txtName);

        RadioButton rdBtnVolume = (RadioButton) findViewById(R.id.rad_volume);
        RadioButton rdBtnVibration = (RadioButton) findViewById(R.id.rad_vibration);
        RadioButton rdBtnsilent = (RadioButton) findViewById(R.id.rad_silent);

        this.spn_volumeRanges = (Spinner)findViewById(R.id.spinner_volumeRanges);

        btn_Addlocations.setOnClickListener(this);
        rbtn_currentLocation.setOnClickListener(this);
        rbtn_LocationOnMap.setOnClickListener(this);

        rdBtnVolume.setOnClickListener(this);
        rdBtnVibration.setOnClickListener(this);
        rdBtnsilent.setOnClickListener(this);

        //pTextToTime.setOnFocusChangeListener(this);

        if (this.m_addOrEdit.equalsIgnoreCase("Edit"))
        {
            //rbtn_LocationOnMap.setChecked(true);
            //i.helper.currentLocation = this.m_LocationInfo.location;

            i.helper.updateiHelperCurrentLocation(this.m_LocationInfo.location);

            btn_Addlocations.setText("Update");

            i.helper.locationName.setText(i.helper.getLocationName(i.helper.completeAddress));

            //i.helper.locationName.setText(this.m_LocationInfo.locationName);

            //pEtRemarks.setText(this.m_LocationInfo.remarks);

            if (this.m_LocationInfo.volume.equalsIgnoreCase(i.helper.volume_mode_vibration))
            {
                rdBtnVibration.setChecked(true);
                updateAudioProfile(false);
            }
            else if (this.m_LocationInfo.volume.equalsIgnoreCase(i.helper.volume_mode_silent))
            {
                rdBtnsilent.setChecked(true);
                updateAudioProfile(false);
            }
            else //volume
            {
                rdBtnVolume.setChecked(true);
                updateAudioProfile(true);
            }

            if (i.helper.setPermissionsForLocation(this, ACSetLocationAct.this))
            {
                Location pCurrentLocation = i.helper.getCurrentLocation(this);

                float distance = i.helper.getDistanceInMetersBetweenTwoLocations(pCurrentLocation, this.m_LocationInfo.location);

                if (distance < 100)//100 meters
                {
                    rbtn_currentLocation.setChecked(true);
                }
                else
                {
                    rbtn_LocationOnMap.setChecked(true);
                }

                i.helper.currentLocation.setLatitude(this.m_LocationInfo.location.getLatitude());
                i.helper.currentLocation.setLongitude(this.m_LocationInfo.location.getLongitude());
            }

            TextView pTextFromTime = (TextView) findViewById(R.id.id_txtFromTime);
            TextView pTextToTime = (TextView) findViewById(R.id.id_txtToTime);

            pTextFromTime.setText(this.m_LocationInfo.fromTime);
            pTextToTime.setText(this.m_LocationInfo.toTime);
        }
    }

    private void loadDropDownsInfo()
    {
        this.spn_categories = (Spinner)findViewById(R.id.spinner_Categories);

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, i.helper.categoriesList);
        this.spn_categories.setAdapter(categoriesAdapter);

        ArrayAdapter<String> volumesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, i.helper.volumeRanges);
        this.spn_volumeRanges.setAdapter(volumesAdapter);

        if (this.m_addOrEdit.equalsIgnoreCase("Edit"))
        {
            int pSpnCategoryPosition = categoriesAdapter.getPosition(this.m_LocationInfo.category);
            this.spn_categories.setSelection(pSpnCategoryPosition);

            int pSpnVolumePosition = volumesAdapter.getPosition(this.m_LocationInfo.volume);
            this.spn_volumeRanges.setSelection(pSpnVolumePosition);
        }
    }

    private void setHeader()
    {
        TextView txt_left = (TextView)findViewById(R.id.id_txtLeft);
        TextView txt_heading = (TextView)findViewById(R.id.id_txtHeading);
        TextView txt_right = (TextView)findViewById(R.id.id_txtRight);
        //TextView txt_message = (TextView)findViewById(R.id.id_txtMessage);

        txt_left.setText("Back");
        txt_heading.setText("Add Location");
        txt_right.setVisibility(View.INVISIBLE);
        //txt_message.setVisibility(View.INVISIBLE);

        txt_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        if (id == R.id.rdBtnCurrentLocation)
        {
            if (i.helper.setPermissionsForLocation(this, ACSetLocationAct.this))
            {
                /*this.m_LocationInfo.location.setLatitude(i.helper.currentLocation.getLatitude());
                this.m_LocationInfo.location.setLongitude(i.helper.currentLocation.getLongitude());*/
                Log.e("SetLocation", "rdBtnCurrentLocation Click");
                this.m_LocationInfo.location = i.helper.getCurrentLocation(this);
                i.helper.getAddress(this.m_LocationInfo.location.getLatitude(), this.m_LocationInfo.location.getLongitude(), this);
                i.helper.locationName.setText(i.helper.getLocationName(i.helper.completeAddress));
            }
        }
        else if (id == R.id.rdBtnlocatnOnMap)
        {
            if (i.helper.setPermissionsForLocation(this, ACSetLocationAct.this))
            {
                Intent pIntent = new Intent(ACSetLocationAct.this, ACMapAct.class);
                pIntent.putExtra("AddOrEdit", this.m_addOrEdit);
                startActivity(pIntent);
            }
        }
        else if (id == R.id.btn_add)
        {
            RadioButton rbtn_currentLocation = (RadioButton) findViewById(R.id.rdBtnCurrentLocation);

            if (rbtn_currentLocation.isChecked())//Get current location directly
            {
                /*this.m_LocationInfo.location = i.helper.getCurrentLocation(this);
                i.helper.getAddress(this.m_LocationInfo.location.getLatitude(), this.m_LocationInfo.location.getLongitude(), this);*/
            }
            else//Get location from the MapView pin point
            {
                this.m_LocationInfo.location.setLatitude(i.helper.currentLocation.getLatitude());
                this.m_LocationInfo.location.setLongitude(i.helper.currentLocation.getLongitude());
            }

            Log.e("m_LocationInfo.location", String.valueOf(this.m_LocationInfo.location));

            this.m_LocationInfo.category = this.spn_categories.getSelectedItem().toString();

            RadioButton rdBtnSilent = (RadioButton) findViewById(R.id.rad_silent);
            RadioButton rdBtnVolume = (RadioButton) findViewById(R.id.rad_volume);

            if (rdBtnSilent.isChecked())
            {
                this.m_LocationInfo.volume = i.helper.volume_mode_silent;
            }
            else if (rdBtnVolume.isChecked())
            {
                this.m_LocationInfo.volume = this.spn_volumeRanges.getSelectedItem().toString();
            }
            else //vibrate
            {
                this.m_LocationInfo.volume = i.helper.volume_mode_vibration;
            }

            /*EditText pEdTxtRemarks = (EditText) findViewById(R.id.et_remarks);
            this.m_LocationInfo.remarks = pEdTxtRemarks.getText().toString();*/

            /*TextView ptxt_locationName = (TextView)findViewById(R.id.id_txtName);
            ptxt_locationName.setText(this.m_LocationInfo.getLocationName());*/

            this.m_LocationInfo.completeAddress = i.helper.completeAddress;

            if (this.m_addOrEdit.equalsIgnoreCase("Add"))
            {
                i.helper.locationsListUser.add(this.m_LocationInfo);
                Log.e("locationsListUser", String.valueOf(i.helper.locationsListUser.size()));
            }
            else
            {
                i.helper.locationsListUser.set(i.helper.editLocationIndex, this.m_LocationInfo);
            }

            TextView pEdtTextFromTime = (TextView) findViewById(R.id.id_txtFromTime);
            this.m_LocationInfo.fromTime = pEdtTextFromTime.getText().toString();

            TextView pEdtTextToTime = (TextView) findViewById(R.id.id_txtToTime);
            this.m_LocationInfo.toTime = pEdtTextToTime.getText().toString();

            Log.e("m_LocationInfo.location", "before saveLocationsList");
            i.helper.allData.writePlist();
            Log.e("m_LocationInfo.location", "After saveLocationsList");

            Intent pIntent = new Intent(ACSetLocationAct.this, ACSettingsAct.class);
            startActivity(pIntent);
        }
        else if (id == R.id.rad_volume)
        {
            updateAudioProfile(true);
        }
        else if ((id == R.id.rad_vibration) || (id == R.id.rad_silent))
        {
            updateAudioProfile(false);
        }
        else if (id == R.id.id_txtLeft)
        {
            Intent pIntent = new Intent(ACSetLocationAct.this, ACSettingsAct.class);
            startActivity(pIntent);
        }
        else if (id == R.id.id_txtToTime)
        {
            Log.e("Totime", "click");
        }
    }

    private void updateAudioProfile(Boolean isVolumeChecked)
    {
        this.spn_volumeRanges.setEnabled(isVolumeChecked);
    }
}
