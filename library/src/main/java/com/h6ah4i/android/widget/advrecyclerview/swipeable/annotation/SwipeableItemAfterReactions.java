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

import androidx.annotation.IntDef;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef(flag = true, value = {
        /*SwipeableItemConstants.AFTER_SWIPE_REACTION_DEFAULT,*/
        SwipeableItemConstants.AFTER_SWIPE_REACTION_MOVE_TO_ORIGIN,
        SwipeableItemConstants.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION,
        SwipeableItemConstants.AFTER_SWIPE_REACTION_REMOVE_ITEM,
        SwipeableItemConstants.AFTER_SWIPE_REACTION_DO_NOTHING,
})
@Retention(RetentionPolicy.SOURCE)
public @interface SwipeableItemAfterReactions {
}
