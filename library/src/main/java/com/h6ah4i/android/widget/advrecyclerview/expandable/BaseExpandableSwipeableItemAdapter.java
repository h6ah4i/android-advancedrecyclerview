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

import com.h6ah4i.android.widget.advrecyclerview.swipeable.annotation.SwipeableItemDrawableTypes;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.annotation.SwipeableItemReactions;

public interface BaseExpandableSwipeableItemAdapter<GVH extends RecyclerView.ViewHolder, CVH extends RecyclerView.ViewHolder> {
    /**
     * Called when user is attempt to swipe the group item.
     *
     * @param holder The ViewHolder which is associated to item user is attempt to start swiping.
     * @param groupPosition Group position.
     * @param x Touched X position. Relative from the itemView's top-left.
     * @param y Touched Y position. Relative from the itemView's top-left.

     * @return Reaction type. Bitwise OR of these flags;
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_LEFT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_SWIPE_LEFT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_UP}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_UP_WITH_RUBBER_BAND_EFFECT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_SWIPE_UP}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_RIGHT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_SWIPE_RIGHT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_DOWN}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_DOWN_WITH_RUBBER_BAND_EFFECT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_SWIPE_DOWN}
     */
    @SwipeableItemReactions
    int onGetGroupItemSwipeReactionType(GVH holder, int groupPosition, int x, int y);

    /**
     * Called when user is attempt to swipe the child item.
     *
     * @param holder The ViewHolder which is associated to item user is attempt to start swiping.
     * @param groupPosition Group position.
     * @param childPosition Child position.
     * @param x Touched X position. Relative from the itemView's top-left.
     * @param y Touched Y position. Relative from the itemView's top-left.

     * @return Reaction type. Bitwise OR of these flags;
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_LEFT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_SWIPE_LEFT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_UP}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_UP_WITH_RUBBER_BAND_EFFECT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_SWIPE_UP}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_RIGHT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_SWIPE_RIGHT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_DOWN}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_NOT_SWIPE_DOWN_WITH_RUBBER_BAND_EFFECT}
     *         - {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#REACTION_CAN_SWIPE_DOWN}
     */
    @SwipeableItemReactions
    int onGetChildItemSwipeReactionType(CVH holder, int groupPosition, int childPosition, int x, int y);

    /**
     * Called when sets background of the swiping group item.
     *
     * @param holder The ViewHolder which is associated to the swiping item.
     * @param groupPosition Group position.
     * @param type Background type. One of the
     *          {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#DRAWABLE_SWIPE_NEUTRAL_BACKGROUND},
     *          {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#DRAWABLE_SWIPE_LEFT_BACKGROUND},
     *          {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#DRAWABLE_SWIPE_UP_BACKGROUND},
     *          {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#DRAWABLE_SWIPE_RIGHT_BACKGROUND} or
     *          {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#DRAWABLE_SWIPE_DOWN_BACKGROUND}.
     */
    void onSetGroupItemSwipeBackground(GVH holder, int groupPosition, @SwipeableItemDrawableTypes int type);


    /**
     * Called when sets background of the swiping child item.
     *
     * @param holder The ViewHolder which is associated to the swiping item.
     * @param groupPosition Group position.
     * @param childPosition Child position.
     * @param type Background type. One of the
     *          {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#DRAWABLE_SWIPE_NEUTRAL_BACKGROUND},
     *          {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#DRAWABLE_SWIPE_LEFT_BACKGROUND},
     *          {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#DRAWABLE_SWIPE_UP_BACKGROUND},
     *          {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#DRAWABLE_SWIPE_RIGHT_BACKGROUND} or
     *          {@link com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants#DRAWABLE_SWIPE_DOWN_BACKGROUND}.
     */
    void onSetChildItemSwipeBackground(CVH holder, int groupPosition, int childPosition, @SwipeableItemDrawableTypes int type);
}
