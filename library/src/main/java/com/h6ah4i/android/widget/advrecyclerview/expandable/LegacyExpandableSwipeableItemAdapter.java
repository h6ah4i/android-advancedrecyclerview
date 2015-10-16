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

public interface LegacyExpandableSwipeableItemAdapter<GVH extends RecyclerView.ViewHolder, CVH extends RecyclerView.ViewHolder>
        extends BaseExpandableSwipeableItemAdapter<GVH, CVH> {

    /**
     * <p>Called when group item is swiped.</p>
     * <p>*Note that do not change data set and do not call notifyDataXXX() methods inside of this method.*</p>
     *
     * @param holder        The ViewHolder which is associated to the swiped item.
     * @param groupPosition Group position.
     * @param result        The result code of user's swipe operation.
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_CANCELED},
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_LEFT},
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_UP},
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_RIGHT} or
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_DOWN}
     * @return Reaction type of after swiping.
     * One of the {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_DEFAULT},
     * {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION} or
     * {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_REMOVE_ITEM}.
     */
    int onSwipeGroupItem(GVH holder, int groupPosition, int result);

    /**
     * <p>Called when child item is swiped.</p>
     * <p>*Note that do not change data set and do not call notifyDataXXX() methods inside of this method.*</p>
     *
     * @param holder        The ViewHolder which is associated to the swiped item.
     * @param groupPosition Group position.
     * @param childPosition Child position.
     * @param result        The result code of user's swipe operation.
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_CANCELED},
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_LEFT},
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_UP},
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_RIGHT} or
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_DOWN}
     * @return Reaction type of after swiping.
     * One of the {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_DEFAULT},
     * {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION} or
     * {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_REMOVE_ITEM}.
     */
    int onSwipeChildItem(CVH holder, int groupPosition, int childPosition, int result);

    /**
     * <p>Called after {@link #onSwipeGroupItem(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.</p>
     * <p>You can update data set and call notifyDataXXX() methods inside of this method.</p>
     *
     * @param holder        The ViewHolder which is associated to the swiped item.
     * @param groupPosition Group position.
     * @param result        The result code of user's swipe operation.
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_CANCELED},
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_LEFT},
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_UP},
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_RIGHT} or
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_DOWN}
     * @param reaction      Reaction type. One of the {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_DEFAULT},
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION} or
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_REMOVE_ITEM}.
     */
    void onPerformAfterSwipeGroupReaction(GVH holder, int groupPosition, int result, int reaction);


    /**
     * <p>Called after {@link #onSwipeChildItem(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>You can update data set and call notifyDataXXX() methods inside of this method.</p>
     *
     * @param holder        The ViewHolder which is associated to the swiped item.
     * @param groupPosition Group position.
     * @param childPosition Child position.
     * @param result        The result code of user's swipe operation.
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_CANCELED},
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_LEFT},
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_UP},
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_RIGHT} or
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#RESULT_SWIPED_DOWN}
     * @param reaction      Reaction type. One of the {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_DEFAULT},
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION} or
     *                      {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager#AFTER_SWIPE_REACTION_REMOVE_ITEM}.
     */
    void onPerformAfterSwipeChildReaction(CVH holder, int groupPosition, int childPosition, int result, int reaction);
}
