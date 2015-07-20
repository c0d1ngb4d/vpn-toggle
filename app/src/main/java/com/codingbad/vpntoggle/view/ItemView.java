package com.codingbad.vpntoggle.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codingbad.vpntoggle.activity.R;
import com.codingbad.vpntoggle.util.ViewUtil;

import roboguice.inject.InjectView;


public class ItemView extends LinearLayout {

    @InjectView(R.id.item_view_icon)
    private ImageView icon;

    @InjectView(R.id.item_view_text)
    private TextView label;

    public ItemView(Context context) {
        super(context);
        init();
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_view, this);
        ViewUtil.reallyInjectViews(this);
    }

    public void fill(String text, Uri iconUri) {
        label.setText(text);

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
