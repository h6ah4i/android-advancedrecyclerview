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

class InternalConstants {
    // bit: 0-5   : LEFT
    // bit: 6-11  : UP
    // bit: 12-17 : RIGHT
    // bit: 18-23 : DOWN
    // bit: 24    : REACTION_START_SWIPE_ON_LONG_PRESS
    static final int BIT_SHIFT_AMOUNT_LEFT = 0;
    static final int BIT_SHIFT_AMOUNT_UP = 6;
    static final int BIT_SHIFT_AMOUNT_RIGHT = 12;
    static final int BIT_SHIFT_AMOUNT_DOWN = 18;

    static final int REACTION_CAN_NOT_SWIPE = 0;
    static final int REACTION_CAN_NOT_SWIPE_WITH_RUBBER_BAND_EFFECT = 1;
    static final int REACTION_CAN_SWIPE = 2;
    static final int REACTION_MASK_START_SWIPE = 8;

    static final int REACTION_START_SWIPE_ON_LONG_PRESS = (1 << 24);

    static final int REACTION_CAPABILITY_MASK = 0x3;
}
