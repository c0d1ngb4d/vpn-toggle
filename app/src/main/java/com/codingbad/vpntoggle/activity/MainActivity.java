package com.codingbad.vpntoggle.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.codingbad.library.activity.AbstractSideBarActivity;
import com.codingbad.library.utils.ComplexSharedPreference;
import com.codingbad.vpntoggle.R;
import com.codingbad.vpntoggle.fragment.ApplicationsListFragment;
import com.codingbad.vpntoggle.model.ApplicationItem;
import com.codingbad.vpntoggle.model.ListOfApplicationItems;
import com.codingbad.vpntoggle.receiver.NetworkChangeReceiver;
import com.codingbad.vpntoggle.service.NetworkManagerIntentService;

import java.util.List;

public class MainActivity extends AbstractSideBarActivity implements ApplicationsListFragment.Callbacks {

    private static final String APPLICATIONS = "applications";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    @Override
    protected void setInitialFragment() {
        setInitialFragment(ApplicationsListFragment.newInstance());
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChangesApplied(List<ApplicationItem> applicationItems) {
        //start broadcast receiver to update changes
        NetworkManagerIntentService.startActionRefresh(this);
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
