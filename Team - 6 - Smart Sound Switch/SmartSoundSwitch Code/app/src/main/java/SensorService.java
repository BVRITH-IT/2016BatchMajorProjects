package se.astacus.smartsoundswitch_raju;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class SensorService extends Service
{
    public  int counter = 0;
    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    //public PhonecallReceiver mPhonecallReceiver;

    public SensorService(Context pContext)
    {
        super();
        Log.e("SensorService", "here I am!");
    }

    public SensorService()
    {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) //start the timer which will print the value of the counter every second
    {
        Log.e("SensorService", "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        if (intent == null)
        {
            Log.e("SensorService intent", "null");

            return START_STICKY_COMPATIBILITY;
        }

        if (intent.getExtras() == null)
        {
            Log.e("SensorintentgetExtras", "null");

            return START_STICKY_COMPATIBILITY;
        }

        Boolean isStartServiceAfterCloseApp = i.helper.getSharedPreferencesBoolean(getApplicationContext(), "isStartService", false); //BY ASHA

        //Boolean isStartServiceAfterCloseApp = i.helper.getSharedPreferencesBoolean(context, "isStartServiceAfterCloseApp", false);
        Toast.makeText(getApplicationContext(), "onStartCommand " + isStartServiceAfterCloseApp.toString(), Toast.LENGTH_SHORT).show();

        if (isStartServiceAfterCloseApp == true)
        {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            Log.e("isStartServiceAfter", stateStr);
            Log.e("isStartServiceAfter", number);

            int state = 0;

            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }

            i.helper.mPhonecallReceiver.onCallStateChanged(getApplicationContext(), state, number);
            i.helper.putSharedPreferencesBoolean(getApplicationContext(), "isStartService", false);
        }

        return (START_STICKY);
    }

    @Override
    public void onDestroy() //onDestroy which will restart the service when killed. Ignore the Timer for now.
    {
        super.onDestroy();

        Log.e("SensorService", "ondestroy!");
        Toast.makeText(getApplicationContext(), "onDestroy ", Toast.LENGTH_SHORT).show();

        //i.helper.isStartServiceAfterCloseApp = true;

        i.helper.putSharedPreferencesBoolean(getApplicationContext(), "isStartServiceAfterCloseApp", true);

        //this.mPhonecallReceiver = new PhonecallReceiver(this);

        Intent broadcastIntent = new Intent(this, PhonecallReceiver.class);
        sendBroadcast(broadcastIntent);

        stoptimertask();
    }

    public void startTimer()
    {
        Log.e("SensorService", "startTimer");

        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask()
    {
        Log.e("SensorService", "initializeTimerTask");

        timerTask = new TimerTask()
        {
            public void run()
            {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    public void stoptimertask()
    {
        Log.e("SensorService", "stoptimertask");

        //stop the timer, if it's not already null.
        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
