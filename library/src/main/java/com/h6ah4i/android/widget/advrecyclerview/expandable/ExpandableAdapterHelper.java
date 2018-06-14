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

import androidx.recyclerview.widget.RecyclerView;

class ExpandableAdapterHelper {
    public static final long NO_EXPANDABLE_POSITION = 0xffffffffffffffffL;

    private static final long LOWER_32BIT_MASK = 0x00000000ffffffffL;
    private static final long LOWER_31BIT_MASK = 0x000000007fffffffL;

    public static long getPackedPositionForChild(int groupPosition, int childPosition) {
        return ((long) childPosition << 32) | (groupPosition & LOWER_32BIT_MASK);
    }

    public static long getPackedPositionForGroup(int groupPosition) {
        return ((long) RecyclerView.NO_POSITION << 32) | (groupPosition & LOWER_32BIT_MASK);
    }

    public static int getPackedPositionChild(long packedPosition) {
        return (int) (packedPosition >>> 32);
    }

    public static int getPackedPositionGroup(long packedPosition) {
        return (int) (packedPosition & LOWER_32BIT_MASK);
    }

    private ExpandableAdapterHelper() {
    }
}
