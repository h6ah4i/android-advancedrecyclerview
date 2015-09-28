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

public interface SwipeableItemConstants {
    /**
     * State flag for the {@link SwipeableItemViewHolder#setSwipeStateFlags(int)} and {@link SwipeableItemViewHolder#getSwipeStateFlags()} methods.
     * Indicates that currently performing swiping.
     */
    int STATE_FLAG_SWIPING = (1 << 0);

    /**
     * State flag for the {@link SwipeableItemViewHolder#setSwipeStateFlags(int)} and {@link SwipeableItemViewHolder#getSwipeStateFlags()} methods.
     * Indicates that this item is being swiped.
     */
    int STATE_FLAG_IS_ACTIVE = (1 << 1);

    /**
     * State flag for the {@link SwipeableItemViewHolder#setSwipeStateFlags(int)} and {@link SwipeableItemViewHolder#getSwipeStateFlags()} methods.
     * If this flag is set, some other flags are changed and require to apply.
     */
    int STATE_FLAG_IS_UPDATED = (1 << 31);


    /**
     * Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * None. (internal default value, this value is not used for the argument)
     */
    int RESULT_NONE = 0;

    /**
     * Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Canceled.
     */
    int RESULT_CANCELED = 1;

    /**
     * Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Swipe left performed.
     */
    int RESULT_SWIPED_LEFT = 2;

    /**
     * Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Swipe up performed.
     */
    int RESULT_SWIPED_UP = 3;

    /**
     * Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Swipe right performed.
     */
    int RESULT_SWIPED_RIGHT = 4;

    /**
     * Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Swipe down performed.
     */
    int RESULT_SWIPED_DOWN = 5;

    // ---
    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe any direction"
     */
    int REACTION_CAN_NOT_SWIPE_ANY = 0;

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe left" (completely no reactions)
     */
    int REACTION_CAN_NOT_SWIPE_LEFT = (0 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_LEFT);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe left"  (not swipeable, but rubber-band effect applied)
     */
    int REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT = (1 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_LEFT);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can swipe left"
     */
    int REACTION_CAN_SWIPE_LEFT = (2 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_LEFT);

    /**
     * Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * If this flag is specified, swipe operation will not start even if user swipe an item to the LEFT direction.
     * <p/>
     */
    int REACTION_FLAG_MASK_START_SWIPE_LEFT = (128 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_LEFT);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe up" (completely no reactions)
     */
    int REACTION_CAN_NOT_SWIPE_UP = (0 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_UP);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe up"  (not swipeable, but rubber-band effect applied)
     */
    int REACTION_CAN_NOT_SWIPE_UP_WITH_RUBBER_BAND_EFFECT = (1 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_UP);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can swipe up"
     */
    int REACTION_CAN_SWIPE_UP = (2 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_UP);

    /**
     * Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * If this flag is specified, swipe operation will not start even if user swipe an item to the UP direction.
     * <p/>
     */
    int REACTION_FLAG_MASK_START_SWIPE_UP = (128 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_UP);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe right" (completely no reactions)
     */
    int REACTION_CAN_NOT_SWIPE_RIGHT = (0 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_RIGHT);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe right"  (not swipeable, but rubber-band effect applied)
     */
    int REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT = (1 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_RIGHT);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can swipe right"
     */
    int REACTION_CAN_SWIPE_RIGHT = (2 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_RIGHT);

    /**
     * Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * If this flag is specified, swipe operation will not start even if user swipe an item to the RIGHT direction.
     * <p/>
     */
    int REACTION_FLAG_MASK_START_SWIPE_RIGHT = (128 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_RIGHT);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe down" (completely no reactions)
     */
    int REACTION_CAN_NOT_SWIPE_DOWN = (0 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_DOWN);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe down"  (not swipeable, but rubber-band effect applied)
     */
    int REACTION_CAN_NOT_SWIPE_DOWN_WITH_RUBBER_BAND_EFFECT = (1 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_DOWN);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can swipe down"
     */
    int REACTION_CAN_SWIPE_DOWN = (2 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_DOWN);

    /**
     * Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * If this flag is specified, swipe operation will not start even if user swipe an item to the left direction.
     * <p/>
     */
    int REACTION_FLAG_MASK_START_SWIPE_DOWN = (128 << RecyclerViewSwipeManager.BIT_SHIFT_AMOUNT_DOWN);

    // ---

    /**
     * Convenient constant value: Equals to {@link #REACTION_CAN_NOT_SWIPE_LEFT} | {@link #REACTION_CAN_NOT_SWIPE_RIGHT}
     */
    int REACTION_CAN_NOT_SWIPE_BOTH_H =
            REACTION_CAN_NOT_SWIPE_LEFT | REACTION_CAN_NOT_SWIPE_RIGHT;

    /**
     * Convenient constant value: Equals to {@link #REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT} | {@link #REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT}
     */
    int REACTION_CAN_NOT_SWIPE_BOTH_H_WITH_RUBBER_BAND_EFFECT =
            REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT |
                    REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT;

    /**
     * Convenient constant value: Equals to {@link #REACTION_CAN_SWIPE_LEFT} | {@link #REACTION_CAN_SWIPE_RIGHT}
     */
    int REACTION_CAN_SWIPE_BOTH_H =
            REACTION_CAN_SWIPE_LEFT | REACTION_CAN_SWIPE_RIGHT;

    /**
     * Convenient constant value: Equals to {@link #REACTION_CAN_NOT_SWIPE_UP} | {@link #REACTION_CAN_NOT_SWIPE_DOWN}
     */
    int REACTION_CAN_NOT_SWIPE_BOTH_V =
            REACTION_CAN_NOT_SWIPE_UP | REACTION_CAN_NOT_SWIPE_DOWN;

    /**
     * Convenient constant value: Equals to {@link #REACTION_CAN_NOT_SWIPE_UP_WITH_RUBBER_BAND_EFFECT} | {@link #REACTION_CAN_NOT_SWIPE_DOWN_WITH_RUBBER_BAND_EFFECT}
     */
    int REACTION_CAN_NOT_SWIPE_BOTH_V_WITH_RUBBER_BAND_EFFECT =
            REACTION_CAN_NOT_SWIPE_UP_WITH_RUBBER_BAND_EFFECT |
                    REACTION_CAN_NOT_SWIPE_DOWN_WITH_RUBBER_BAND_EFFECT;

    /**
     * Convenient constant value: Equals to {@link #REACTION_CAN_SWIPE_UP} | {@link #REACTION_CAN_SWIPE_DOWN}
     */
    int REACTION_CAN_SWIPE_BOTH_V =
            REACTION_CAN_SWIPE_UP | REACTION_CAN_SWIPE_DOWN;

    // ---

    /**
     * @deprecated Use {@link #REACTION_CAN_NOT_SWIPE_ANY} directly.
     */
    int REACTION_CAN_NOT_SWIPE_BOTH = REACTION_CAN_NOT_SWIPE_ANY;

    /**
     * @deprecated Use {@link #REACTION_CAN_NOT_SWIPE_BOTH_H_WITH_RUBBER_BAND_EFFECT} directly.
     */
    int REACTION_CAN_NOT_SWIPE_BOTH_WITH_RUBBER_BAND_EFFECT = REACTION_CAN_NOT_SWIPE_BOTH_H_WITH_RUBBER_BAND_EFFECT;

    /**
     * @deprecated Use {@link #REACTION_CAN_SWIPE_BOTH_H} directly.
     */
    int REACTION_CAN_SWIPE_BOTH = REACTION_CAN_SWIPE_BOTH_H;

    // ---

    /**
     * Background drawable type used for the second argument of the
     * {@link SwipeableItemAdapter#onSetSwipeBackground(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Background image for the neutral (= not swiping) item.
     */
    int DRAWABLE_SWIPE_NEUTRAL_BACKGROUND = 0;

    /**
     * Background drawable type used for the second argument of the
     * {@link SwipeableItemAdapter#onSetSwipeBackground(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Background image for the swiping-left item.
     */
    int DRAWABLE_SWIPE_LEFT_BACKGROUND = 1;

    /**
     * Background drawable type used for the second argument of the
     * {@link SwipeableItemAdapter#onSetSwipeBackground(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Background image for the swiping-up item.
     */
    int DRAWABLE_SWIPE_UP_BACKGROUND = 2;

    /**
     * Background drawable type used for the second argument of the
     * {@link SwipeableItemAdapter#onSetSwipeBackground(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Background image for the swiping-right item.
     */
    int DRAWABLE_SWIPE_RIGHT_BACKGROUND = 3;

    /**
     * Background drawable type used for the second argument of the
     * {@link SwipeableItemAdapter#onSetSwipeBackground(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Background image for the swiping-down item.
     */
    int DRAWABLE_SWIPE_DOWN_BACKGROUND = 4;

    // ---

    /**
     * After-reaction type used for the {@link SwipeableItemViewHolder#setAfterSwipeReaction(int)} and {@link SwipeableItemViewHolder#getAfterSwipeReaction()} methods.
     * Represents perform nothing.
     */
    int AFTER_SWIPE_REACTION_DEFAULT = 0;

    /**
     * After-reaction type used for the {@link SwipeableItemViewHolder#setAfterSwipeReaction(int)} and {@link SwipeableItemViewHolder#getAfterSwipeReaction()} methods.
     * Represents remove the swiped item.
     */
    int AFTER_SWIPE_REACTION_REMOVE_ITEM = 1;

    /**
     * After-reaction type used for the {@link SwipeableItemViewHolder#setAfterSwipeReaction(int)} and {@link SwipeableItemViewHolder#getAfterSwipeReaction()} methods.
     * Represents that the item moved to swiped direction.
     */
    int AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION = 2;

    // ---
    /**
     * Special value for the {@link SwipeableItemViewHolder#setSwipeItemHorizontalSlideAmount(float)}
     * and {@link SwipeableItemViewHolder#getSwipeItemHorizontalSlideAmount()} methods.
     * Indicates that this item is pinned to LEFT of the window.
     */
    float OUTSIDE_OF_THE_WINDOW_LEFT = -((1 << 16) + 0);

    /**
     * Special value for the {@link SwipeableItemViewHolder#setSwipeItemVerticalSlideAmount(float)}
     * and {@link SwipeableItemViewHolder#getSwipeItemVerticalSlideAmount()} methods.
     * Indicates that this item is pinned to UP of the window.
     */
    float OUTSIDE_OF_THE_WINDOW_TOP = -((1 << 16) + 1);

    /**
     * Special value for the {@link SwipeableItemViewHolder#setSwipeItemHorizontalSlideAmount(float)}
     * and {@link SwipeableItemViewHolder#getSwipeItemHorizontalSlideAmount()} methods.
     * Indicates that this item is pinned to RIGHT the window.
     */
    float OUTSIDE_OF_THE_WINDOW_RIGHT = ((1 << 16) + 0);

    /**
     * Special value for the {@link SwipeableItemViewHolder#setSwipeItemVerticalSlideAmount(float)}
     * and {@link SwipeableItemViewHolder#getSwipeItemVerticalSlideAmount()} methods.
     * Indicates that this item is pinned to DOWN the window.
     */
    float OUTSIDE_OF_THE_WINDOW_BOTTOM = ((1 << 16) + 1);

    // ---

}
