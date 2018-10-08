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

package com.h6ah4i.android.widget.advrecyclerview.animator.impl;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.animator.BaseItemAnimator;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseItemAnimationManager<T extends ItemAnimationInfo> {
    private static TimeInterpolator sDefaultInterpolator;

    protected final BaseItemAnimator mItemAnimator;
    protected final List<T> mPending;
    protected final List<List<T>> mDeferredReadySets;
    protected final List<RecyclerView.ViewHolder> mActive;

    public BaseItemAnimationManager(@NonNull BaseItemAnimator itemAnimator) {
        mItemAnimator = itemAnimator;
        mPending = new ArrayList<>();
        mActive = new ArrayList<>();
        mDeferredReadySets = new ArrayList<>();
    }

    protected final boolean debugLogEnabled() {
        return mItemAnimator.debugLogEnabled();
    }

    public boolean hasPending() {
        return !mPending.isEmpty();
    }

    public boolean isRunning() {
        return !mPending.isEmpty() || !mActive.isEmpty() || !mDeferredReadySets.isEmpty();
    }

    public boolean removeFromActive(@NonNull RecyclerView.ViewHolder item) {
        return mActive.remove(item);
    }

    public void cancelAllStartedAnimations() {
        final List<RecyclerView.ViewHolder> active = mActive;
        for (int i = active.size() - 1; i >= 0; i--) {
            final View view = active.get(i).itemView;
            ViewCompat.animate(view).cancel();
        }
    }

    public void runPendingAnimations(boolean deferred, long deferredDelay) {
        final List<T> ready = new ArrayList<>(mPending);
        mPending.clear();

        if (deferred) {
            mDeferredReadySets.add(ready);

            final Runnable process = new Runnable() {
                @Override
                public void run() {
                    for (T info : ready) {
                        createAnimation(info);
                    }
                    ready.clear();
                    mDeferredReadySets.remove(ready);
                }
            };

            final View view = ready.get(0).getAvailableViewHolder().itemView;
            ViewCompat.postOnAnimationDelayed(view, process, deferredDelay);
        } else {
            for (T info : ready) {
                createAnimation(info);
            }
            ready.clear();
        }
    }

    public abstract void dispatchStarting(@NonNull T info, @NonNull RecyclerView.ViewHolder item);

    public abstract void dispatchFinished(@NonNull T info, @NonNull RecyclerView.ViewHolder item);

    public abstract long getDuration();

    public abstract void setDuration(long duration);

    public void endPendingAnimations(@Nullable RecyclerView.ViewHolder item) {
        final List<T> pending = mPending;

        for (int i = pending.size() - 1; i >= 0; i--) {
            final T info = pending.get(i);

            if (endNotStartedAnimation(info, item) && (item != null)) {
                pending.remove(i);
            }
        }

        if (item == null) {
            pending.clear();
        }
    }

    public void endAllPendingAnimations() {
        endPendingAnimations(null);
    }

    public void endDeferredReadyAnimations(@Nullable RecyclerView.ViewHolder item) {
        for (int i = mDeferredReadySets.size() - 1; i >= 0; i--) {
            final List<T> ready = mDeferredReadySets.get(i);

            for (int j = ready.size() - 1; j >= 0; j--) {
                final T info = ready.get(j);

                if (endNotStartedAnimation(info, item) && (item != null)) {
                    ready.remove(j);
                }
            }

            if (item == null) {
                ready.clear();
            }

            if (ready.isEmpty()) {
                mDeferredReadySets.remove(ready);
            }
        }
    }

    public void endAllDeferredReadyAnimations() {
        endDeferredReadyAnimations(null);
    }

    /*package*/ void createAnimation(@NonNull T info) {
        onCreateAnimation(info);
    }

    protected void endAnimation(@NonNull RecyclerView.ViewHolder holder) {
        mItemAnimator.endAnimation(holder);
    }

    protected void resetAnimation(@NonNull RecyclerView.ViewHolder holder) {
        if (sDefaultInterpolator == null) {
            sDefaultInterpolator = new ValueAnimator().getInterpolator();
        }
        holder.itemView.animate().setInterpolator(sDefaultInterpolator);
        endAnimation(holder);
    }

    protected void dispatchFinishedWhenDone() {
        mItemAnimator.dispatchFinishedWhenDone();
    }

    protected void enqueuePendingAnimationInfo(@NonNull T info) {
        mPending.add(info);
    }

    protected void startActiveItemAnimation(@NonNull T info, @NonNull RecyclerView.ViewHolder holder,
                                            @NonNull ViewPropertyAnimatorCompat animator) {
        animator.setListener(new BaseAnimatorListener(this, info, holder, animator));
        addActiveAnimationTarget(holder);
        animator.start();
    }

    private void addActiveAnimationTarget(RecyclerView.ViewHolder item) {
        if (item == null) {
            throw new IllegalStateException("item is null");
        }
        mActive.add(item);
    }

    protected abstract void onCreateAnimation(@NonNull T info);

    protected abstract void onAnimationEndedSuccessfully(@NonNull T info, @NonNull RecyclerView.ViewHolder item);

    protected abstract void onAnimationEndedBeforeStarted(@NonNull T info, @NonNull RecyclerView.ViewHolder item);

    protected abstract void onAnimationCancel(@NonNull T info, @NonNull RecyclerView.ViewHolder item);

    protected abstract boolean endNotStartedAnimation(@NonNull T info, @NonNull RecyclerView.ViewHolder item);

    protected static class BaseAnimatorListener implements ViewPropertyAnimatorListener {
        private BaseItemAnimationManager mManager;
        private ItemAnimationInfo mAnimationInfo;
        private RecyclerView.ViewHolder mHolder;
        private ViewPropertyAnimatorCompat mAnimator;

        public BaseAnimatorListener(BaseItemAnimationManager manager, ItemAnimationInfo info,
                RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
            mManager = manager;
            mAnimationInfo = info;
            mHolder = holder;
            mAnimator = animator;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onAnimationStart(@NonNull View view) {
            mManager.dispatchStarting(mAnimationInfo, mHolder);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onAnimationEnd(@NonNull View view) {
            final BaseItemAnimationManager manager = mManager;
            final ItemAnimationInfo info = mAnimationInfo;
            final RecyclerView.ViewHolder holder = mHolder;

            mAnimator.setListener(null);
            mManager = null;
            mAnimationInfo = null;
            mHolder = null;
            mAnimator = null;

            manager.onAnimationEndedSuccessfully(info, holder);
            manager.dispatchFinished(info, holder);
            info.clear(holder);
            manager.mActive.remove(holder);
            manager.dispatchFinishedWhenDone();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onAnimationCancel(@NonNull View view) {
            mManager.onAnimationCancel(mAnimationInfo, mHolder);
        }
    }
}

