package se.astacus.smartsoundswitch_raju;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class ACNumber
{
    public String number;
    public Date date;

    public ACNumber()
    {
        number = "";
    }

    public ACNumber(String pNumber, Date pDate)
    {
        this.number = pNumber;
        this.date = pDate;
    }

    //If a number repeat called 3 times with in 5 mins.is called an urgent call.
    public boolean isUrgentCall(ArrayList<ACNumber> pNumbers)
    {
        Log.e("isUrgentCall", "isUrgentCall");

        if (pNumbers.size() <= 1)
        {
            return (false);
        }

        int nMatches = 1;

        for (int index = i.helper.incomingCallsNumbersList.size() - 1; index >= 0; index--)
        {
            ACNumber pNumber = i.helper.incomingCallsNumbersList.get(index);  //2 no from last

            if (this.number.equalsIgnoreCase(pNumber.number))
            {
                nMatches++;

                if (nMatches >= 3)
                {
                    if (this.isDateDifferenceLessThan5Mins(pNumber))
                    {
                        return (true);
                    }
                }
            }
        }

        return (false);
    }

    public boolean isDateDifferenceLessThan5Mins(ACNumber pNumber)
    {
        long millis = this.date.getTime() - pNumber.date.getTime();
        int hours = (int) (millis / (1000 * 60 * 60));
        int mins = (int) ((millis / (1000 * 60)) % 60);

        return (mins < 5);
    }
}
