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

package com.h6ah4i.android.widget.advrecyclerview.swipeable.annotation;

import android.annotation.SuppressLint;
import android.support.annotation.IntDef;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("deprecation")
@SuppressLint("UniqueConstants")
@IntDef(flag = true, value = {
        SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_ANY,
        SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_LEFT,
        SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT,
        SwipeableItemConstants.REACTION_CAN_SWIPE_LEFT,
        SwipeableItemConstants.REACTION_MASK_START_SWIPE_LEFT,
        SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_UP,
        SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_UP_WITH_RUBBER_BAND_EFFECT,
        SwipeableItemConstants.REACTION_CAN_SWIPE_UP,
        SwipeableItemConstants.REACTION_MASK_START_SWIPE_UP,
        SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_RIGHT,
        SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT,
        SwipeableItemConstants.REACTION_CAN_SWIPE_RIGHT,
        SwipeableItemConstants.REACTION_MASK_START_SWIPE_RIGHT,
        SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_DOWN,
        SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_DOWN_WITH_RUBBER_BAND_EFFECT,
        SwipeableItemConstants.REACTION_CAN_SWIPE_DOWN,
        SwipeableItemConstants.REACTION_MASK_START_SWIPE_DOWN,
        SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_H,
        SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_H_WITH_RUBBER_BAND_EFFECT,
        SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H,
        SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_V,
        SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_V_WITH_RUBBER_BAND_EFFECT,
        SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_V,
        SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH,
        SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_WITH_RUBBER_BAND_EFFECT,
        SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH,
        SwipeableItemConstants.REACTION_START_SWIPE_ON_LONG_PRESS,
})
@Retention(RetentionPolicy.SOURCE)
public @interface SwipeableItemReactions {
}
