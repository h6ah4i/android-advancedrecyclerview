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
     * <p>Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.</p>
     * <p>None. (internal default value, this value is not used for the argument)</p>
     */
    int RESULT_NONE = 0;

    /**
     * <p>Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.</p>
     * <p>Canceled.</p>
     */
    int RESULT_CANCELED = 1;

    /**
     * <p>Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.</p>
     * <p>Swipe left performed.</p>
     */
    int RESULT_SWIPED_LEFT = 2;

    /**
     * <p>Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.</p>
     * <p>Swipe up performed.</p>
     */
    int RESULT_SWIPED_UP = 3;

    /**
     * <p>Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.</p>
     * <p>Swipe right performed.</p>
     */
    int RESULT_SWIPED_RIGHT = 4;

    /**
     * <p>Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.</p>
     * <p>Swipe down performed.</p>
     */
    int RESULT_SWIPED_DOWN = 5;

    // ---
    /**
     * <p>Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>Indicates "can not swipe any direction"</p>
     */
    int REACTION_CAN_NOT_SWIPE_ANY = 0;

    /**
     * <p>Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>Indicates "can not swipe left" (completely no reactions)</p>
     */
    int REACTION_CAN_NOT_SWIPE_LEFT = (InternalConstants.REACTION_CAN_NOT_SWIPE << InternalConstants.BIT_SHIFT_AMOUNT_LEFT);

    /**
     * <p>Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>Indicates "can not swipe left"  (not swipeable, but rubber-band effect applied)</p>
     */
    int REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT = (InternalConstants.REACTION_CAN_NOT_SWIPE_WITH_RUBBER_BAND_EFFECT << InternalConstants.BIT_SHIFT_AMOUNT_LEFT);

    /**
     * <p>Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>Indicates "can swipe left"</p>
     */
    int REACTION_CAN_SWIPE_LEFT = (InternalConstants.REACTION_CAN_SWIPE << InternalConstants.BIT_SHIFT_AMOUNT_LEFT);

    /**
     * <p>Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>If this flag is specified, swipe operation will not start even if user swipe an item to the LEFT direction.</p>
     */
    int REACTION_MASK_START_SWIPE_LEFT = (InternalConstants.REACTION_MASK_START_SWIPE << InternalConstants.BIT_SHIFT_AMOUNT_LEFT);

    /**
     * <p>Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>Indicates "can not swipe up" (completely no reactions)</p>
     */
    int REACTION_CAN_NOT_SWIPE_UP = (InternalConstants.REACTION_CAN_NOT_SWIPE << InternalConstants.BIT_SHIFT_AMOUNT_UP);

    /**
     * <p>Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>Indicates "can not swipe up"  (not swipeable, but rubber-band effect applied)</p>
     */
    int REACTION_CAN_NOT_SWIPE_UP_WITH_RUBBER_BAND_EFFECT = (InternalConstants.REACTION_CAN_NOT_SWIPE_WITH_RUBBER_BAND_EFFECT << InternalConstants.BIT_SHIFT_AMOUNT_UP);

    /**
     * <p>Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>Indicates "can swipe up"</p>
     */
    int REACTION_CAN_SWIPE_UP = (InternalConstants.REACTION_CAN_SWIPE << InternalConstants.BIT_SHIFT_AMOUNT_UP);

    /**
     * <p>Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>If this flag is specified, swipe operation will not start even if user swipe an item to the UP direction.</p>
     */
    int REACTION_MASK_START_SWIPE_UP = (InternalConstants.REACTION_MASK_START_SWIPE << InternalConstants.BIT_SHIFT_AMOUNT_UP);

    /**
     * <p>Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>Indicates "can not swipe right" (completely no reactions)</p>
     */
    int REACTION_CAN_NOT_SWIPE_RIGHT = (InternalConstants.REACTION_CAN_NOT_SWIPE << InternalConstants.BIT_SHIFT_AMOUNT_RIGHT);

    /**
     * <p>Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>Indicates "can not swipe right"  (not swipeable, but rubber-band effect applied)</p>
     */
    int REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT = (InternalConstants.REACTION_CAN_NOT_SWIPE_WITH_RUBBER_BAND_EFFECT << InternalConstants.BIT_SHIFT_AMOUNT_RIGHT);

    /**
     * <p>Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>Indicates "can swipe right"</p>
     */
    int REACTION_CAN_SWIPE_RIGHT = (InternalConstants.REACTION_CAN_SWIPE << InternalConstants.BIT_SHIFT_AMOUNT_RIGHT);

    /**
     * <p>Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>If this flag is specified, swipe operation will not start even if user swipe an item to the RIGHT direction.</p>
     */
    int REACTION_MASK_START_SWIPE_RIGHT = (InternalConstants.REACTION_MASK_START_SWIPE << InternalConstants.BIT_SHIFT_AMOUNT_RIGHT);

    /**
     * <p>Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>Indicates "can not swipe down" (completely no reactions)</p>
     */
    int REACTION_CAN_NOT_SWIPE_DOWN = (InternalConstants.REACTION_CAN_NOT_SWIPE << InternalConstants.BIT_SHIFT_AMOUNT_DOWN);

    /**
     * <p>Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>Indicates "can not swipe down"  (not swipeable, but rubber-band effect applied)</p>
     */
    int REACTION_CAN_NOT_SWIPE_DOWN_WITH_RUBBER_BAND_EFFECT = (InternalConstants.REACTION_CAN_NOT_SWIPE_WITH_RUBBER_BAND_EFFECT << InternalConstants.BIT_SHIFT_AMOUNT_DOWN);

    /**
     * <p>Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>Indicates "can swipe down"</p>
     */
    int REACTION_CAN_SWIPE_DOWN = (InternalConstants.REACTION_CAN_SWIPE << InternalConstants.BIT_SHIFT_AMOUNT_DOWN);

    /**
     * <p>Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>If this flag is specified, swipe operation will not start even if user swipe an item to the left direction.</p>
     */
    int REACTION_MASK_START_SWIPE_DOWN = (InternalConstants.REACTION_MASK_START_SWIPE << InternalConstants.BIT_SHIFT_AMOUNT_DOWN);

    /**
     * <p>Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.</p>
     * <p>If this flag is specified, swiping starts on long press.</p>
     */
    int REACTION_START_SWIPE_ON_LONG_PRESS = InternalConstants.REACTION_START_SWIPE_ON_LONG_PRESS;

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
     * <p>Background drawable type used for the second argument of the
     * {@link SwipeableItemAdapter#onSetSwipeBackground(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.</p>
     * <p>Background image for the neutral (= not swiping) item.</p>
     */
    int DRAWABLE_SWIPE_NEUTRAL_BACKGROUND = 0;

    /**
     * <p>Background drawable type used for the second argument of the
     * {@link SwipeableItemAdapter#onSetSwipeBackground(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.</p>
     * <p>Background image for the swiping-left item.</p>
     */
    int DRAWABLE_SWIPE_LEFT_BACKGROUND = 1;

    /**
     * <p>Background drawable type used for the second argument of the
     * {@link SwipeableItemAdapter#onSetSwipeBackground(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.</p>
     * <p>Background image for the swiping-up item.</p>
     */
    int DRAWABLE_SWIPE_UP_BACKGROUND = 2;

    /**
     * <p>Background drawable type used for the second argument of the
     * {@link SwipeableItemAdapter#onSetSwipeBackground(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.</p>
     * <p>Background image for the swiping-right item.</p>
     */
    int DRAWABLE_SWIPE_RIGHT_BACKGROUND = 3;

    /**
     * <p>Background drawable type used for the second argument of the
     * {@link SwipeableItemAdapter#onSetSwipeBackground(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.</p>
     * <p>Background image for the swiping-down item.</p>
     */
    int DRAWABLE_SWIPE_DOWN_BACKGROUND = 4;

    // ---

    /**
     * <p>After-reaction type used for the {@link SwipeableItemViewHolder#setAfterSwipeReaction(int)} and {@link SwipeableItemViewHolder#getAfterSwipeReaction()} methods.</p>
     * <p>Represents moving the item back to its original position.</p>
     */
    int AFTER_SWIPE_REACTION_MOVE_TO_ORIGIN = 0;

    /**
     * <p>After-reaction type used for the {@link SwipeableItemViewHolder#setAfterSwipeReaction(int)} and {@link SwipeableItemViewHolder#getAfterSwipeReaction()} methods.</p>
     * <p>Represents remove the swiped item.</p>
     */
    int AFTER_SWIPE_REACTION_REMOVE_ITEM = 1;

    /**
     * <p>After-reaction type used for the {@link SwipeableItemViewHolder#setAfterSwipeReaction(int)} and {@link SwipeableItemViewHolder#getAfterSwipeReaction()} methods.</p>
     * <p>Represents that the item moved to swiped direction.</p>
     */
    int AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION = 2;

    /**
     * <p>After-reaction type used for the {@link SwipeableItemViewHolder#setAfterSwipeReaction(int)} and {@link SwipeableItemViewHolder#getAfterSwipeReaction()} methods.</p>
     * <p>Represents performing no action on the item.</p>
     */
    int AFTER_SWIPE_REACTION_DO_NOTHING = 3;

    /**
     * <p>After-reaction type used for the {@link SwipeableItemViewHolder#setAfterSwipeReaction(int)} and {@link SwipeableItemViewHolder#getAfterSwipeReaction()} methods.</p>
     * <p>Represents moving the item back to its original position. (Alias of the {@link #AFTER_SWIPE_REACTION_MOVE_TO_ORIGIN})</p>
     */
    int AFTER_SWIPE_REACTION_DEFAULT = AFTER_SWIPE_REACTION_MOVE_TO_ORIGIN;

    // ---
    /**
     * <p>Special value for the {@link SwipeableItemViewHolder#setSwipeItemHorizontalSlideAmount(float)}
     * and {@link SwipeableItemViewHolder#getSwipeItemHorizontalSlideAmount()} methods.</p>
     * <p>Indicates that this item is pinned to LEFT of the window.</p>
     */
    @SuppressWarnings("PointlessArithmeticExpression")
    float OUTSIDE_OF_THE_WINDOW_LEFT = -((1 << 16) + 0);

    /**
     * <p>Special value for the {@link SwipeableItemViewHolder#setSwipeItemVerticalSlideAmount(float)}
     * and {@link SwipeableItemViewHolder#getSwipeItemVerticalSlideAmount()} methods.</p>
     * <p>Indicates that this item is pinned to UP of the window.</p>
     */
    float OUTSIDE_OF_THE_WINDOW_TOP = -((1 << 16) + 1);

    /**
     * <p>Special value for the {@link SwipeableItemViewHolder#setSwipeItemHorizontalSlideAmount(float)}
     * and {@link SwipeableItemViewHolder#getSwipeItemHorizontalSlideAmount()} methods.</p>
     * <p>Indicates that this item is pinned to RIGHT the window.</p>
     */
    @SuppressWarnings("PointlessArithmeticExpression")
    float OUTSIDE_OF_THE_WINDOW_RIGHT = ((1 << 16) + 0);

    /**
     * <p>Special value for the {@link SwipeableItemViewHolder#setSwipeItemVerticalSlideAmount(float)}
     * and {@link SwipeableItemViewHolder#getSwipeItemVerticalSlideAmount()} methods.</p>
     * <p>Indicates that this item is pinned to DOWN the window.</p>
     */
    float OUTSIDE_OF_THE_WINDOW_BOTTOM = ((1 << 16) + 1);

    // ---

}
