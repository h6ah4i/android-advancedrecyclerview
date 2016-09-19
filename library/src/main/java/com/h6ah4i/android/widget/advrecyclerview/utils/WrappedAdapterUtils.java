/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.h6ah4i.android.widget.advrecyclerview.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.adapter.WrappedAdapter;
import com.h6ah4i.android.widget.advrecyclerview.adapter.WrapperAdapter;

public class WrappedAdapterUtils {
    private WrappedAdapterUtils() {
    }

    @SuppressWarnings("unchecked")
    public static void invokeOnViewRecycled(@NonNull RecyclerView.Adapter adapter, @NonNull RecyclerView.ViewHolder holder, int viewType) {
        if (adapter instanceof WrapperAdapter) {
            ((WrapperAdapter) adapter).onViewRecycled(holder, viewType);
        } else {
            adapter.onViewRecycled(holder);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean invokeOnFailedToRecycleView(@NonNull RecyclerView.Adapter adapter, @NonNull RecyclerView.ViewHolder holder, int viewType) {
        if (adapter instanceof WrappedAdapter) {
            return ((WrappedAdapter) adapter).onFailedToRecycleView(holder, viewType);
        } else {
            return adapter.onFailedToRecycleView(holder);
        }
    }

    @SuppressWarnings("unchecked")
    public static void invokeOnViewAttachedToWindow(@NonNull RecyclerView.Adapter adapter, @NonNull RecyclerView.ViewHolder holder, int viewType) {
        if (adapter instanceof WrappedAdapter) {
            ((WrappedAdapter) adapter).onViewAttachedToWindow(holder, viewType);
        } else {
            adapter.onViewAttachedToWindow(holder);
        }
    }

    @SuppressWarnings("unchecked")
    public static void invokeOnViewDetachedFromWindow(RecyclerView.Adapter adapter, RecyclerView.ViewHolder holder, int viewType) {
        if (adapter instanceof WrappedAdapter) {
            ((WrappedAdapter) adapter).onViewDetachedFromWindow(holder, viewType);
        } else {
            adapter.onViewDetachedFromWindow(holder);
        }
    }
}
