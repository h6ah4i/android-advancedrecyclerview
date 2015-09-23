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

package com.h6ah4i.android.widget.advrecyclerview.expandable;

import android.support.v7.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;

public interface ExpandableSwipeableItemAdapter<GVH extends RecyclerView.ViewHolder, CVH extends RecyclerView.ViewHolder>
    extends BaseExpandableSwipeableItemAdapter<GVH, CVH> {

    /**
     * Called when group item is swiped.
     *
     * *Note that do not change data set and do not call notifyDataXXX() methods inside of this method.*
     *
     * @param holder The ViewHolder which is associated to the swiped item.
     * @param groupPosition Group position.
     * @param result The result code of user's swipe operation.
     *              {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_CANCELED},
     *              {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_LEFT},
     *              {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_UP},
     *              {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_RIGHT} or
     *              {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_DOWN}
     *
     * @return Reaction type of after swiping.
     *          One of the {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_DEFAULT},
     *          {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION} or
     *          {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_REMOVE_ITEM}.
     */
    SwipeResultAction onSwipeGroupItem(GVH holder, int groupPosition, int result);

    /**
     * Called when child item is swiped.
     *
     * *Note that do not change data set and do not call notifyDataXXX() methods inside of this method.*
     *
     * @param holder The ViewHolder which is associated to the swiped item.
     * @param groupPosition Group position.
     * @param childPosition Child position.
     * @param result The result code of user's swipe operation.
     *              {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_CANCELED},
     *              {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_LEFT},
     *              {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_UP},
     *              {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_RIGHT} or
     *              {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_DOWN}
     *
     * @return Reaction type of after swiping.
     *          One of the {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_DEFAULT},
     *          {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION} or
     *          {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_REMOVE_ITEM}.
     */
    SwipeResultAction onSwipeChildItem(CVH holder, int groupPosition, int childPosition, int result);
}
