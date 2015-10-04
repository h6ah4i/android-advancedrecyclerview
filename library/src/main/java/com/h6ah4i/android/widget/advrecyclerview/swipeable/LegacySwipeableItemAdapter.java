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

package com.h6ah4i.android.widget.advrecyclerview.swipeable;

import android.support.v7.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.annotation.SwipeableItemAfterReactions;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.annotation.SwipeableItemResults;

/**
 * This class is for easy migration from Advanced RecyclerView v0.7.x.
 * Just change {@link SwipeableItemAdapter} to {@link LegacySwipeableItemAdapter}.
 * However this class will be removed in future, so it's recommended to migrate to
 * the new {@link SwipeableItemAdapter} interface.
 *
 * @param <T> Type of the view holder class
 */
public interface LegacySwipeableItemAdapter<T extends RecyclerView.ViewHolder> extends BaseSwipeableItemAdapter<T> {

    /**
     * <p>Called when item is swiped.</p>
     * <p>*Note that do not change the data set and do not call notifyDataXXX() methods inside of this method.*</p>
     *
     * @param holder   The ViewHolder which is associated to the swiped item.
     * @param position The position of the item within the adapter's data set.
     * @param result   The result code of user's swipe operation.
     *                 {@link SwipeableItemConstants#RESULT_CANCELED},
     *                 {@link SwipeableItemConstants#RESULT_SWIPED_LEFT},
     *                 {@link SwipeableItemConstants#RESULT_SWIPED_UP},
     *                 {@link SwipeableItemConstants#RESULT_SWIPED_RIGHT} or
     *                 {@link SwipeableItemConstants#RESULT_SWIPED_DOWN}
     * @return Reaction type of after swiping.
     * One of the {@link SwipeableItemConstants#AFTER_SWIPE_REACTION_DEFAULT},
     * {@link SwipeableItemConstants#AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION} or
     * {@link SwipeableItemConstants#AFTER_SWIPE_REACTION_REMOVE_ITEM}.
     */
    @SwipeableItemAfterReactions
    int onSwipeItem(T holder, int position, int result);

    /**
     * <p>Called after {@link #onSwipeItem(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.</p>
     * <p>You can update the data set and call notifyDataXXX() methods inside of this method.</p>
     *
     * @param holder   The ViewHolder which is associated to the swiped item.
     * @param position The position of the item within the adapter's data set.
     * @param result   The result code of user's swipe operation.
     *                 {@link SwipeableItemConstants#RESULT_CANCELED},
     *                 {@link SwipeableItemConstants#RESULT_SWIPED_LEFT},
     *                 {@link SwipeableItemConstants#RESULT_SWIPED_UP},
     *                 {@link SwipeableItemConstants#RESULT_SWIPED_RIGHT} or
     *                 {@link SwipeableItemConstants#RESULT_SWIPED_DOWN}
     * @param reaction Reaction type. One of the {@link SwipeableItemConstants#AFTER_SWIPE_REACTION_DEFAULT},
     *                 {@link SwipeableItemConstants#AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION} or
     *                 {@link SwipeableItemConstants#AFTER_SWIPE_REACTION_REMOVE_ITEM}.
     */
    void onPerformAfterSwipeReaction(T holder, int position, @SwipeableItemResults int result, @SwipeableItemAfterReactions int reaction);
}
