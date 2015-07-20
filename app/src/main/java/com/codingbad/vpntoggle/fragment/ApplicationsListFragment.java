package com.codingbad.vpntoggle.fragment;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codingbad.library.fragment.AbstractFragment;
import com.codingbad.vpntoggle.activity.R;
import com.codingbad.vpntoggle.adapter.ItemAdapter;
import com.codingbad.vpntoggle.model.ApplicationItem;

import java.security.Permission;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import roboguice.inject.InjectView;

/**
 * Created by ayi on 6/26/15.
 */
public class ApplicationsListFragment extends AbstractFragment<ApplicationsListFragment.Callbacks> implements ItemAdapter.RecyclerViewListener {

    @InjectView(R.id.fragment_list_recyclerview)
    private RecyclerView recyclerView;
    private ItemAdapter adapter;

    public static ApplicationsListFragment newInstance() {
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

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new FadeInAnimator());

        adapter = new ItemAdapter(this);
        getDeviceApplications();
        recyclerView.setAdapter(adapter);
    }

    private void getDeviceApplications() {
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
                    applicationItemMap.put(appUid, new ApplicationItem(appIconUri, appName));
                }
            }
        }

        for (ApplicationItem item : applicationItemMap.values()) {
            this.adapter.addItem(item);
        }

    }

    @Override
    public void onItemClickListener(View view, int position) {

    }

    public interface Callbacks {
    }
}
