/*
 *    Copyright (C) 2016 Haruki Hasegawa
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
package com.h6ah4i.android.widget.advrecyclerview.composedadapter;

import androidx.recyclerview.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.adapter.BridgeAdapterDataObserver;

import java.util.ArrayList;
import java.util.List;

class ComposedChildAdapterDataObserver extends BridgeAdapterDataObserver {
    public ComposedChildAdapterDataObserver(Subscriber subscriber, RecyclerView.Adapter sourceAdapter) {
        super(subscriber, sourceAdapter, new ArrayList<ComposedChildAdapterTag>());
    }

    @SuppressWarnings("unchecked")
    private List<ComposedChildAdapterTag> getChildAdapterTags() {
        return (List<ComposedChildAdapterTag>) getTag();
    }

    public void registerChildAdapterTag(ComposedChildAdapterTag tag) {
        getChildAdapterTags().add(tag);
    }

    public void unregisterChildAdapterTag(ComposedChildAdapterTag tag) {
        getChildAdapterTags().remove(tag);
    }

    public boolean hasChildAdapters() {
        return !getChildAdapterTags().isEmpty();
    }

    public void release() {
        getChildAdapterTags().clear();
    }
}
