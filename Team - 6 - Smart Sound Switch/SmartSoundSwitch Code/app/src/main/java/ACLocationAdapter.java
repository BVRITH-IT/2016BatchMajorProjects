package se.astacus.smartsoundswitch_raju;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

public class ACLocationAdapter extends BaseAdapter
{
    List<ACLocationInfo> locationInfos;
    Activity activity;

    public ACLocationAdapter(Activity activity, List<ACLocationInfo> locationInfo)
    {
        super();
        this.activity = activity;
        this.locationInfos = locationInfo;
    }

    @Override
    public int getCount()
    {
        return (this.locationInfos.size());
    }

    @Override
    public Object getItem(int i)
    {
        return (this.locationInfos.get(i));
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    private class ViewHolder
    {
        TextView txt_lat, txt_long, txt_category, txt_volume, txt_remarks, txt_name;
        Button btn_Edit, btn_delete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        final ViewHolder holder;
        LayoutInflater inflater =  this.activity.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.listview_item, null);

            holder = new ViewHolder();
            holder.txt_name = (TextView) convertView.findViewById(R.id.id_txtName); //uncomment
            holder.txt_category = (TextView) convertView.findViewById(R.id.id_txtCategory);
            holder.txt_volume = (TextView) convertView.findViewById(R.id.id_txtVolume);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final ACLocationInfo pLocationInfo  = this.locationInfos.get(position);
        holder.txt_name.setText(String.format("%s", i.helper.getLocationName(pLocationInfo.completeAddress)));
        holder.txt_category.setText(String.format("%s", pLocationInfo.category));
        holder.txt_volume.setText(String.valueOf(pLocationInfo.volume));
        //holder.txt_lat.setText(String.format("%.4f", pLocationInfo.location.getLatitude()));
        //holder.txt_long.setText(String.format("%.4f", pLocationInfo.location.getLongitude()));
        //holder.txt_remarks.setText(pLocationInfo.remarks);

        //holder.btn_Edit.setTag(position);
        //holder.btn_delete.setTag(position);

        /*holder.btn_Edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                i.helper.editLocationIndex = (Integer) v.getTag();

                Intent pIntent = new Intent(activity, ACSetLocationAct.class);

                if (pLocationInfo != null)
                {
                    Location pLocation = pLocationInfo.location;
                    pIntent.putExtra("Category", pLocationInfo.category);
                    pIntent.putExtra("Volume", pLocationInfo.volume);
                    pIntent.putExtra("Lat", pLocation == null ? 0.0 : pLocationInfo.location.getLatitude());
                    pIntent.putExtra("Long", pLocation == null ? 0.0 : pLocationInfo.location.getLongitude());
                    pIntent.putExtra("Remarks", pLocationInfo.remarks);
                    pIntent.putExtra("AddOrEdit", "Edit");
                }

                activity.startActivity(pIntent);
                notifyDataSetChanged();
            }
        });*/

        /*holder.btn_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //i.Helper.loadAnimation(activity, v);

                AlertDialog.Builder pAlert = new AlertDialog.Builder(activity);
                pAlert.setTitle("Delete Location");
                pAlert.setMessage("Are you sure to delete " + pLocationInfo.category + " location?");

                final int positionToRemove = (Integer) v.getTag();

                pAlert.setNegativeButton("Cancel", null);

                pAlert.setPositiveButton("Ok", new AlertDialog.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        locationInfos.remove(positionToRemove);
                        i.helper.allData.writePlist();
                        notifyDataSetChanged();
                    }
                });

                pAlert.show();
            }
        });*/

        return (convertView);
    }
}
