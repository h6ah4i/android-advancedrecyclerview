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

package com.h6ah4i.android.widget.advrecyclerview.draggable;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.utils.CustomRecyclerViewUtils;

public class DraggingItemInfo {
    public final int width;
    public final int height;
    public final long id;
    public final int initialItemLeft;
    public final int initialItemTop;
    public final int grabbedPositionX;
    public final int grabbedPositionY;
    public final Rect margins;
    public final int spanSize;

    public DraggingItemInfo(RecyclerView rv, RecyclerView.ViewHolder vh, int touchX, int touchY) {
        width = vh.itemView.getWidth();
        height = vh.itemView.getHeight();
        id = vh.getItemId();
        initialItemLeft = vh.itemView.getLeft();
        initialItemTop = vh.itemView.getTop();
        grabbedPositionX = touchX - initialItemLeft;
        grabbedPositionY = touchY - initialItemTop;
        margins = new Rect();
        CustomRecyclerViewUtils.getLayoutMargins(vh.itemView, margins);
        spanSize = CustomRecyclerViewUtils.getSpanSize(vh);
    }
}
