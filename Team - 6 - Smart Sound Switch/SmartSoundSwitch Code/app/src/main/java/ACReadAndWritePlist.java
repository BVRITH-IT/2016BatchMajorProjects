package se.astacus.smartsoundswitch_raju;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import xmlwise.Plist;
import xmlwise.XmlParseException;

//import xmlwise.Plist;

public class ACReadAndWritePlist
{
    //String fileName = "smartSound.plist";
    //public List<ACLocationInfo> locations = new ArrayList<ACLocationInfo>();

    public ACReadAndWritePlist()
    {

    }

    public void readPlist()
    {
        try
        {
            if (i.helper.pathFileLoginCredentialsPlist.exists())
            {
                Log.e("filePath:",String.valueOf(i.helper.pathFileLoginCredentialsPlist));
                //Read from sdcard
                FileInputStream pFileInputStream = new FileInputStream(i.helper.pathFileLoginCredentialsPlist);
                BufferedReader pBufferedReader = new BufferedReader(new InputStreamReader(pFileInputStream));

                StringBuffer pFileData = new StringBuffer(1024);
                String pInputLine;

                while ((pInputLine = pBufferedReader.readLine()) != null)
                {
                    pFileData.append(pInputLine);
                }

                pBufferedReader.close();

                HashMap<String, Object> pDictionaryRoot = (HashMap<String, Object>) Plist.objectFromXml(pFileData.toString());
                List<HashMap<String, Object>> pDictionaryLocations = (List<HashMap<String, Object>>)pDictionaryRoot.get("Locations");

                if (pDictionaryLocations != null)
                {
                    i.helper.locationsListUser.clear();

                    for (HashMap<String, Object> pDictionary: pDictionaryLocations)
                    {
                        ACLocationInfo pLcoationInfo = new ACLocationInfo();
                        pLcoationInfo.initWithDictionary(pDictionary);
                        i.helper.locationsListUser.add(pLcoationInfo);
                    }
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (XmlParseException e)
        {
            e.printStackTrace();
        }
    }

    public void writePlist()
    {
        String pPathpFilePlist = i.helper.pathFileLoginCredentialsPlist.getPath();

        try
        {
            if (pPathpFilePlist != null)
            {
                HashMap<String, Object> pDictionaryRoot = new HashMap<String, Object>();

                List<HashMap<String, Object>> pDictionaryLocations = new ArrayList<HashMap<String, Object>>();

                for (ACLocationInfo pLocationInfo: i.helper.locationsListUser)
                {
                    pDictionaryLocations.add(pLocationInfo.getDictionary());
                }

                pDictionaryRoot.put("Locations", pDictionaryLocations);

                Plist.storeObject(pDictionaryRoot, pPathpFilePlist);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
