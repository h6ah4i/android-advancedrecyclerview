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

package com.h6ah4i.android.widget.advrecyclerview.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.adapter.AdapterPath;
import com.h6ah4i.android.widget.advrecyclerview.adapter.AdapterPathSegment;
import com.h6ah4i.android.widget.advrecyclerview.adapter.SimpleWrapperAdapter;
import com.h6ah4i.android.widget.advrecyclerview.adapter.UnwrapPositionResult;
import com.h6ah4i.android.widget.advrecyclerview.adapter.WrapperAdapter;

import java.util.ArrayList;
import java.util.List;

public class WrapperAdapterUtils {

    private WrapperAdapterUtils() {
    }

    public static <T> T findWrappedAdapter(RecyclerView.Adapter adapter, Class<T> clazz) {
        if (clazz.isInstance(adapter)) {
            return clazz.cast(adapter);
        } else if (adapter instanceof SimpleWrapperAdapter) {
            final RecyclerView.Adapter wrappedAdapter = ((SimpleWrapperAdapter) adapter).getWrappedAdapter();
            return findWrappedAdapter(wrappedAdapter, clazz);
        } else {
            return null;
        }
    }

    public static <T> T findWrappedAdapter(RecyclerView.Adapter originAdapter, Class<T> clazz, int position) {
        final AdapterPath path = new AdapterPath();
        final int wrappedPosition = unwrapPosition(originAdapter, null, null, position, path);

        if (wrappedPosition == RecyclerView.NO_POSITION) {
            return null;
        }

        for (AdapterPathSegment segment : path.segments()) {
            if (clazz.isInstance(segment.adapter)) {
                return clazz.cast(segment.adapter);
            }
        }

        return null;
    }

    public static RecyclerView.Adapter releaseAll(RecyclerView.Adapter adapter) {
        return releaseCyclically(adapter);
    }

    @SuppressWarnings("unchecked")
    private static RecyclerView.Adapter releaseCyclically(RecyclerView.Adapter adapter) {
        if (!(adapter instanceof WrapperAdapter)) {
            return adapter;
        }

        final WrapperAdapter wrapperAdapter = (WrapperAdapter) adapter;
        final List<RecyclerView.Adapter> wrappedAdapters = new ArrayList<>();

        wrapperAdapter.getWrappedAdapters(wrappedAdapters);

        wrapperAdapter.release();

        for (int i = wrappedAdapters.size() - 1; i >= 0; i--) {
            RecyclerView.Adapter wrappedAdapter = wrappedAdapters.get(i);
            releaseCyclically(wrappedAdapter);
        }

        wrappedAdapters.clear();

        return adapter;
    }

    public static int unwrapPosition(@NonNull RecyclerView.Adapter originAdapter, int position) {
        return unwrapPosition(originAdapter, null, position);
    }

    public static int unwrapPosition(@NonNull RecyclerView.Adapter originAdapter, @Nullable RecyclerView.Adapter targetAdapter, int position) {
        return unwrapPosition(originAdapter, targetAdapter, null, position, null);
    }


    public static int unwrapPosition(@NonNull RecyclerView.Adapter originAdapter, @Nullable RecyclerView.Adapter targetAdapter, Object targetAdapterTag, int position) {
        return unwrapPosition(originAdapter, targetAdapter, targetAdapterTag, position, null);
    }

    public static int unwrapPosition(RecyclerView.Adapter originAdapter, RecyclerView.Adapter targetAdapter, Object targetAdapterTag, int originPosition, @Nullable AdapterPath destPath) {
        RecyclerView.Adapter wrapper = originAdapter;
        int wrappedPosition = originPosition;
        final UnwrapPositionResult tmpResult = new UnwrapPositionResult();
        Object wrappedAdapterTag = null;

        if (destPath != null) {
            destPath.clear();
        }

        if (wrapper == null) {
            return RecyclerView.NO_POSITION;
        }

        if (destPath != null) {
            destPath.append(new AdapterPathSegment(originAdapter, null));
        }

        do {
            if (wrappedPosition == RecyclerView.NO_POSITION) {
                break;
            }

            if (wrapper == targetAdapter) {
                break;
            }

            if (!(wrapper instanceof WrapperAdapter)) {
                if (targetAdapter != null) {
                    wrappedPosition = RecyclerView.NO_POSITION;
                }
                break;
            }

            final WrapperAdapter wrapperParentAdapter = (WrapperAdapter) wrapper;

            tmpResult.clear();
            wrapperParentAdapter.unwrapPosition(tmpResult, wrappedPosition);
            wrappedPosition = tmpResult.position;
            wrappedAdapterTag = tmpResult.tag;

            if (tmpResult.isValid()) {
                if (destPath != null) {
                    destPath.append(tmpResult);
                }
            }

            wrapper = tmpResult.adapter;
        } while (wrapper != null);

        if (targetAdapter != null && wrapper != targetAdapter) {
            wrappedPosition = RecyclerView.NO_POSITION;
        }

        if (targetAdapterTag != null && (wrappedAdapterTag != targetAdapterTag)) {
            wrappedPosition = RecyclerView.NO_POSITION;
        }

        if (wrappedPosition == RecyclerView.NO_POSITION && destPath != null) {
            destPath.clear();
        }

        return wrappedPosition;
    }

    public static int wrapPosition(@NonNull AdapterPath path, @Nullable RecyclerView.Adapter originAdapter, @Nullable RecyclerView.Adapter targetAdapter, int position) {
        final List<AdapterPathSegment> segments = path.segments();
        final int nSegments = segments.size();

        int originSegmentIndex = (originAdapter == null) ? nSegments - 1 : -1;
        int targetSegmentIndex = (targetAdapter == null) ? 0 : -1;

        if (originAdapter != null || targetAdapter != null) {
            for (int i = 0; i < nSegments; i++) {
                AdapterPathSegment segment = segments.get(i);
                if (originAdapter != null && segment.adapter == originAdapter) {
                    originSegmentIndex = i;
                }
                if (targetAdapter != null && segment.adapter == targetAdapter) {
                    targetSegmentIndex = i;
                }
            }
        }

        if (!((originSegmentIndex != -1) && (targetSegmentIndex != -1) && (targetSegmentIndex <= originSegmentIndex))) {
            return RecyclerView.NO_POSITION;
        }

        return wrapPosition(path, originSegmentIndex, targetSegmentIndex, position);
    }

    public static int wrapPosition(@NonNull AdapterPath path, int originSegmentIndex, int targetSegmentIndex, int position) {
        final List<AdapterPathSegment> segments = path.segments();
        int wrappedPosition = position;

        for (int i = originSegmentIndex; i > targetSegmentIndex; i--) {
            AdapterPathSegment segment = segments.get(i);
            AdapterPathSegment parentSegment = segments.get(i - 1);

            final int prevWrappedPosition = wrappedPosition;

            wrappedPosition = ((WrapperAdapter) parentSegment.adapter).wrapPosition(segment, wrappedPosition);

            if (wrappedPosition == RecyclerView.NO_POSITION) {
                break;
            }
        }

        return wrappedPosition;
    }

}
