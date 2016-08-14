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
package com.h6ah4i.android.widget.advrecyclerview.animator;

import android.support.v7.widget.RecyclerView;

/**
 * ItemAnimator for Draggable item. This animator is required to work animations properly on drop an item.
 */
public class DraggableItemAnimator extends RefactoredDefaultItemAnimator {

    @Override
    protected void onSetup() {
        super.onSetup();
        super.setSupportsChangeAnimations(false);
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        if (oldHolder == newHolder && fromX == toX && fromY == toY) {
            // WORKAROUND: Skip animateChange() for the dropped item. Should be implemented better approach.
            dispatchChangeFinished(oldHolder, true);
            return false;
        }

        return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);
    }
}
