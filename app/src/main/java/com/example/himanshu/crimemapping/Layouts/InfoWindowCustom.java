package com.example.himanshu.crimemapping.Layouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.example.himanshu.crimemapping.InfoWindowData;
import com.example.himanshu.crimemapping.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class InfoWindowCustom implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private LayoutInflater inflater;


    InfoWindowCustom(Context contextt) {
        this.context = contextt;
    }


    @Override
    public View getInfoWindow(Marker marker) {
        return null;

    }

    @Override
    public View getInfoContents(Marker marker) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.infowindow_custom_layout, null);


        TextView info_heading = (TextView) v.findViewById(R.id.infowindow_heading);
        TextView info_description = (TextView) v.findViewById(R.id.infowindow_description);
        TextView info_date = (TextView) v.findViewById(R.id.infowindow_date);
        TextView info_time = (TextView) v.findViewById(R.id.infowindow_time);

try {
    InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

    info_heading.setText(infoWindowData.getHeading());
    info_description.setText(infoWindowData.getDescription());
    info_date.setText(infoWindowData.getDate());
    info_time.setText(infoWindowData.getTime());
}catch (Exception e){
    e.printStackTrace();
}

        return v;
    }
}
