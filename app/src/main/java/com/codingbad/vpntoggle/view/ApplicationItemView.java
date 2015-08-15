package com.codingbad.vpntoggle.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codingbad.library.view.ThreeStatesButton;
import com.codingbad.vpntoggle.R;
import com.codingbad.vpntoggle.model.ApplicationItem;
import com.codingbad.vpntoggle.util.ViewUtil;

import roboguice.inject.InjectView;

public class ApplicationItemView extends LinearLayout {

    @InjectView(R.id.item_view_icon)
    private ImageView icon;

    @InjectView(R.id.item_button)
    private ThreeStatesButton stateButton;

    @InjectView(R.id.item_text)
    private TextView textButton;

    public ApplicationItemView(Context context) {
        super(context);
        init();
    }

    public ApplicationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ApplicationItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_view, this);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ViewUtil.reallyInjectViews(this);
    }

    public void fill(String text, Uri iconUri, ApplicationItem.StateEnum state) {
        textButton.setText(text);

        stateButton.setState(state.toInt());
        int size = getResources().getDimensionPixelSize(R.dimen.icon_size);

        Glide.with(getContext())
                .load(iconUri)
                .centerCrop()
                .crossFade()
                .override(size, size)
                .placeholder(R.drawable.ic_thumbnail)
                .into(this.icon);
    }
}
