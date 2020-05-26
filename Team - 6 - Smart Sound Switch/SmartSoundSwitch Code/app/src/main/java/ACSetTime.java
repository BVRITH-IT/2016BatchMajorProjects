package se.astacus.smartsoundswitch_raju;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class ACSetTime implements /*View.OnFocusChangeListener,*/ TimePickerDialog.OnTimeSetListener, View.OnClickListener {
    private TextView editText;
    private Calendar myCalendar;
    private Context ctx;

    public ACSetTime(TextView editText, Context ctx)
    {
        this.editText = editText;
        this.editText.setOnClickListener(this);
        //this.editText.setOnFocusChangeListener(this);
        this.myCalendar = Calendar.getInstance();
        this.ctx = ctx;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        // TODO Auto-generated method stub
        //this.editText.setText( hourOfDay + ":" + minute);
        showTime(hourOfDay, minute);
    }

    public void showTime(int hour, int min)
    {
        String format = "";

        if (hour == 0)
        {
            hour += 12;
            format = "AM";
        }
        else if (hour == 12)
        {
            format = "PM";
        }
        else if (hour > 12)
        {
            hour -= 12;
            format = "PM";
        }
        else
        {
            format = "AM";
        }

        StringBuilder pStringBuilder = new StringBuilder();
        pStringBuilder.append(hour);
        pStringBuilder.append(":");
        pStringBuilder.append(min);
        pStringBuilder.append(" ");
        pStringBuilder.append(format);

        this.editText.setText(pStringBuilder);
    }

    @Override
    public void onClick(View v)
    {
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);
        TimePickerDialog pTimePickerDialog = new TimePickerDialog(ctx, this, hour, minute, false);
        pTimePickerDialog.show();
    }
}
