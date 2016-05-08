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

class SwipeReactionUtils {
    @SuppressWarnings("PointlessBitwiseExpression")
    public static int extractLeftReaction(int type) {
        return ((type >>> InternalConstants.BIT_SHIFT_AMOUNT_LEFT) & InternalConstants.REACTION_CAPABILITY_MASK);
    }

    public static int extractUpReaction(int type) {
        return ((type >>> InternalConstants.BIT_SHIFT_AMOUNT_UP) & InternalConstants.REACTION_CAPABILITY_MASK);
    }

    public static int extractRightReaction(int type) {
        return ((type >>> InternalConstants.BIT_SHIFT_AMOUNT_RIGHT) & InternalConstants.REACTION_CAPABILITY_MASK);
    }

    public static int extractDownReaction(int type) {
        return ((type >>> InternalConstants.BIT_SHIFT_AMOUNT_DOWN) & InternalConstants.REACTION_CAPABILITY_MASK);
    }

    public static boolean canSwipeLeft(int reactionType) {
        return (extractLeftReaction(reactionType) == InternalConstants.REACTION_CAN_SWIPE);
    }

    public static boolean canSwipeUp(int reactionType) {
        return (extractUpReaction(reactionType) == InternalConstants.REACTION_CAN_SWIPE);
    }

    public static boolean canSwipeRight(int reactionType) {
        return (extractRightReaction(reactionType) == InternalConstants.REACTION_CAN_SWIPE);
    }

    public static boolean canSwipeDown(int reactionType) {
        return (extractDownReaction(reactionType) == InternalConstants.REACTION_CAN_SWIPE);
    }
}
