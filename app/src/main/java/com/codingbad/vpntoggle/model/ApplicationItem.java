package com.codingbad.vpntoggle.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ayi on 7/19/15.
 * <p/>
 * Each item represents a set of applications that share the same UID.
 * The icon is the first one in a set of icons for now and the applications list is a comma
 * separated list of the applications' names.
 */
public class ApplicationItem implements Parcelable {
    public static final Parcelable.Creator<ApplicationItem> CREATOR
            = new Parcelable.Creator<ApplicationItem>() {
        public ApplicationItem createFromParcel(Parcel in) {
            return new ApplicationItem(in);
        }

        public ApplicationItem[] newArray(int size) {
            return new ApplicationItem[size];
        }
    };
    private String applicationName;
    private int uid;
    private String iconUri;
    private int state;

    public ApplicationItem(Uri icon, String applicationName, int uid) {
        this.applicationName = applicationName;
        this.uid = uid;
        if (icon != null) {
            this.iconUri = icon.toString();
        }

        state = StateEnum.THROUGH_VPN.toInt();
    }

    private ApplicationItem(Parcel in) {
        this.applicationName = in.readString();
        this.uid = in.readInt();
        this.iconUri = in.readString();
        this.state = in.readInt();
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Uri getIconUri() {
        if (this.iconUri == null) {
            return null;
        }

        return Uri.parse(this.iconUri);
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }

    public void addApplication(String appName) {
        this.applicationName = this.applicationName.concat(", " + appName);
    }

    public StateEnum getState() {
        return StateEnum.fromInt(state);
    }

    public void setState(StateEnum state) {
        this.state = state.toInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(applicationName);
        dest.writeInt(this.uid);
        dest.writeString(this.iconUri);
        dest.writeInt(state);
    }

    public int getUID() {
        return this.uid;
    }

    public enum StateEnum {
        THROUGH_VPN, AVOID_VPN, BLOCK;

        public static StateEnum fromInt(int number) {
            switch (number) {
                case 0:
                    return THROUGH_VPN;
                case 1:
                    return AVOID_VPN;
                case 2:
                    return BLOCK;
                default:
                    return THROUGH_VPN;
            }
        }

        public int toInt() {
            switch (this) {
                case THROUGH_VPN:
                    return 0;
                case AVOID_VPN:
                    return 1;
                case BLOCK:
                    return 2;
                default:
                    return -1;
            }
        }
    }
}
