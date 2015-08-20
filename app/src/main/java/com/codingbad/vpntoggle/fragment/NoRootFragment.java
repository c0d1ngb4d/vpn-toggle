package com.codingbad.vpntoggle.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codingbad.library.fragment.AbstractFragment;
import com.codingbad.vpntoggle.R;

/**
 * Created by ayi on 8/19/15.
 */
public class NoRootFragment extends AbstractFragment<NoRootFragment.Callbacks> {
    public class Callbacks {
    }

    public static Fragment newInstance() {
        return new NoRootFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noroot, container, false);
    }
}