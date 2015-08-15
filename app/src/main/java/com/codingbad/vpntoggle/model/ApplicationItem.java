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
    private String iconUri;
    private int state;

    public ApplicationItem(Uri icon, String applicationName) {
        this.applicationName = applicationName;
        if (icon != null) {
            this.iconUri = icon.toString();
        }

        state = ThreeStatesButton.StateEnum.THROUGH_VPN.toInt();
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public Uri getIconUri() {
        if (this.iconUri == null) {
            return null;
        }

        return Uri.parse(this.iconUri);
    }

    public void addApplication(String appName) {
        this.applicationName = this.applicationName.concat(", " + appName);
    }

    public void setState(ThreeStatesButton.StateEnum state) {
        this.state = state.toInt();
    }

    public ThreeStatesButton.StateEnum getState() {
        return ThreeStatesButton.StateEnum.fromInt(state);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(applicationName);
        dest.writeString(this.iconUri);
        dest.writeInt(state);
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
        this.iconUri = in.readString();
        this.state = in.readInt();
    }
}
