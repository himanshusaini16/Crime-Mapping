package com.example.himanshu.crimemapping;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Crime> crimeItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<Crime> crimeItems) {
        this.activity = activity;
        this.crimeItems = crimeItems;
    }

    @Override
    public int getCount() {
        return crimeItems.size();
    }

    @Override
    public Object getItem(int location) {
        return crimeItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.layout_crime_list_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.im_crimeimage);
        TextView heading = (TextView) convertView.findViewById(R.id.tv_crimeheading);
        TextView desc = (TextView) convertView.findViewById(R.id.tv_crimedesc);
        TextView date = (TextView) convertView.findViewById(R.id.tv_crimedate);
        TextView time = (TextView) convertView.findViewById(R.id.tv_crimetime);
        TextView addr = (TextView) convertView.findViewById(R.id.tv_crimeaddress);

        // getting movie data for the row
        Crime m = crimeItems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // title
        heading.setText(m.getTitle());
        desc.setText(m.getDes());
        date.setText("Date: "+m.getDate());
        time.setText("Time: "+m.getTime());
        addr.setText("Location: " + m.getAddress());



        return convertView;
    }

}