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

import java.util.Collections;
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

        mData = new LinkedList<>();

        for (int i = 0; i < sectionItems.length(); i++) {
            // put section header
            {
                final long id = mData.size();
                final int viewType = ITEM_VIEW_TYPE_SECTION_HEADER;
                final String text = "Section " + Character.toString(sectionItems.charAt(i));
                mData.add(new ConcreteData(id, true, viewType, text));
            }

            // put section child items
            for (int j = 0; j < innerSectionItems.length(); j++) {
                final long id = mData.size();
                final int viewType = ITEM_VIEW_TYPE_SECTION_ITEM;
                final String text = Character.toString(innerSectionItems.charAt(j));
                mData.add(new ConcreteData(id, false, viewType, text));
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
    public void swapItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        Collections.swap(mData, fromPosition, toPosition);
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
        private boolean mPinned;

        ConcreteData(long id, boolean isSectionHeader, int viewType, String text) {
            mId = id;
            mIsSectionHeader = isSectionHeader;
            mViewType = viewType;
            mText = text;
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
        public String getText() {
            return mText;
        }

        @Override
        public boolean isPinned() {
            return mPinned;
        }

        @Override
        public void setPinned(boolean pinned) {
            mPinned = pinned;
        }
    }
}
