package com.codingbad.vpntoggle.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;


import com.codingbad.vpntoggle.model.ApplicationItem;
import com.codingbad.vpntoggle.view.ItemView;

import java.util.ArrayList;
import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<ApplicationItem> applicationList;
    private RecyclerViewListener recyclerViewListener;
    private int lastPosition = -1;

    public ItemAdapter(RecyclerViewListener recyclerViewListener) {
        this.recyclerViewListener = recyclerViewListener;
        this.applicationList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemView view = new ItemView(parent.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ApplicationItem applicationItem = applicationList.get(position);


        if (position > lastPosition) {
            for (Animator anim : getAnimators(holder.itemView)) {
                anim.setDuration(500).start();
                anim.setInterpolator(new LinearInterpolator());
            }
//            Animator anim = getAnimator(holder.itemView);
//            anim.setDuration(500).start();
//            anim.setInterpolator(new LinearInterpolator());
            lastPosition = position;
        }
        holder.itemView.fill(applicationItem.getApplicationsList(), applicationItem.getIconUri());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // nothing to be done on click
            }
        });
    }

    public void addItem(ApplicationItem applicationItem) {
        this.applicationList.add(applicationItem);
        notifyItemInserted(getItemCount());
    }

    public void addItemList(List<ApplicationItem> applicationItems) {
        this.applicationList.addAll(applicationItems);
        notifyDataSetChanged();
    }

    public void removeAll() {
        this.applicationList.clear();
        notifyDataSetChanged();
    }

    public void removeItemAt(int position) {
        this.applicationList.remove(position);
        notifyItemRemoved(position);
    }

    public ApplicationItem getItemAtPosition(int position) {
        return this.applicationList.get(position);
    }

    @Override
    public int getItemCount() {
        return this.applicationList.size();
    }

    protected Animator[] getAnimators(View view) {
        return new Animator[]{
                ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight(), 0),
                getAlphaAnimator(view)
        };
    }

    protected Animator getAnimator(View view) {
//        return ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight(), 0);
        return getAlphaAnimator(view);
    }

    protected Animator getAlphaAnimator(final View view) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);
        return anim;
    }

    public interface RecyclerViewListener {
        void onItemClickListener(View view, int position);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ItemView itemView;

        public ViewHolder(ItemView itemView) {
            super(itemView);
            this.itemView = itemView;
            this.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerViewListener.onItemClickListener(v, getAdapterPosition());
        }
    }
}
