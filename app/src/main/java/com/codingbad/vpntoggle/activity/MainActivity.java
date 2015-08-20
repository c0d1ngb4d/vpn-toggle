package com.codingbad.vpntoggle.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.codingbad.library.activity.AbstractSideBarActivity;
import com.codingbad.library.utils.ComplexSharedPreference;
import com.codingbad.vpntoggle.R;
import com.codingbad.vpntoggle.fragment.AboutFragment;
import com.codingbad.vpntoggle.fragment.ApplicationsListFragment;
import com.codingbad.vpntoggle.fragment.HowToFragment;
import com.codingbad.vpntoggle.fragment.NoRootFragment;
import com.codingbad.vpntoggle.model.ApplicationItem;
import com.codingbad.vpntoggle.model.ListOfApplicationItems;
import com.codingbad.vpntoggle.service.NetworkManagerIntentService;

import java.util.List;

public class MainActivity extends AbstractSideBarActivity implements ApplicationsListFragment.Callbacks {

    private static final String APPLICATIONS = "applications";
    private static boolean isSuAvailable = NetworkManagerIntentService.isSUAvailable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    @Override
    protected void setInitialFragment() {
        setInitialFragment(getInitialFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch (id) {
            case R.id.action_about:
                replaceFragment(AboutFragment.newInstance());
                break;
            case R.id.action_howto:
                replaceFragment(HowToFragment.newInstance());
                break;
            case R.id.action_main:
                replaceFragment(getInitialFragment());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private Fragment getInitialFragment() {
        if (isSuAvailable) {
            ApplicationsListFragment.newInstance();
        }

        return NoRootFragment.newInstance();
    }

    @Override
    public void onChangesApplied(List<ApplicationItem> applicationItems) {
        // save current state of applications
        saveApplications(applicationItems);

        // start broadcast receiver to update changes
        NetworkManagerIntentService.startActionRefresh(this);
    }

    private void saveApplications(List<ApplicationItem> applicationItems) {
        ListOfApplicationItems listOfApplicationItems = new ListOfApplicationItems();
        listOfApplicationItems.applicationItems = applicationItems;
        ComplexSharedPreference.write(this, listOfApplicationItems, APPLICATIONS);
    }

    @Override
    public List<ApplicationItem> getApplicationsSavedStatus() {
        ListOfApplicationItems items = ComplexSharedPreference.read(this, APPLICATIONS, ListOfApplicationItems.class);
        if (items == null) {
            return null;
        }
        return items.applicationItems;
    }
}
