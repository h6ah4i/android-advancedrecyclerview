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

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class CustomRecyclerViewUtils {
    public static final int ORIENTATION_UNKNOWN = -1;
    public static final int ORIENTATION_HORIZONTAL = OrientationHelper.HORIZONTAL; // = 0
    public static final int ORIENTATION_VERTICAL = OrientationHelper.VERTICAL; // = 1

    public static final int LAYOUT_TYPE_UNKNOWN = -1;
    public static final int LAYOUT_TYPE_LINEAR_HORIZONTAL = 0;
    public static final int LAYOUT_TYPE_LINEAR_VERTICAL = 1;
    public static final int LAYOUT_TYPE_GRID_HORIZONTAL = 2;
    public static final int LAYOUT_TYPE_GRID_VERTICAL = 3;
    public static final int LAYOUT_TYPE_STAGGERED_GRID_HORIZONTAL = 4;
    public static final int LAYOUT_TYPE_STAGGERED_GRID_VERTICAL = 5;

    public static final int INVALID_SPAN_ID = -1;
    public static final int INVALID_SPAN_COUNT = -1;

    public static RecyclerView.ViewHolder findChildViewHolderUnderWithoutTranslation(@NonNull RecyclerView rv, float x, float y) {
        final View child = findChildViewUnderWithoutTranslation(rv, x, y);
        return (child != null) ? rv.getChildViewHolder(child) : null;
    }

    public static int getLayoutType(@NonNull RecyclerView rv) {
        return getLayoutType(rv.getLayoutManager());
    }

    public static int extractOrientation(int layoutType) {
        switch (layoutType) {
            case LAYOUT_TYPE_UNKNOWN:
                return ORIENTATION_UNKNOWN;
            case LAYOUT_TYPE_LINEAR_HORIZONTAL:
            case LAYOUT_TYPE_GRID_HORIZONTAL:
            case LAYOUT_TYPE_STAGGERED_GRID_HORIZONTAL:
                return ORIENTATION_HORIZONTAL;
            case LAYOUT_TYPE_LINEAR_VERTICAL:
            case LAYOUT_TYPE_GRID_VERTICAL:
            case LAYOUT_TYPE_STAGGERED_GRID_VERTICAL:
                return ORIENTATION_VERTICAL;
            default:
                throw new IllegalArgumentException("Unknown layout type (= " + layoutType + ")");
        }
    }

    public static int getLayoutType(@Nullable RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            if (((GridLayoutManager) layoutManager).getOrientation() == RecyclerView.HORIZONTAL) {
                return LAYOUT_TYPE_GRID_HORIZONTAL;
            } else {
                return LAYOUT_TYPE_GRID_VERTICAL;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) layoutManager).getOrientation() == RecyclerView.HORIZONTAL) {
                return LAYOUT_TYPE_LINEAR_HORIZONTAL;
            } else {
                return LAYOUT_TYPE_LINEAR_VERTICAL;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            if (((StaggeredGridLayoutManager) layoutManager).getOrientation() == StaggeredGridLayoutManager.HORIZONTAL) {
                return LAYOUT_TYPE_STAGGERED_GRID_HORIZONTAL;
            } else {
                return LAYOUT_TYPE_STAGGERED_GRID_VERTICAL;
            }
        } else {
            return LAYOUT_TYPE_UNKNOWN;
        }
    }

    private static View findChildViewUnderWithoutTranslation(@NonNull ViewGroup parent, float x, float y) {
        final int count = parent.getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = parent.getChildAt(i);
            if (x >= child.getLeft() &&
                    x <= child.getRight() &&
                    y >= child.getTop() &&
                    y <= child.getBottom()) {
                return child;
            }
        }
        return null;
    }

    public static RecyclerView.ViewHolder findChildViewHolderUnderWithTranslation(@NonNull RecyclerView rv, float x, float y) {
        final View child = rv.findChildViewUnder(x, y);
        return (child != null) ? rv.getChildViewHolder(child) : null;
    }

    public static Rect getLayoutMargins(@NonNull View v, @NonNull Rect outMargins) {
        final ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            final ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            outMargins.left = marginLayoutParams.leftMargin;
            outMargins.right = marginLayoutParams.rightMargin;
            outMargins.top = marginLayoutParams.topMargin;
            outMargins.bottom = marginLayoutParams.bottomMargin;
        } else {
            outMargins.left = outMargins.right = outMargins.top = outMargins.bottom = 0;
        }
        return outMargins;
    }

    public static Rect getDecorationOffsets(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View view, @NonNull Rect outDecorations) {
        outDecorations.left = layoutManager.getLeftDecorationWidth(view);
        outDecorations.right = layoutManager.getRightDecorationWidth(view);
        outDecorations.top = layoutManager.getTopDecorationHeight(view);
        outDecorations.bottom = layoutManager.getBottomDecorationHeight(view);

        return outDecorations;
    }

    public static Rect getViewBounds(@NonNull View v, @NonNull Rect outBounds) {
        outBounds.left = v.getLeft();
        outBounds.right = v.getRight();
        outBounds.top = v.getTop();
        outBounds.bottom = v.getBottom();
        return outBounds;
    }


    public static int findFirstVisibleItemPosition(@NonNull RecyclerView rv, boolean includesPadding) {
        RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();

        if (layoutManager instanceof LinearLayoutManager) {
            if (includesPadding) {
                return findFirstVisibleItemPositionIncludesPadding((LinearLayoutManager) layoutManager);
            } else {
                return (((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition());
            }
        } else {
            return RecyclerView.NO_POSITION;
        }
    }

    public static int findLastVisibleItemPosition(@NonNull RecyclerView rv, boolean includesPadding) {
        RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();

        if (layoutManager instanceof LinearLayoutManager) {
            if (includesPadding) {
                return findLastVisibleItemPositionIncludesPadding((LinearLayoutManager) layoutManager);
            } else {
                return (((LinearLayoutManager) layoutManager).findLastVisibleItemPosition());
            }
        } else {
            return RecyclerView.NO_POSITION;
        }
    }

    public static int findFirstCompletelyVisibleItemPosition(@NonNull RecyclerView rv) {
        RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();

        if (layoutManager instanceof LinearLayoutManager) {
            return (((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition());
        } else {
            return RecyclerView.NO_POSITION;
        }
    }

    public static int findLastCompletelyVisibleItemPosition(@NonNull RecyclerView rv) {
        RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();

        if (layoutManager instanceof LinearLayoutManager) {
            return (((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition());
        } else {
            return RecyclerView.NO_POSITION;
        }
    }

    public static int getSynchronizedPosition(@NonNull RecyclerView.ViewHolder holder) {
        int pos1 = holder.getLayoutPosition();
        int pos2 = holder.getAdapterPosition();
        if (pos1 == pos2) {
            return pos1;
        } else {
            return RecyclerView.NO_POSITION;
        }
    }

    public static int getSpanCount(@NonNull RecyclerView rv) {
        RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        } else {
            return 1;
        }
    }

    public static int getOrientation(@NonNull RecyclerView rv) {
        return getOrientation(rv.getLayoutManager());
    }

    public static int getOrientation(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getOrientation();
        } else if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).getOrientation();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getOrientation();
        } else {
            return ORIENTATION_UNKNOWN;
        }
    }

    private static int findFirstVisibleItemPositionIncludesPadding(LinearLayoutManager lm) {
        final View child = findOneVisibleChildIncludesPadding(lm, 0, lm.getChildCount(), false, true);
        return child == null ? RecyclerView.NO_POSITION : lm.getPosition(child);
    }

    private static int findLastVisibleItemPositionIncludesPadding(LinearLayoutManager lm) {
        final View child = findOneVisibleChildIncludesPadding(lm, lm.getChildCount() - 1, -1, false, true);
        return child == null ? RecyclerView.NO_POSITION : lm.getPosition(child);
    }

    // This method is a modified version of the LinearLayoutManager.findOneVisibleChild().
    private static View findOneVisibleChildIncludesPadding(
            LinearLayoutManager lm, int fromIndex, int toIndex,
            boolean completelyVisible, boolean acceptPartiallyVisible) {
        boolean isVertical = (lm.getOrientation() == RecyclerView.VERTICAL);
        final int start = 0;
        final int end = (isVertical) ? lm.getHeight() : lm.getWidth();
        final int next = toIndex > fromIndex ? 1 : -1;
        View partiallyVisible = null;
        for (int i = fromIndex; i != toIndex; i += next) {
            final View child = lm.getChildAt(i);
            final int childStart = (isVertical) ? child.getTop() : child.getLeft();
            final int childEnd = (isVertical) ? child.getBottom() : child.getRight();
            if (childStart < end && childEnd > start) {
                if (completelyVisible) {
                    if (childStart >= start && childEnd <= end) {
                        return child;
                    } else if (acceptPartiallyVisible && partiallyVisible == null) {
                        partiallyVisible = child;
                    }
                } else {
                    return child;
                }
            }
        }
        return partiallyVisible;
    }

    public static int safeGetAdapterPosition(@Nullable RecyclerView.ViewHolder holder) {
        return (holder != null) ? holder.getAdapterPosition() : RecyclerView.NO_POSITION;
    }

    public static int safeGetLayoutPosition(@Nullable RecyclerView.ViewHolder holder) {
        return (holder != null) ? holder.getLayoutPosition() : RecyclerView.NO_POSITION;
    }

    public static View findViewByPosition(@NonNull RecyclerView.LayoutManager layoutManager, int position) {
        return (position != RecyclerView.NO_POSITION) ? layoutManager.findViewByPosition(position) : null;
    }


    public static int getSpanIndex(@Nullable RecyclerView.ViewHolder holder) {
        final View itemView = getLaidOutItemView(holder);

        if (itemView == null) {
            return INVALID_SPAN_ID;
        }

        ViewGroup.LayoutParams lp = itemView.getLayoutParams();

        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            return ((StaggeredGridLayoutManager.LayoutParams) lp).getSpanIndex();
        } else if (lp instanceof GridLayoutManager.LayoutParams) {
            return ((GridLayoutManager.LayoutParams) lp).getSpanIndex();
        } else if (lp instanceof RecyclerView.LayoutParams) {
            return 0;
        } else {
            return INVALID_SPAN_ID;
        }
    }

    public static int getSpanSize(@Nullable RecyclerView.ViewHolder holder) {
        final View itemView = getLaidOutItemView(holder);

        if (itemView == null) {
            return INVALID_SPAN_COUNT;
        }

        ViewGroup.LayoutParams lp = itemView.getLayoutParams();

        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            final boolean isFullSpan = ((StaggeredGridLayoutManager.LayoutParams) lp).isFullSpan();
            if (isFullSpan) {
                final RecyclerView rv = (RecyclerView) itemView.getParent();
                return getSpanCount(rv);
            } else {
                return 1;
            }
        } else if (lp instanceof GridLayoutManager.LayoutParams) {
            return ((GridLayoutManager.LayoutParams) lp).getSpanSize();
        } else if (lp instanceof RecyclerView.LayoutParams) {
            return 1;
        } else {
            return INVALID_SPAN_COUNT;
        }
    }

    public static boolean isFullSpan(@Nullable RecyclerView.ViewHolder holder) {
        final View itemView = getLaidOutItemView(holder);

        if (itemView == null) {
            return true;
        }

        ViewGroup.LayoutParams lp = itemView.getLayoutParams();

        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            return ((StaggeredGridLayoutManager.LayoutParams) lp).isFullSpan();
        } else if (lp instanceof GridLayoutManager.LayoutParams) {
            final RecyclerView rv = (RecyclerView) itemView.getParent();
            final int spanCount = getSpanCount(rv);
            final int spanSize = ((GridLayoutManager.LayoutParams) lp).getSpanSize();
            return (spanCount == spanSize);
        } else if (lp instanceof RecyclerView.LayoutParams) {
            return true;
        } else {
            return true;
        }
    }

    private static View getLaidOutItemView(@Nullable RecyclerView.ViewHolder  holder) {
        if (holder == null) {
            return null;
        }

        final View itemView = holder.itemView;

        if (!ViewCompat.isLaidOut(itemView)) {
            return null;
        }

        return itemView;
    }

    public static boolean isLinearLayout(int layoutType) {
        return ((layoutType == LAYOUT_TYPE_LINEAR_VERTICAL) || (layoutType == LAYOUT_TYPE_LINEAR_HORIZONTAL));
    }


    public static boolean isGridLayout(int layoutType) {
        return ((layoutType == LAYOUT_TYPE_GRID_VERTICAL) || (layoutType == LAYOUT_TYPE_GRID_HORIZONTAL));
    }

    public static boolean isStaggeredGridLayout(int layoutType) {
        return ((layoutType == LAYOUT_TYPE_STAGGERED_GRID_VERTICAL) || (layoutType == LAYOUT_TYPE_STAGGERED_GRID_HORIZONTAL));
    }
}