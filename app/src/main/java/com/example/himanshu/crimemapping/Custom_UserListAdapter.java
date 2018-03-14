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

public class Custom_UserListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Crime_UserRelated> crimeItemsUser;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public Custom_UserListAdapter(Activity activity, List<Crime_UserRelated> crimeItemsUser) {
        this.activity = activity;
        this.crimeItemsUser = crimeItemsUser;
    }

    @Override
    public int getCount() {
        return crimeItemsUser.size();
    }

    @Override
    public Object getItem(int location) {
        return crimeItemsUser.get(location);
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
            convertView = inflater.inflate(R.layout.layout_userprofile_crime_list, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = convertView.findViewById(R.id.user_crimeimage);
        TextView heading = convertView.findViewById(R.id.user_crimeheading);
        TextView desc = convertView.findViewById(R.id.user_crimedesc);
        TextView addr = convertView.findViewById(R.id.user_crimeaddress);


        Crime_UserRelated ma = crimeItemsUser.get(position);

        // thumbnail image
        thumbNail.setImageUrl(ma.getThumbnailUrlUser(), imageLoader);

        // title
        heading.setText(ma.getTitleUser());
        desc.setText(ma.getDesUser());
        addr.setText("Location: " + ma.getAddressUser());


        return convertView;
    }

}