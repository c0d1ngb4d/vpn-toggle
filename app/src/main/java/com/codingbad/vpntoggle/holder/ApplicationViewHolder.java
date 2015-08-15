package com.codingbad.vpntoggle.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.codingbad.library.view.ThreeStatesButton;
import com.codingbad.vpntoggle.R;
import com.codingbad.vpntoggle.model.ApplicationItem;
import com.codingbad.vpntoggle.view.ApplicationItemView;


/**
 * Created by ayi on 8/6/15.
 */
public class ApplicationViewHolder extends RecyclerView.ViewHolder implements ThreeStatesButton.StateListener {

    private final ApplicationItemView applicationItemView;
    private final ThreeStatesButton threeStatesButton;
    private ApplicationItem applicationItem;

    public ApplicationViewHolder(ApplicationItemView itemView) {
        super(itemView);
        this.applicationItemView = itemView;
        this.threeStatesButton = (ThreeStatesButton) itemView.findViewById(R.id.item_button);
    }

    public void bind(ApplicationItem applicationItem) {
        this.applicationItem = applicationItem;

        applicationItemView.fill(applicationItem.getApplicationName(), applicationItem.getIconUri(), applicationItem.getState());
        threeStatesButton.setStateListener(this);
    }

    public View getApplicationItemView() {
        return applicationItemView;
    }

    @Override
    public void onState1() {
        this.applicationItem.setState(ApplicationItem.StateEnum.THROUGH_VPN);
    }

    @Override
    public void onState2() {
        this.applicationItem.setState(ApplicationItem.StateEnum.AVOID_VPN);
    }

    @Override
    public void onState3() {
        this.applicationItem.setState(ApplicationItem.StateEnum.BLOCK);
    }
}
