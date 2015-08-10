package com.codingbad.vpntoggle.model;

import android.net.Uri;

/**
 * Created by ayi on 7/19/15.
 *
 * Each item represents a set of applications that share the same UID.
 * The icon is the first one in a set of icons for now and the applications list is a comma
 * separated list of the applications' names.
 */
public class ApplicationItem {
    private String applicationName;
    private Uri iconUri;
    private boolean isSelected = false;

    public ApplicationItem(Uri icon, String applicationsList) {
        this.applicationName = applicationsList;
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

    // TODO: check from saved data
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelection(boolean selected) {
        isSelected = selected;
    }
}
