package com.example.himanshu.crimemapping.Layouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.himanshu.crimemapping.InfoWindowData;
import com.example.himanshu.crimemapping.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;


public class InfoWindowCustom implements GoogleMap.InfoWindowAdapter {

    private Context context;


    InfoWindowCustom(Context contextt) {
        this.context = contextt;
    }


    @Override
    public View getInfoWindow(Marker marker) {
        return null;

    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.infowindow_custom_layout, null);


        TextView info_heading = v.findViewById(R.id.infowindow_heading);
        TextView info_description = v.findViewById(R.id.infowindow_description);
        TextView info_date = v.findViewById(R.id.infowindow_date);
        TextView info_time = v.findViewById(R.id.infowindow_time);
        TextView date_time_reported = v.findViewById(R.id.infowindow_reportedon);

        try {
            InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

            info_heading.setText(infoWindowData.getHeading());
            info_description.setText(infoWindowData.getDescription());
            info_date.setText(infoWindowData.getDate());
            info_time.setText(infoWindowData.getTime());
            date_time_reported.setText(infoWindowData.getDateReported() + "| " + infoWindowData.getTimeReported());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }
}
