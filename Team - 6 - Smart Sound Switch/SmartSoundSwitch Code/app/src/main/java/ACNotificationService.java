package se.astacus.smartsoundswitch_raju;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.google.android.gms.common.internal.service.Common;

public class ACNotificationService extends NotificationListenerService
{
    NLServiceReceiver pNLServiceReceiver;

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.e("ACNotificationService", "oncreate");

        pNLServiceReceiver = new NLServiceReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.kpbird.nlsexample.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");

        registerReceiver(pNLServiceReceiver, filter);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        Log.e("ACNotificationService", "onDestroy");
        unregisterReceiver(pNLServiceReceiver);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.e("ACNotificationService", "onBind");
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
        //boolean mOnUnbind = super.onUnbind(mIntent);
        //return mOnUnbind;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {
        Log.e("ACNotificationService", "onNotificationPosted");
        super.onNotificationPosted(sbn);

        //i.helper.actualDeviceMode = i.helper.getDeviceRingerMode(this);

        /*String pText = sbn.getNotification().tickerText.toString();
        Log.e("onNotificationPosted", pText);*/
        Log.e("onNotificationPosted","ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());

        i.helper.updateRingerVolume(this, true);
        /*AudioManager pAudioManager = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
        pAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);*/
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn)
    {
        Log.e("ACNotificationService", "onNotificationRemoved");
        super.onNotificationRemoved(sbn);

        //i.helper.setDeviceStateToOriginal(i.helper.actualDeviceMode, this); //temp
    }

    public class NLServiceReceiver extends BroadcastReceiver
    {
        public NLServiceReceiver()
        {
            Log.e("ACNotificationService", "NLServiceReceiver Constructor");
        }

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.e("ACNotificationService", "onNotificationReceiver");
        }
    }
}
