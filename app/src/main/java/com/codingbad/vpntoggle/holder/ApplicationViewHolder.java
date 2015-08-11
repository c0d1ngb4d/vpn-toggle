package com.codingbad.vpntoggle.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.codingbad.library.view.ThreeStatesButton;
import com.codingbad.vpntoggle.activity.R;
import com.codingbad.vpntoggle.model.ApplicationItem;
import com.codingbad.vpntoggle.view.ApplicationItemView;


/**
 * Created by ayi on 8/6/15.
 */
public class ApplicationViewHolder extends RecyclerView.ViewHolder implements ThreeStatesButton.StateListener {

    private final ApplicationItemView applicationItemView;
    private final ThreeStatesButton checkbox;
    private ApplicationItem applicationItem;

    public ApplicationViewHolder(ApplicationItemView itemView) {
        super(itemView);
        this.applicationItemView = itemView;
        this.checkbox = (ThreeStatesButton) itemView.findViewById(R.id.item_button);
    }

    public void bind(ApplicationItem applicationItem) {
        this.applicationItem = applicationItem;

        applicationItemView.fill(applicationItem.getApplicationName(), applicationItem.getIconUri(), applicationItem.getState());
        checkbox.setStateListener(this);
    }

    public View getApplicationItemView() {
        return applicationItemView;
    }

    @Override
    public void onAutomatic() {
        this.applicationItem.setState(ThreeStatesButton.StateEnum.AUTOMATIC);
    }

    @Override
    public void onOn() {
        this.applicationItem.setState(ThreeStatesButton.StateEnum.ON);
    }

    @Override
    public void onOff() {
        this.applicationItem.setState(ThreeStatesButton.StateEnum.OFF);
    }
}
