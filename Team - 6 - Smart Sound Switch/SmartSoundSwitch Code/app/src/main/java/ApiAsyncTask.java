package se.astacus.smartsoundswitch_raju;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiAsyncTask extends AsyncTask<Void, Void, Void>
{
    private ACGoogleCalenderEvents mActivity;

    /**
     * Constructor.
     * @param activity MainActivity that spawned this task.
     */
    ApiAsyncTask(ACGoogleCalenderEvents activity)
    {
        this.mActivity = activity;
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params)
    {
        Log.e("ApiAsyncTask", "doInBackground");

        try
        {
            mActivity.clearResultsText();
            mActivity.updateResultsText(getDataFromApi());

            i.helper.eventsList = getDataFromApi();
        }
        catch (final GooglePlayServicesAvailabilityIOException availabilityException)
        {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(availabilityException.getConnectionStatusCode());
        }
        catch (UserRecoverableAuthIOException userRecoverableException)
        {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(), ACGoogleCalenderEvents.REQUEST_AUTHORIZATION);

        }
        catch (IOException e)
        {
            mActivity.updateStatus("The following error occurred: " + e.getMessage());

            //alert may be show AN ALERT
        }

        return null;
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException
    {
        Log.e("ApiAsyncTask", "getDataFromApi");

        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        List<String> eventStrings = new ArrayList<String>();
        Events events = mActivity.mService.events().list("primary").setMaxResults(30).setTimeMin(now).setOrderBy("startTime")
                 .setSingleEvents(true).setShowDeleted(false)
                .execute();
        List<Event> items = events.getItems();

        for (Event event : items)
        {
            DateTime start = event.getStart().getDateTime();

            if (start == null)
            {
                // All-day events don't have start times, so just use
                // the start date.
                start = event.getStart().getDate();
            }

            eventStrings.add(String.format("%s (%s) location: %s", event.getSummary(), start, event.getLocation()));
        }

        return eventStrings;
    }
}
