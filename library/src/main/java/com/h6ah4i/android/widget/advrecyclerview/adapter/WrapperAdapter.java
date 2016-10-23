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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * An RecyclerView adapter which wraps another adapter(s).
 */
public interface WrapperAdapter<VH extends RecyclerView.ViewHolder> extends WrappedAdapter<VH> {

    /**
     * Unwraps position. This method converts the passed wrapped position to child adapter's position.
     *
     * @param dest     The destination
     * @param position The wrapped position to be unwrapped
     */
    void unwrapPosition(@NonNull UnwrapPositionResult dest, int position);

    /**
     * Wraps position. This method converts the passed child adapter's position to wrapped position.
     *
     * @param pathSegment The path segment of the child adapter
     * @param position    The child adapter's position to be wrapped
     * @return Wrapped position
     */
    int wrapPosition(@NonNull AdapterPathSegment pathSegment, int position);

    /**
     * Gets wrapped children adapters.
     *
     * @param adapters The destination
     */
    void getWrappedAdapters(@NonNull List<RecyclerView.Adapter> adapters);

    /**
     * Releases bounded resources.
     */
    void release();
}
