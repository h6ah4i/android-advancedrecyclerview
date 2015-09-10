/*
*The MIT License (MIT)
*
*Copyright (c) 2015 Hieu Rocker
*
*Permission is hereby granted, free of charge, to any person obtaining a copy
*of this software and associated documentation files (the "Software"), to deal
*in the Software without restriction, including without limitation the rights
*to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
*copies of the Software, and to permit persons to whom the Software is
*furnished to do so, subject to the following conditions:
*
*The above copyright notice and this permission notice shall be included in all
*copies or substantial portions of the Software.
*
*THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
*AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
*OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
*SOFTWARE.
*/
package com.h6ah4i.android.widget.advrecyclerview.adapter;


import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.utils.BaseWrapperAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author rockerhieu on 8/11/15.
 */
public class StatesRecyclerViewAdapter extends BaseWrapperAdapter<RecyclerView.ViewHolder> {
    private final View vLoadingView;
    private final View vEmptyView;
    private final View vErrorView;

    @IntDef({STATE_NORMAL, STATE_LOADING, STATE_EMPTY, STATE_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public static final int STATE_NORMAL = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_EMPTY = 2;
    public static final int STATE_ERROR = 3;

    public static final int TYPE_LOADING = 1000;
    public static final int TYPE_EMPTY = 1001;
    public static final int TYPE_ERROR = 1002;
    @State
    private int state = STATE_NORMAL;

    public StatesRecyclerViewAdapter(@NonNull RecyclerView.Adapter wrapped, @Nullable View loadingView, @Nullable View emptyView, @Nullable View errorView) {
        super(wrapped);
        this.vLoadingView = loadingView;
        this.vEmptyView = emptyView;
        this.vErrorView = errorView;
    }

    @State
    public int getState() {
        return state;
    }

    public void setState(@State int state) {
        this.state = state;
        getWrappedAdapter().notifyDataSetChanged();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        switch (state) {
            case STATE_LOADING:
            case STATE_EMPTY:
            case STATE_ERROR:
                return 1;
        }
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        switch (state) {
            case STATE_LOADING:
                return TYPE_LOADING;
            case STATE_EMPTY:
                return TYPE_EMPTY;
            case STATE_ERROR:
                return TYPE_ERROR;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_LOADING:
                return new SimpleViewHolder(vLoadingView);
            case TYPE_EMPTY:
                return new SimpleViewHolder(vEmptyView);
            case TYPE_ERROR:
                return new SimpleViewHolder(vErrorView);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (state) {
            case STATE_LOADING:
                onBindLoadingViewHolder(holder, position);
                break;
            case STATE_EMPTY:
                onBindEmptyViewHolder(holder, position);
                break;
            case STATE_ERROR:
                onBindErrorViewHolder(holder, position);
                break;
            default:
                super.onBindViewHolder(holder, position);
                break;
        }
    }

    public void onBindErrorViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    public void onBindEmptyViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    public void onBindLoadingViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }
}