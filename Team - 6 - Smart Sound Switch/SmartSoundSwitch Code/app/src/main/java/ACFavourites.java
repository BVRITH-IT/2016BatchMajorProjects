package se.astacus.smartsoundswitch_raju;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ACFavourites
{
    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;
    private int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 102;
    public Context mContext;

    public ACFavourites(Context mContext)
    {
        this.mContext = mContext;
    }

    /*@RequiresApi(api = Build.VERSION_CODES.M)
    public void updatePermissionsForContactsAndPhoneCalls(Activity pActivity)
    {
        Log.e("ACFavourites", "updatePermissionsForContactsAndPhoneCalls");

        if (pActivity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(pActivity, new String[]{Manifest.permission.READ_PHONE_STATE}, this.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }

        if (ActivityCompat.checkSelfPermission(pActivity, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
        {
            i.helper.favouriteNumbersList = getFavouriteContactNumbers();
        }
        else
        {
            requestLocationPermission(pActivity);
        }
    }*/

    /*protected void requestLocationPermission(Activity pActivity)
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(pActivity, android.Manifest.permission.READ_CONTACTS))
        {// show UI part if you want here to show some rationale !!!

        }
        else
        {
            ActivityCompat.requestPermissions(pActivity, new String[]{android.Manifest.permission.READ_CONTACTS}, this.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }*/

    public ArrayList<String> getFavouriteContactNumbers()
    {
        ArrayList<String> pFavouriteNumbers = new ArrayList<String>();

        Cursor pCursor = this.mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.STARRED + "=1", null, null);

        if (pCursor.getCount() > 0)
        {
            while (pCursor.moveToNext())
            {
                String pPhoneNumber = pCursor.getString(pCursor.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (pPhoneNumber.contains("+"))
                {
                    pPhoneNumber = pPhoneNumber.replace("+91", "");
                }

                pFavouriteNumbers.add(pPhoneNumber);
            }
        }

        pCursor.close();

        return (pFavouriteNumbers);
    }
}
