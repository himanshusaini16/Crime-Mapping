package com.example.himanshu.crimemapping;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CustomAdapter extends ArrayAdapter<String> {

    private int[] crime_images;
    private int[] crime_marker;
    private int[] crime_heading;
    private int[] crime_description;
    private Context mContext;
    public CustomAdapter(@NonNull Context context, int[] crime_images,int[] crime_marker,int[] crime_heading,int [] crime_description) {
        super(context, R.layout.layout_crime_list_item);
        this.crime_images=crime_images;
        this.crime_marker=crime_marker;
        this.crime_heading=crime_heading;
        this.crime_description=crime_description;
        this.mContext=context;
    }

    @Override
    public int getCount() {
        return crime_heading.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.spinner_layout, parent, false);
            mViewHolder.crime_images = convertView.findViewById(R.id.crime_image_spinner);
            mViewHolder.crime_heading = convertView.findViewById(R.id.crime_heading);
            mViewHolder.crime_description = convertView.findViewById(R.id.crime_description);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.crime_images.setImageResource(crime_images[position]);
        mViewHolder.crime_heading.setText(crime_heading[position]);
        mViewHolder.crime_description.setText(crime_description[position]);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    private static class ViewHolder {
        ImageView crime_images;
        TextView crime_heading;
        TextView crime_description;
    }
}
