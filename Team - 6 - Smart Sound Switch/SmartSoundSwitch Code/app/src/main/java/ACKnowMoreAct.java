package se.astacus.smartsoundswitch_raju;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ACKnowMoreAct extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        i.helper.hideTitleAndTitleBar(this);

        setContentView(R.layout.activity_acknow_more);

        i.helper.changeStatusBarColor(this);

        setHeader();

        TextView pTxtView = (TextView)findViewById(R.id.txt_features);

        String pDisplayString = " 1. You can choose your preferred locations from map, by enabling GPS and Location Services. \n\n";
        pDisplayString += "2. Set audio preferences accordingly for chosen locations and never get disturbed.\n\n";
        pDisplayString += "3. Your calendar events are taken into consideration too!\n\n";
        pDisplayString += "4. Don't worry about missing your important calls! :) \n\n 5. If there is a call more than twice within 5 minutes, the settings are disabled and you can hear the ring normally.\n\n";
        pDisplayString += "6. Just swipe right to delete/edit any added location.\n\n";

        pTxtView.setText(pDisplayString);
    }

    private void setHeader()
    {
        TextView txt_left = (TextView)findViewById(R.id.id_txtLeft);
        TextView txt_heading = (TextView)findViewById(R.id.id_txtHeading);
        TextView txt_right = (TextView)findViewById(R.id.id_txtRight);

        txt_left.setText("Back");
        txt_heading.setText("Features");
        txt_right.setVisibility(View.INVISIBLE);

        txt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }
}
