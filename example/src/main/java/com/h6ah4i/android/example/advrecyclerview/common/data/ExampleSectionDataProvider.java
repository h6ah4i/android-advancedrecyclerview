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

package com.h6ah4i.android.example.advrecyclerview.common.data;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

import java.util.LinkedList;
import java.util.List;

public class ExampleSectionDataProvider extends AbstractDataProvider {
    private List<ConcreteData> mData;
    private ConcreteData mLastRemovedData;
    private int mLastRemovedPosition = -1;

    public static final int ITEM_VIEW_TYPE_SECTION_HEADER = 0;
    public static final int ITEM_VIEW_TYPE_SECTION_ITEM = 1;

    public ExampleSectionDataProvider() {
        final String sectionItems = "ABCDE";
        final String innerSectionItems = "abc";
//        final String innerSectionItems = "abcdefghijklmnopqrstuvwxyz";

        mData = new LinkedList<>();

        for (int i = 0; i < sectionItems.length(); i++) {
            // put section header
            {
                final long id = mData.size();
                final int viewType = ITEM_VIEW_TYPE_SECTION_HEADER;
                final String text = "Section " + Character.toString(sectionItems.charAt(i));
                final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_BOTH;
                mData.add(new ConcreteData(id, true, viewType, text, swipeReaction));
            }

            // put section child items
            for (int j = 0; j < innerSectionItems.length(); j++) {
                final long id = mData.size();
                final int viewType = ITEM_VIEW_TYPE_SECTION_ITEM;
                final String text = Character.toString(innerSectionItems.charAt(j));
                final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_LEFT | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_RIGHT;
                mData.add(new ConcreteData(id, false, viewType, text, swipeReaction));
            }
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Data getItem(int index) {
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("index = " + index);
        }

        return mData.get(index);
    }

    @Override
    public int undoLastRemoval() {
        if (mLastRemovedData != null) {
            int insertedPosition;
            if (mLastRemovedPosition >= 0 && mLastRemovedPosition < mData.size()) {
                insertedPosition = mLastRemovedPosition;
            } else {
                insertedPosition = mData.size();
            }

            mData.add(insertedPosition, mLastRemovedData);

            mLastRemovedData = null;
            mLastRemovedPosition = -1;

            return insertedPosition;
        } else {
            return -1;
        }
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final ConcreteData item = mData.remove(fromPosition);

        mData.add(toPosition, item);
        mLastRemovedPosition = -1;
    }

    @Override
    public void removeItem(int position) {
        //noinspection UnnecessaryLocalVariable
        final ConcreteData removedItem = mData.remove(position);

        mLastRemovedData = removedItem;
        mLastRemovedPosition = position;
    }

    public static final class ConcreteData extends Data {

        private final long mId;
        private final boolean mIsSectionHeader;
        private final String mText;
        private final int mViewType;
        private final int mSwipeReaction;
        private boolean mPinnedToSwipeLeft;

        ConcreteData(long id, boolean isSectionHeader, int viewType, String text, int swipeReaction) {
            mId = id;
            mIsSectionHeader = isSectionHeader;
            mViewType = viewType;
            mText = text;
            mSwipeReaction = swipeReaction;
        }

        @Override
        public boolean isSectionHeader() {
            return mIsSectionHeader;
        }

        @Override
        public int getViewType() {
            return mViewType;
        }

        @Override
        public long getId() {
            return mId;
        }

        @Override
        public String toString() {
            return mText;
        }

        @Override
        public int getSwipeReactionType() {
            return mSwipeReaction;
        }

        @Override
        public String getText() {
            return mText;
        }

        @Override
        public boolean isPinnedToSwipeLeft() {
            return mPinnedToSwipeLeft;
        }

        @Override
        public void setPinnedToSwipeLeft(boolean pinedToSwipeLeft) {
            mPinnedToSwipeLeft = pinedToSwipeLeft;
        }
    }
}
