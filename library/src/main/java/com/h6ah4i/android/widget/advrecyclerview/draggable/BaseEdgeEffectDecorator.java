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

package com.h6ah4i.android.widget.advrecyclerview.draggable;

import android.graphics.Canvas;
import android.widget.EdgeEffect;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.widget.EdgeEffectCompat;
import androidx.recyclerview.widget.RecyclerView;

abstract class BaseEdgeEffectDecorator extends RecyclerView.ItemDecoration {
    private RecyclerView mRecyclerView;
    private EdgeEffect mGlow1;
    private EdgeEffect mGlow2;
    private boolean mStarted;
    private int mGlow1Dir;
    private int mGlow2Dir;

    protected static final int EDGE_LEFT = 0;
    protected static final int EDGE_TOP = 1;
    protected static final int EDGE_RIGHT = 2;
    protected static final int EDGE_BOTTOM = 3;

    public BaseEdgeEffectDecorator(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    protected abstract int getEdgeDirection(int no);

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        boolean needsInvalidate = false;

        if (mGlow1 != null) {
            needsInvalidate |= drawGlow(c, parent, mGlow1Dir, mGlow1);
        }

        if (mGlow2 != null) {
            needsInvalidate |= drawGlow(c, parent, mGlow2Dir, mGlow2);
        }

        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(parent);
        }
    }

    private static boolean drawGlow(Canvas c, RecyclerView parent, int dir, EdgeEffect edge) {
        if (edge.isFinished()) {
            return false;
        }

        final int restore = c.save();
        final boolean clipToPadding = getClipToPadding(parent);

        switch (dir) {
            case EDGE_TOP:
                if (clipToPadding) {
                    c.translate(parent.getPaddingLeft(), parent.getPaddingTop());
                }
                break;
            case EDGE_BOTTOM:
                c.rotate(180);
                if (clipToPadding) {
                    c.translate(-parent.getWidth() + parent.getPaddingRight(), -parent.getHeight() + parent.getPaddingBottom());
                } else {
                    c.translate(-parent.getWidth(), -parent.getHeight());
                }
                break;
            case EDGE_LEFT:
                c.rotate(-90);
                if (clipToPadding) {
                    c.translate(-parent.getHeight() + parent.getPaddingTop(), parent.getPaddingLeft());
                } else {
                    c.translate(-parent.getHeight(), 0);
                }
                break;
            case EDGE_RIGHT:
                c.rotate(90);
                if (clipToPadding) {
                    c.translate(parent.getPaddingTop(), -parent.getWidth() + parent.getPaddingRight());
                } else {
                    c.translate(0, -parent.getWidth());
                }
                break;
        }

        boolean needsInvalidate = edge.draw(c);

        c.restoreToCount(restore);

        return needsInvalidate;
    }

    public void start() {
        if (mStarted) {
            return;
        }
        mGlow1Dir = getEdgeDirection(0);
        mGlow2Dir = getEdgeDirection(1);
        mRecyclerView.addItemDecoration(this);
        mStarted = true;
    }

    public void finish() {
        if (mStarted) {
            mRecyclerView.removeItemDecoration(this);
        }
        releaseBothGlows();
        mRecyclerView = null;
        mStarted = false;
    }

    public void pullFirstEdge(float deltaDistance) {
        ensureGlow1(mRecyclerView);

        EdgeEffectCompat.onPull(mGlow1, deltaDistance, 0.5f);
        ViewCompat.postInvalidateOnAnimation(mRecyclerView);
    }

    public void pullSecondEdge(float deltaDistance) {
        ensureGlow2(mRecyclerView);

        EdgeEffectCompat.onPull(mGlow2, deltaDistance, 0.5f);
        ViewCompat.postInvalidateOnAnimation(mRecyclerView);
    }

    public void releaseBothGlows() {
        boolean needsInvalidate = false;

        if (mGlow1 != null) {
            mGlow1.onRelease();
            //noinspection ConstantConditions
            needsInvalidate |= mGlow1.isFinished();
        }

        if (mGlow2 != null) {
            mGlow2.onRelease();
            needsInvalidate |= mGlow2.isFinished();
        }

        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(mRecyclerView);
        }
    }

    private void ensureGlow1(RecyclerView rv) {
        if (mGlow1 == null) {
            mGlow1 = new EdgeEffect(rv.getContext());
        }

        updateGlowSize(rv, mGlow1, mGlow1Dir);
    }

    private void ensureGlow2(RecyclerView rv) {
        if (mGlow2 == null) {
            mGlow2 = new EdgeEffect(rv.getContext());
        }
        updateGlowSize(rv, mGlow2, mGlow2Dir);
    }

    private static void updateGlowSize(RecyclerView rv, EdgeEffect glow, int dir) {
        int width = rv.getMeasuredWidth();
        int height = rv.getMeasuredHeight();

        if (getClipToPadding(rv)) {
            width -= rv.getPaddingLeft() + rv.getPaddingRight();
            height -= rv.getPaddingTop() + rv.getPaddingBottom();
        }

        width = Math.max(0, width);
        height = Math.max(0, height);

        if (dir == EDGE_LEFT || dir == EDGE_RIGHT) {
            int t = width;
            //noinspection SuspiciousNameCombination
            width = height;
            height = t;
        }

        glow.setSize(width, height);
    }

    private static boolean getClipToPadding(RecyclerView rv) {
        return rv.getLayoutManager().getClipToPadding();
    }

    public void reorderToTop() {
        if (mStarted) {
            mRecyclerView.removeItemDecoration(this);
            mRecyclerView.addItemDecoration(this);
        }
    }
}
