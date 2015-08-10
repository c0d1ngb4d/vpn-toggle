package com.codingbad.vpntoggle.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.codingbad.vpntoggle.activity.R;
import com.codingbad.vpntoggle.util.ViewUtil;

import roboguice.inject.InjectView;



public class ApplicationItemView extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

    @InjectView(R.id.item_view_icon)
    private ImageView icon;

    @InjectView(R.id.item_checkbox)
    private CheckBox checkBox;

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

    public void fill(String text, Uri iconUri) {
        checkBox.setText(text);

        checkBox.setOnCheckedChangeListener(this);
        int size = getResources().getDimensionPixelSize(R.dimen.icon_size);

        Glide.with(getContext())
                .load(iconUri)
                .centerCrop()
                .crossFade()
                .override(size, size)
                .placeholder(R.drawable.ic_thumbnail)
                .into(this.icon);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LinearLayout parent = (LinearLayout) buttonView.getParent();
        if (isChecked) {
            parent.setBackgroundColor(getResources().getColor(R.color.checked));
        } else {
            parent.setBackgroundColor(getResources().getColor(R.color.primary_light));
        }
    }
}
