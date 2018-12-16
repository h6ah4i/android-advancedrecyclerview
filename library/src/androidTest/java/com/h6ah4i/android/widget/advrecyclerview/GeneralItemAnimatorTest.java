/*
 * This file is derived from /v7/recyclerview/tests/src/android/support/v7/widget/DefaultItemAnimatorTest.java
 */
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.h6ah4i.android.widget.advrecyclerview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.animator.BaseItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public abstract class GeneralItemAnimatorTest {

    GeneralItemAnimator mAnimator;
    Adapter mAdapter;
    ViewGroup mDummyParent;
    CountDownLatch mExpectedItems;

    final Set<RecyclerView.ViewHolder> mRemoveFinished = new HashSet<>();
    final Set<RecyclerView.ViewHolder> mAddFinished = new HashSet<>();
    final Set<RecyclerView.ViewHolder> mMoveFinished = new HashSet<>();
    final Set<RecyclerView.ViewHolder> mChangeFinished = new HashSet<>();

    @Rule
    public ActivityTestRule<TestActivity> mActivityRule = new ActivityTestRule(TestActivity.class);

    @Before
    public void setUp() {
        mAnimator = onCreateTestTargetItemAnimator();
        mAnimator.setDebug(true);
        mAdapter = new Adapter(20);
        mDummyParent = getActivity().mContainer;
        mAnimator.setListener(new BaseItemAnimator.ItemAnimatorListener() {
            @Override
            public void onRemoveFinished(@NonNull RecyclerView.ViewHolder item) {
                assertTrue(mRemoveFinished.add(item));
                onFinished();
            }

            @Override
            public void onAddFinished(@NonNull RecyclerView.ViewHolder item) {
                assertTrue(mAddFinished.add(item));
                onFinished();
            }

            @Override
            public void onMoveFinished(@NonNull RecyclerView.ViewHolder item) {
                assertTrue(mMoveFinished.add(item));
                onFinished();
            }

            @Override
            public void onChangeFinished(@NonNull RecyclerView.ViewHolder item) {
                assertTrue(mChangeFinished.add(item));
                onFinished();
            }

            private void onFinished() {
                if (mExpectedItems != null) {
                    mExpectedItems.countDown();
                }
            }
        });
    }

    protected abstract GeneralItemAnimator onCreateTestTargetItemAnimator();

    private void expectItems(int count) {
        mExpectedItems = new CountDownLatch(count);
    }

    private void runAndWait(int seconds) throws Throwable {
        runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mAnimator.runPendingAnimations();
            }
        });
        waitForItems(seconds);
    }

    private void waitForItems(int seconds) throws InterruptedException {
        mExpectedItems.await(seconds, TimeUnit.SECONDS);
        assertEquals("all expected finish events should happen", 0, mExpectedItems.getCount());
    }

    @Test
    public void testAnimateAdd() throws Throwable {
        ViewHolder vh = createViewHolder(1);
        expectItems(1);
        assertTrue(animateAdd(vh));
        assertTrue(mAnimator.isRunning());
        runAndWait(1);
    }

    @Test
    public void testAnimateRemove() throws Throwable {
        ViewHolder vh = createViewHolder(1);
        expectItems(1);
        assertTrue(animateRemove(vh));
        assertTrue(mAnimator.isRunning());
        runAndWait(1);
    }

    @Test
    public void testAnimateMove() throws Throwable {
        ViewHolder vh = createViewHolder(1);
        expectItems(1);
        assertTrue(animateMove(vh, 0, 0, 100, 100));
        assertTrue(mAnimator.isRunning());
        runAndWait(1);
    }

    @Test
    public void testAnimateChange() throws Throwable {
        ViewHolder vh = createViewHolder(1);
        ViewHolder vh2 = createViewHolder(2);
        expectItems(2);
        assertTrue(animateChange(vh, vh2, 0, 0, 100, 100));
        assertTrue(mAnimator.isRunning());
        runAndWait(1);
    }

    private boolean animateAdd(final RecyclerView.ViewHolder vh) throws Throwable {
        final boolean[] result = new boolean[1];
        runOnMainSync(new Runnable() {
            @Override
            public void run() {
                result[0] = mAnimator.animateAdd(vh);
            }
        });
        return result[0];
    }

    private boolean animateRemove(final RecyclerView.ViewHolder vh) throws Throwable {
        final boolean[] result = new boolean[1];
        runOnMainSync(new Runnable() {
            @Override
            public void run() {
                result[0] = mAnimator.animateRemove(vh);
            }
        });
        return result[0];
    }

    private boolean animateMove(final RecyclerView.ViewHolder vh, final int fromX, final int fromY,
                        final int toX, final int toY) throws Throwable {
        final boolean[] result = new boolean[1];
        runOnMainSync(new Runnable() {
            @Override
            public void run() {
                result[0] = mAnimator.animateMove(vh, fromX, fromY, toX, toY);
            }
        });
        return result[0];
    }

    private boolean animateChange(final RecyclerView.ViewHolder oldHolder,
                          final RecyclerView.ViewHolder newHolder,
                          final int fromX, final int fromY, final int toX, final int toY) throws Throwable {
        final boolean[] result = new boolean[1];
        runOnMainSync(new Runnable() {
            @Override
            public void run() {
                result[0] = mAnimator.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);
            }
        });
        return result[0];
    }

    private ViewHolder createViewHolder(final int pos) throws Throwable {
        final ViewHolder vh = mAdapter.createViewHolder(mDummyParent, 1);
        runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mAdapter.bindViewHolder(vh, pos);
                mDummyParent.addView(vh.itemView);
            }
        });

        return vh;
    }

    private TestActivity getActivity() {
        return mActivityRule.getActivity();
    }

    public final void runOnMainSync(Runnable action) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(action);
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        final List<String> mItems;

        Adapter(int count) {
            mItems = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                mItems.add("item-" + i);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(new TextView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        String mBoundText;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(String text) {
            mBoundText = text;
            ((TextView) itemView).setText(text);
        }
    }
}

