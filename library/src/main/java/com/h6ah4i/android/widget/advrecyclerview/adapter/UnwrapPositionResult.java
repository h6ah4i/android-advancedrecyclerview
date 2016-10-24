/*
 *    Copyright (C) 2016 Haruki Hasegawa
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

package com.h6ah4i.android.widget.advrecyclerview.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * The result object of {@link WrapperAdapter#unwrapPosition(UnwrapPositionResult, int)}.
 * This class is mutable that is why it is intended to reuse the same instance multiple times to avoid object creations.
 */
public class UnwrapPositionResult {
    /**
     * Adapter
     */
    public RecyclerView.Adapter adapter;

    /**
     * Tag object
     *
     * <p>The tag object can be used to identify the path.
     * (e.g.: wrapper adapter can use a same child adapter multiple times)</p>
     */
    public Object tag;

    /**
     * Unwrapped position
     */
    public int position = RecyclerView.NO_POSITION;

    /**
     * Clear fields
     */
    public void clear() {
        adapter = null;
        tag = null;
        position = RecyclerView.NO_POSITION;
    }

    /**
     * Returns the result is valid.
     * @return True if the result object indicates valid position. Otherwise, false.
     */
    public boolean isValid() {
        return (adapter != null) && (position != RecyclerView.NO_POSITION);
    }
}
