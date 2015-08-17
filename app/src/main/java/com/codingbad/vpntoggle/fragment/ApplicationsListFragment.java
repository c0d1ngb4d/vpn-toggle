package com.codingbad.vpntoggle.fragment;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codingbad.library.fragment.AbstractFragment;
import com.codingbad.vpntoggle.R;
import com.codingbad.vpntoggle.adapter.ItemsAdapter;
import com.codingbad.vpntoggle.model.ApplicationItem;
import com.codingbad.vpntoggle.model.ApplicationsStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import roboguice.inject.InjectView;

/**
 * Created by ayi on 6/26/15.
 */
public class ApplicationsListFragment extends AbstractFragment<ApplicationsListFragment.Callbacks> implements View.OnClickListener {

    private static final String LIST_STATE = "listState";
    @InjectView(R.id.fragment_list_recyclerview)
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ApplicationsStatus applications;

    public static Fragment newInstance() {
        return new ApplicationsListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_applicationslist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_apply) {
            applications.apply();
            callbacks.onChangesApplied(applications.getApplicationItems());
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Changes has been applied", Snackbar.LENGTH_LONG)
                    .setAction("Undo", this)
                    .setActionTextColor(getResources().getColor(R.color.accent))
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (applications == null) {
            List<ApplicationItem> items = callbacks.getApplicationsSavedStatus();
            if (items == null) {
                items = getDeviceApplications();
            }

            applications = new ApplicationsStatus(items);
        }

        ItemsAdapter adapter = new ItemsAdapter();
        adapter.addItemList(applications.getApplicationItems());
        recyclerView.setAdapter(adapter);
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        // set layout manager which positions items in the screen
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // ItemAnimator animates views
        recyclerView.setItemAnimator(new FadeInAnimator());
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<ApplicationItem> list = savedInstanceState.getParcelableArrayList(LIST_STATE);
            applications = new ApplicationsStatus(list);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (applications != null) {
            outState.putParcelableArrayList(LIST_STATE, (ArrayList<? extends Parcelable>) applications.getApplicationItems());
        }
    }sta

    private ArrayList<ApplicationItem> getDeviceApplications() {
        final PackageManager packageManager = getActivity().getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        Map<Integer, ApplicationItem> applicationItemMap = new HashMap<Integer, ApplicationItem>();
        for (ApplicationInfo applicationInfo : installedApplications) {
            if (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(Manifest.permission.INTERNET, applicationInfo.packageName)) {
                String appName = (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "(unknown)");
                Uri appIconUri = null;

                if (applicationInfo.icon != 0) {
                    appIconUri = Uri.parse("android.resource://" + applicationInfo.packageName + "/" + applicationInfo.icon);
                }

                int appUid = applicationInfo.uid;

                if (applicationItemMap.containsKey(appUid)) {
                    ApplicationItem applicationItem = applicationItemMap.get(appUid);
                    applicationItem.addApplication(appName);
                    applicationItemMap.put(appUid, applicationItem);
                } else {
                    applicationItemMap.put(appUid, new ApplicationItem(appIconUri, appName, appUid));
                }
            }
        }

        return new ArrayList<ApplicationItem>(applicationItemMap.values());
    }

    @Override
    public void onClick(View v) {
        // UNDO apply
        callbacks.onChangesApplied(applications.undo());
        ItemsAdapter adapter = new ItemsAdapter();
        adapter.addItemList(applications.getApplicationItems());
        recyclerView.setAdapter(adapter);
    }

    public interface Callbacks {
        void onChangesApplied(List<ApplicationItem> applicationItems);

        List<ApplicationItem> getApplicationsSavedStatus();
    }
}
