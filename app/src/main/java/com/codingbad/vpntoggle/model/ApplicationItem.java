package com.codingbad.vpntoggle.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.codingbad.library.view.ThreeStatesButton;

/**
 * Created by ayi on 7/19/15.
 *
 * Each item represents a set of applications that share the same UID.
 * The icon is the first one in a set of icons for now and the applications list is a comma
 * separated list of the applications' names.
 */
public class ApplicationItem implements Parcelable {
    private String applicationName;
    private Uri iconUri;
    private ThreeStatesButton.StateEnum state = ThreeStatesButton.StateEnum.AUTOMATIC;

    public ApplicationItem(Uri icon, String applicationName) {
        this.applicationName = applicationName;
        this.iconUri = icon;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public Uri getIconUri() {
        return this.iconUri;
    }

    public void addApplication(String appName) {
        this.applicationName = this.applicationName.concat(", " + appName);
    }

    public void setState(ThreeStatesButton.StateEnum state) {
        this.state = state;
    }

    public ThreeStatesButton.StateEnum getState() {
        return state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(applicationName);
        String iconUriString = null;

        if (this.iconUri != null) {
            iconUriString = this.iconUri.toString();
        }

        dest.writeString(iconUriString);
        dest.writeInt(state.toInt());
    }

    public static final Parcelable.Creator<ApplicationItem> CREATOR
            = new Parcelable.Creator<ApplicationItem>() {
        public ApplicationItem createFromParcel(Parcel in) {
            return new ApplicationItem(in);
        }

        public ApplicationItem[] newArray(int size) {
            return new ApplicationItem[size];
        }
    };

    private ApplicationItem(Parcel in) {
        this.applicationName = in.readString();
        this.iconUri = Uri.parse(in.readString());
        this.state = ThreeStatesButton.StateEnum.fromInt(in.readInt());
    }
}
