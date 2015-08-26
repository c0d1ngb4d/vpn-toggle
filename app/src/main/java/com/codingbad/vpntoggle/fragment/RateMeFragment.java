package com.codingbad.vpntoggle.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codingbad.library.fragment.AbstractFragment;
import com.codingbad.vpntoggle.R;

/**
 * Created by ayi on 8/26/15.
 */
public class RateMeFragment extends AbstractFragment<AboutFragment.Callbacks> {
    public class Callbacks {
    }

    public static Fragment newInstance() {
        return new RateMeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rate_me, container, false);
    }
}
