package com.codingbad.vpntoggle.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.codingbad.vpntoggle.activity.R;
import com.codingbad.vpntoggle.model.ApplicationItem;
import com.codingbad.vpntoggle.view.ApplicationItemView;

/**
 * Created by ayi on 8/6/15.
 */
public class ApplicationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ApplicationItemView applicationItemView;
    private final CheckBox checkbox;
    private ApplicationItem applicationItem;

    public ApplicationViewHolder(ApplicationItemView itemView) {
        super(itemView);
        this.applicationItemView = itemView;
        this.applicationItemView.setOnClickListener(this);
        this.checkbox = (CheckBox) itemView.findViewById(R.id.item_checkbox);
    }

    @Override
    public void onClick(View v) {
        // TODO: decide what to do when an item is clicked
        // recyclerViewListener.onItemClickListener(v, getAdapterPosition());
    }

    public void bind(ApplicationItem applicationItem) {
        this.applicationItem = applicationItem;
        this.checkbox.setChecked(applicationItem.isSelected());

        applicationItemView.fill(applicationItem.getApplicationsList(), applicationItem.getIconUri());
    }

    public View getApplicationItemView() {
        return applicationItemView;
    }
}
