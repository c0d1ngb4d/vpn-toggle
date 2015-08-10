package com.codingbad.vpntoggle.holder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.codingbad.vpntoggle.activity.R;
import com.codingbad.vpntoggle.model.ApplicationItem;
import com.codingbad.vpntoggle.view.ApplicationItemView;


/**
 * Created by ayi on 8/6/15.
 */
public class ApplicationViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

    private final ApplicationItemView applicationItemView;
    private final CheckBox checkbox;
    private ApplicationItem applicationItem;

    public ApplicationViewHolder(ApplicationItemView itemView) {
        super(itemView);
        this.applicationItemView = itemView;
        this.checkbox = (CheckBox) itemView.findViewById(R.id.item_checkbox);
    }

    public void bind(ApplicationItem applicationItem) {
        this.applicationItem = applicationItem;
        this.checkbox.setChecked(applicationItem.isSelected());

        applicationItemView.fill(applicationItem.getApplicationsList(), applicationItem.getIconUri());
        checkbox.setOnCheckedChangeListener(this);
    }

    public View getApplicationItemView() {
        return applicationItemView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        this.applicationItem.setSelection(isChecked);
    }
}
