package se.astacus.smartsoundswitch_raju;

import android.content.Context;
import android.util.Log;

import java.util.TimerTask;

public class ACTimerTask extends TimerTask
{
    public Context _context;

    public ACTimerTask(Context pContext)
    {
        this._context = pContext;
    }
    @Override
    public void run()
    {
        Log.e("ACTimerTask run", "ACTimerTask run");

        //i.helper.updateRingerVolume(this._context);
    }
}
