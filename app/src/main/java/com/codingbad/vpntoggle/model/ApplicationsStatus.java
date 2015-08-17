package com.codingbad.vpntoggle.model;

import com.codingbad.library.utils.ArrayUtils;

import java.util.List;

/**
 * Created by ayi on 8/17/15.
 */
public class ApplicationsStatus {
    List<ApplicationItem> applicationItems;
    List<ApplicationItem> prevStatus;
    List<ApplicationItem> prevPrevStatus;

    public ApplicationsStatus(List<ApplicationItem> items) {
        applicationItems = items;
        prevStatus = ArrayUtils.copyFrom(items);
        prevPrevStatus = null;
    }

    public List<ApplicationItem> getApplicationItems() {
        return applicationItems;
    }

    public List<ApplicationItem> undo() {
        applicationItems = ArrayUtils.copyFrom(prevPrevStatus);
        prevStatus = prevPrevStatus;
        prevPrevStatus = null;
        return applicationItems;
    }

    public void apply() {
        prevPrevStatus = prevStatus;
        prevStatus = ArrayUtils.copyFrom(applicationItems);
    }
}
