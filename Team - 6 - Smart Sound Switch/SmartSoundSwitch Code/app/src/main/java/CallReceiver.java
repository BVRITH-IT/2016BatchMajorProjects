package se.astacus.smartsoundswitch_raju;

import android.content.Context;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class CallReceiver extends PhonecallReceiver
{
    public CallReceiver()
    {
        super();
    }
    //private Ringtone m_ringtone;

    protected void onIncomingCallReceived(Context ctx, String number, Date date)
    {
        Toast.makeText(ctx, "onIncomingCallReceived", Toast.LENGTH_SHORT).show();

        Log.e("onIncomingCallReceived", number);
        Log.e("onIncomingCallReceived", String.valueOf(date));

        i.helper.actualDeviceMode = i.helper.getDeviceRingerMode(ctx);
        Log.e("actualDeviceMode", String.valueOf(i.helper.actualDeviceMode));

        if (number.contains("+"))
        {
            number = number.replace("+91", "");
        }

        Uri alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        i.helper.m_ringtone = RingtoneManager.getRingtone(ctx, alertUri);
        i.helper.stopRingtone(false);

        ACNumber pNumber = new ACNumber(number, date);
        //i.helper.incomingCallsNumbersList.add(pNumber);

        if (pNumber.isUrgentCall(i.helper.incomingCallsNumbersList)) //temp Commented
        {
            Log.e("NumberExistance: ", "Incoming number is exist in Favourites");
            Log.e("IncomingCallsCount", String.valueOf(i.helper.incomingCallsNumbersList.size()));

            if (i.helper.isDeviceModeInVibrateOrSilent(ctx))//by asha
            {
                Toast.makeText(ctx, "CallReceiver If", Toast.LENGTH_SHORT).show();
                Log.e("onIncomingCallReceived:", "onIncomingCallReceived");

                AudioManager audioManager = (AudioManager) ctx.getSystemService(ctx.AUDIO_SERVICE);
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);

                if ((i.helper.m_ringtone != null) && (!(i.helper.m_ringtone.isPlaying())))
                {
                    Log.e("m_ringtone:", "play");

                    i.helper.m_ringtone.play();
                }
            }

            i.helper.incomingCallsNumbersList.clear();
        }
        else
        {
            Toast.makeText(ctx, "CallReceiver else", Toast.LENGTH_SHORT).show();
            Log.v("NumberExistance: ", "Incoming number doesn't exist in Favourites");

            i.helper.incomingCallsNumbersList.add(pNumber);
            i.helper.updateRingerVolume(ctx, false);
        }
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start)
    {
        Log.e("onIncomingCallAnswered", number);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end)
    {
        Log.e("onIncomingCallEnded", number);

        i.helper.stopRingtone(true);

        //updated the normal volume after user move from church/temple/hospital etc.
        i.helper.setDeviceStateToOriginal(i.helper.actualDeviceMode, ctx);
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start)
    {
        Log.e("onOutgoingCallStarted", number);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end)
    {
        Log.e("onOutgoingCallEnded", number);
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start)
    {
        Log.e("onMissedCall", number);

        i.helper.stopRingtone(true);
        i.helper.setDeviceStateToOriginal(i.helper.actualDeviceMode, ctx);
    }
}