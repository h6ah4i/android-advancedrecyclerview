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

package com.h6ah4i.android.example.advrecyclerview.demo_e_minimal;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * This example shows very very minimal implementation of expandable feature.
 * Please refer to other examples for more advanced usages. Thanks!
 */
public class MinimalExpandableExampleActivity extends AppCompatActivity {

    private static class FillExpandingAnimationGapDecoration extends RecyclerView.ItemDecoration
            implements RecyclerViewExpandableItemManager.OnGroupExpandListener,
            RecyclerViewExpandableItemManager.OnGroupCollapseListener {

        private Paint mPaint;
        private GroupItemInfo[] mGroupItemInfo;
        private RecyclerView mRecyclerView;
        private RecyclerViewExpandableItemManager mExpMgr;
        private Interpolator mExpandInterpolator = new AccelerateInterpolator();
        private Interpolator mCollapseInterpolator = new AccelerateDecelerateInterpolator();
        LongSparseArray<GapFillAnimationRequest> mGapFillRequests = new LongSparseArray<>();

        public void setup(RecyclerView recyclerView, RecyclerViewExpandableItemManager expMgr) {
            mRecyclerView = recyclerView;
            mExpMgr = expMgr;

            mPaint = new Paint();
            mPaint.setColor(ContextCompat.getColor(recyclerView.getContext(), R.color.bg_group_item_normal_state));

            mGroupItemInfo = new GroupItemInfo[20]; // XXX must be larger than count of group items placed on the screen
            for (int i = 0; i < mGroupItemInfo.length; i++) {
                mGroupItemInfo[i] = new GroupItemInfo();
            }

            recyclerView.addItemDecoration(this);
            expMgr.setOnGroupCollapseListener(this);
            expMgr.setOnGroupExpandListener(this);
        }

        @Override
        public void onGroupExpand(int groupPosition, boolean fromUser) {
            putGapFillRequest(groupPosition, true);
        }

        @Override
        public void onGroupCollapse(int groupPosition, boolean fromUser) {
            putGapFillRequest(groupPosition, false);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if (mGapFillRequests.size() > 0) {
                boolean needsInvalidate = drawGapFills(c, parent);

                if (needsInvalidate) {
                    ViewCompat.postInvalidateOnAnimation(parent);
                }
            }

            super.onDraw(c, parent, state);
        }

        private boolean drawGapFills(Canvas c, RecyclerView parent) {
            long currentTime = AnimationUtils.currentAnimationTimeMillis();

            int childViewCount = parent.getChildCount();
            int groupItemCount = 0;

            for (int i = 0; i < childViewCount; i++) {
                View v = parent.getChildAt(i);
                RecyclerView.ViewHolder vh = parent.getChildViewHolder(v);
                if (!(vh instanceof MyGroupViewHolder))
                    continue;

                mGroupItemInfo[groupItemCount].fromViewHolder(vh);

                groupItemCount += 1;
            }

            mGroupItemInfo[groupItemCount].bottomSentry(parent);
            groupItemCount += 1;

            Arrays.sort(mGroupItemInfo, 0, groupItemCount);

            for (int i = 0; i < groupItemCount - 1; i++) {
                GroupItemInfo group = mGroupItemInfo[i];
                GroupItemInfo nextGroup = mGroupItemInfo[i + 1];
                GapFillAnimationRequest ai = mGapFillRequests.get(group.id, null);

                if (ai == null) {
                    continue;
                }

                if ((nextGroup.top + nextGroup.ty) > (group.bottom + group.ty)) {
                    float progress = (float) (currentTime - ai.startTime) / ai.duration;
                    progress = Math.min(Math.max(progress, 0.0f), 1.0f);
                    progress = ((ai.expand) ? mExpandInterpolator : mCollapseInterpolator).getInterpolation(progress);

                    float alpha = (ai.expand) ? (1.0f - progress) : progress;

                    mPaint.setAlpha((int) (255 * alpha));

                    c.drawRect(
                            Math.min(group.left, nextGroup.left),
                            (group.bottom + group.ty),
                            Math.max(group.right, nextGroup.right),
                            (nextGroup.top + nextGroup.ty),
                            mPaint);
                }
            }

            cleanUpGapFillRequests(currentTime);

            return mGapFillRequests.size() > 0;
        }

        private void putGapFillRequest(int groupPosition, boolean expand) {
            int flatPosition = mExpMgr.getFlatPosition(RecyclerViewExpandableItemManager.getPackedPositionForGroup(groupPosition));

            RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
            long itemId = mRecyclerView.getAdapter().getItemId(flatPosition);

            long currentTime = AnimationUtils.currentAnimationTimeMillis();
            long duration;

            duration = animator.getMoveDuration();
            duration += (expand) ? animator.getAddDuration() : animator.getRemoveDuration();
            duration += 150; // additional time

            mGapFillRequests.put(itemId, new GapFillAnimationRequest(currentTime, duration, expand));

            ViewCompat.postInvalidateOnAnimation(mRecyclerView);
        }

        private void cleanUpGapFillRequests(long currentTime) {
            for (int i = mGapFillRequests.size() - 1; i >= 0; i--) {
                GapFillAnimationRequest ai = mGapFillRequests.valueAt(i);

                if (currentTime >= (ai.startTime + ai.duration)) {
                    mGapFillRequests.removeAt(i);
                }
            }
        }


        static class GroupItemInfo implements Comparable {
            long id;
            int ty;
            int top;
            int bottom;
            int left;
            int right;

            void fromViewHolder(RecyclerView.ViewHolder vh) {
                View v = vh.itemView;
                this.id = vh.getItemId();
                this.ty = (int) (ViewCompat.getTranslationY(v) + 0.5f);
                this.top = v.getTop();
                this.bottom = v.getBottom();
                this.left = v.getLeft();
                this.right = v.getRight();
            }

            void bottomSentry(RecyclerView rv) {
                this.id = RecyclerView.NO_ID;
                this.ty = 0;
                this.top = rv.getHeight();
                this.bottom = rv.getHeight();
                this.left = rv.getPaddingLeft();
                this.right = rv.getWidth() - rv.getPaddingRight();
            }

            @Override
            public int compareTo(Object o) {
                GroupItemInfo target = ((GroupItemInfo) o);
                return (this.top + this.ty) - (target.top + target.ty);
            }
        }

        static class GapFillAnimationRequest {
            private final long startTime;
            private final long duration;
            private final boolean expand;

            public GapFillAnimationRequest(long startTime, long duration, boolean expand) {
                this.startTime = startTime;
                this.duration = duration;
                this.expand = expand;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo_minimal);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Setup expandable feature and RecyclerView
        RecyclerViewExpandableItemManager expMgr = new RecyclerViewExpandableItemManager(null);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(expMgr.createWrappedAdapter(new MyAdapter()));

//        recyclerView.getItemAnimator().setMoveDuration(300);
//        recyclerView.getItemAnimator().setAddDuration(300);
//        recyclerView.getItemAnimator().setRemoveDuration(300);

        FillExpandingAnimationGapDecoration gapDecoration = new FillExpandingAnimationGapDecoration();
        gapDecoration.setup(recyclerView, expMgr);

        // NOTE: need to disable change animations to ripple effect work properly
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        expMgr.attachRecyclerView(recyclerView);
    }

    static abstract class MyBaseItem {
        public final long id;
        public final String text;

        public MyBaseItem(long id, String text) {
            this.id = id;
            this.text = text;
        }
    }

    static class MyGroupItem extends MyBaseItem {
        public final List<MyChildItem> children;

        public MyGroupItem(long id, String text) {
            super(id, text);
            children = new ArrayList<>();
        }
    }

    static class MyChildItem extends MyBaseItem {
        public MyChildItem(long id, String text) {
            super(id, text);
        }
    }

    static abstract class MyBaseViewHolder extends AbstractExpandableItemViewHolder {
        TextView textView;

        public MyBaseViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }

    static class MyGroupViewHolder extends MyBaseViewHolder {
        public MyGroupViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class MyChildViewHolder extends MyBaseViewHolder {
        public MyChildViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class MyAdapter extends AbstractExpandableItemAdapter<MyGroupViewHolder, MyChildViewHolder> {
        List<MyGroupItem> mItems;

        public MyAdapter() {
            setHasStableIds(true); // this is required for expandable feature.

            mItems = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                MyGroupItem group = new MyGroupItem(i, "GROUP " + i);
                for (int j = 0; j < 5; j++) {
                    group.children.add(new MyChildItem(j, "child " + j));
                }
                mItems.add(group);
            }
        }

        @Override
        public int getGroupCount() {
            return mItems.size();
        }

        @Override
        public int getChildCount(int groupPosition) {
            return mItems.get(groupPosition).children.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            // This method need to return unique value within all group items.
            return mItems.get(groupPosition).id;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // This method need to return unique value within the group.
            return mItems.get(groupPosition).children.get(childPosition).id;
        }

        @Override
        public MyGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group_item_for_expandable_minimal, parent, false);
            return new MyGroupViewHolder(v);
        }

        @Override
        public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_child_item_for_expandable_minimal, parent, false);
            return new MyChildViewHolder(v);
        }

        @Override
        public void onBindGroupViewHolder(MyGroupViewHolder holder, int groupPosition, int viewType) {
            MyGroupItem group = mItems.get(groupPosition);
            holder.textView.setText(group.text);
        }

        @Override
        public void onBindChildViewHolder(MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
            MyChildItem child = mItems.get(groupPosition).children.get(childPosition);
            holder.textView.setText(child.text);
        }

        @Override
        public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
            return true;
        }
    }
}
