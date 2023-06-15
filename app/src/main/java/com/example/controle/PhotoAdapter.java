package com.example.controle;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PhotoAdapter extends ArrayAdapter<PhotoItem> {
    private Context context;
    private ArrayList<PhotoItem> photoList;

    public PhotoAdapter(Context context, ArrayList<PhotoItem> photoList) {
        super(context, 0, photoList);
        this.context = context;
        this.photoList = photoList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        ViewHolder viewHolder;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.photoImageView = listItemView.findViewById(R.id.image_view_item);
            viewHolder.timestampTextView = listItemView.findViewById(R.id.text_view_item);

            listItemView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) listItemView.getTag();
        }

        PhotoItem currentItem = photoList.get(position);

        // Set the photo image
        Bitmap photoBitmap = currentItem.getPhotoBitmap();
        viewHolder.photoImageView.setImageBitmap(photoBitmap);

        // Set the timestamp
        long timestamp = currentItem.getTimestamp();
        viewHolder.timestampTextView.setText(String.valueOf(timestamp));

        return listItemView;
    }

    static class ViewHolder {
        ImageView photoImageView;
        TextView timestampTextView;
    }
}
