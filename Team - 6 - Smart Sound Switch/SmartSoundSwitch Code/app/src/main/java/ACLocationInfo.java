package se.astacus.smartsoundswitch_raju;

import android.location.Location;

import java.io.Serializable;
import java.util.HashMap;

public class ACLocationInfo
{
    public Location location;
    public String category;
    public String volume;
    //public String remarks;
    //public String locationName;
    public String completeAddress;
    public String fromTime, toTime;

    public  ACLocationInfo()
    {
        this.location = new Location("");
        this.category = "";
        this.volume = "";
        //this.remarks = "";
        //this.locationName = "";
        this.completeAddress = "";
        this.fromTime = "";
        this.toTime = "";
    }

    public void initWithDictionary(HashMap<String, Object> pHashMap)
    {
        double latitude = (double) pHashMap.get("latitude");
        double longitude = (double) pHashMap.get("longitude");
        this.category = (String) pHashMap.get("category");
        this.volume = (String) pHashMap.get("volume");
        //this.remarks = (String) pHashMap.get("remarks");
        //this.locationName = (String) pHashMap.get("name");
        this.completeAddress = (String) pHashMap.get("completeAddress");
        this.fromTime = (String) pHashMap.get("fromTime");
        this.toTime = (String) pHashMap.get("toTime");

        this.location = new Location("");
        this.location.setLatitude(latitude);
        this.location.setLongitude(longitude);
    }

    public HashMap<String, Object> getDictionary()
    {
        HashMap<String, Object> pDictionary = new HashMap<String, Object>();

        pDictionary.put("latitude", this.location == null ? 0 : this.location.getLatitude());
        pDictionary.put("longitude", this.location == null ? 0 : this.location.getLongitude());
        pDictionary.put("category", this.category);
        pDictionary.put("volume", this.volume);
        //pDictionary.put("remarks", this.remarks);
        //pDictionary.put("name", this.locationName);
        pDictionary.put("completeAddress", this.completeAddress);
        pDictionary.put("fromTime", this.fromTime);
        pDictionary.put("toTime", this.toTime);

        return (pDictionary);
    }

    /*private void setVolumeForVibrationOrSilent()
    {
        if (this.volume.equalsIgnoreCase(i.helper.volume_mode_silent))
        {
            this.volume = "0";
        }
        else if (this.volume.equalsIgnoreCase(i.helper.volume_mode_vibration))
        {
            this.volume = "-1";
        }
    }*/
}
