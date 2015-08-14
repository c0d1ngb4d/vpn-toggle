package com.codingbad.vpntoggle.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.codingbad.library.activity.AbstractSideBarActivity;
import com.codingbad.vpntoggle.fragment.ApplicationsListFragment;

import task.Startup;

public class MainActivity extends AbstractSideBarActivity implements ApplicationsListFragment.Callbacks {

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
            (new Startup()).setContext(this).execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
