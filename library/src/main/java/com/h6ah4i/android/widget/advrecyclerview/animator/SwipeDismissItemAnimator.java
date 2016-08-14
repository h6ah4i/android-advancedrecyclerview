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

package com.h6ah4i.android.widget.advrecyclerview.animator;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.h6ah4i.android.widget.advrecyclerview.animator.impl.ItemRemoveAnimationManager;
import com.h6ah4i.android.widget.advrecyclerview.animator.impl.RemoveAnimationInfo;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemViewHolder;

public class SwipeDismissItemAnimator extends DraggableItemAnimator {

    public static final Interpolator MOVE_INTERPOLATOR = new AccelerateDecelerateInterpolator();

    @Override
    protected void onSetup() {
        setItemAddAnimationsManager(new DefaultItemAddAnimationManager(this));
        setItemRemoveAnimationManager(new SwipeDismissItemRemoveAnimationManager(this));
        setItemChangeAnimationsManager(new DefaultItemChangeAnimationManager(this));
        setItemMoveAnimationsManager(new DefaultItemMoveAnimationManager(this));

        setRemoveDuration(150);
        setMoveDuration(150);
    }

    /**
     * Item Animation manager for REMOVE operation  (Same behavior as DefaultItemAnimator class)
     */
    protected static class SwipeDismissItemRemoveAnimationManager extends ItemRemoveAnimationManager {
        protected static final Interpolator DEFAULT_INTERPOLATOR = new AccelerateDecelerateInterpolator();

        public SwipeDismissItemRemoveAnimationManager(BaseItemAnimator itemAnimator) {
            super(itemAnimator);
        }

        @Override
        protected void onCreateAnimation(RemoveAnimationInfo info) {
            final ViewPropertyAnimatorCompat animator;

            if (isSwipeDismissed(info.holder)) {
                final View view = info.holder.itemView;
                animator = ViewCompat.animate(view);
                animator.setDuration(getDuration());
            } else {
                final View view = info.holder.itemView;
                animator = ViewCompat.animate(view);
                animator.setDuration(getDuration());
                animator.setInterpolator(DEFAULT_INTERPOLATOR);
                animator.alpha(0);
            }

            startActiveItemAnimation(info, info.holder, animator);
        }

        protected static boolean isSwipeDismissed(RecyclerView.ViewHolder item) {
            if (!(item instanceof SwipeableItemViewHolder)) {
                return false;
            }

            final SwipeableItemViewHolder item2 = (SwipeableItemViewHolder) item;
            final int result = item2.getSwipeResult();
            final int reaction = item2.getAfterSwipeReaction();

            return ((result == RecyclerViewSwipeManager.RESULT_SWIPED_LEFT) ||
                    (result == RecyclerViewSwipeManager.RESULT_SWIPED_UP) ||
                    (result == RecyclerViewSwipeManager.RESULT_SWIPED_RIGHT) ||
                    (result == RecyclerViewSwipeManager.RESULT_SWIPED_DOWN)) &&
                    (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM);
        }

        protected static boolean isSwipeDismissed(RemoveAnimationInfo info) {
            return (info instanceof SwipeDismissRemoveAnimationInfo);
        }

        @Override
        protected void onAnimationEndedSuccessfully(RemoveAnimationInfo info, RecyclerView.ViewHolder item) {
            final View view = item.itemView;

            if (isSwipeDismissed(info)) {
                ViewCompat.setTranslationX(view, 0);
                ViewCompat.setTranslationY(view, 0);
            } else {
                ViewCompat.setAlpha(view, 1);
            }
        }

        @Override
        protected void onAnimationEndedBeforeStarted(RemoveAnimationInfo info, RecyclerView.ViewHolder item) {
            final View view = item.itemView;

            if (isSwipeDismissed(info)) {
                ViewCompat.setTranslationX(view, 0);
                ViewCompat.setTranslationY(view, 0);
            } else {
                ViewCompat.setAlpha(view, 1);
            }
        }

        @Override
        protected void onAnimationCancel(RemoveAnimationInfo info, RecyclerView.ViewHolder item) {
        }

        @Override
        public boolean addPendingAnimation(RecyclerView.ViewHolder holder) {
            if (isSwipeDismissed(holder)) {
                final View itemView = holder.itemView;
                final int prevItemX = (int) (ViewCompat.getTranslationX(itemView) + 0.5f);
                final int prevItemY = (int) (ViewCompat.getTranslationY(itemView) + 0.5f);

                endAnimation(holder);

                ViewCompat.setTranslationX(itemView, prevItemX);
                ViewCompat.setTranslationY(itemView, prevItemY);

                enqueuePendingAnimationInfo(new SwipeDismissRemoveAnimationInfo(holder));

                return true;
            } else {
                endAnimation(holder);

                enqueuePendingAnimationInfo(new RemoveAnimationInfo(holder));

                return true;
            }
        }
    }

    protected static class SwipeDismissRemoveAnimationInfo extends RemoveAnimationInfo {
        public SwipeDismissRemoveAnimationInfo(RecyclerView.ViewHolder holder) {
            super(holder);
        }
    }
}
