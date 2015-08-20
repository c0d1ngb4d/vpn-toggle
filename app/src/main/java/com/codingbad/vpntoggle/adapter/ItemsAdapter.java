/*
* Copyright (C) 2015 Ayelen Chavez y Joaquín Rinaudo
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* Contributors:
*
* Ayelen Chavez ashy.on.line@gmail.com
* Joaquin Rinaudo jmrinaudo@gmail.com
*
*/
package com.codingbad.vpntoggle.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.codingbad.vpntoggle.holder.ApplicationViewHolder;
import com.codingbad.vpntoggle.model.ApplicationItem;
import com.codingbad.vpntoggle.view.ApplicationItemView;

import java.util.ArrayList;
import java.util.List;


public class ItemsAdapter extends RecyclerView.Adapter<ApplicationViewHolder> {

    private List<ApplicationItem> applicationList;
    private int lastPosition = -1;

    public ItemsAdapter() {
        this.applicationList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public ApplicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ApplicationItemView view = new ApplicationItemView(parent.getContext());

        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ApplicationViewHolder holder, int position) {
        final ApplicationItem applicationItem = applicationList.get(position);

        if (position > lastPosition) {
            for (Animator anim : getAnimators(holder.getApplicationItemView())) {
                anim.setDuration(500).start();
                anim.setInterpolator(new LinearInterpolator());
            }

            lastPosition = position;
        }

        holder.bind(applicationItem);
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
}
