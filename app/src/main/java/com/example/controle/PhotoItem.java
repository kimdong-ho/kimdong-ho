package com.example.controle;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class PhotoItem implements Parcelable {
    private Bitmap photoBitmap;
    private long timestamp;

    public PhotoItem(Bitmap photoBitmap, long timestamp) {
        this.photoBitmap = photoBitmap;
        this.timestamp = timestamp;
    }

    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    public long getTimestamp() {
        return timestamp;
    }

    protected PhotoItem(Parcel in) {
        photoBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        timestamp = in.readLong();
    }

    public static final Creator<PhotoItem> CREATOR = new Creator<PhotoItem>() {
        @Override
        public PhotoItem createFromParcel(Parcel in) {
            return new PhotoItem(in);
        }

        @Override
        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(photoBitmap, flags);
        dest.writeLong(timestamp);
    }
}
