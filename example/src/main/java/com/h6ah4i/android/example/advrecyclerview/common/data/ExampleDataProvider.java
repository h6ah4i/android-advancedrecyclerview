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

public class ExampleDataProvider extends AbstractDataProvider {
    private List<ConcreteData> mData;
    private ConcreteData mLastRemovedData;
    private int mLastRemovedPosition = -1;

    public ExampleDataProvider(boolean simple) {
        final String atoz = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        mData = new LinkedList<>();

        if (simple) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < atoz.length(); j++) {
                    final long id = mData.size();
                    final int viewType = 0;
                    final String text = Character.toString(atoz.charAt(j));
                    final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_LEFT | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_RIGHT;
                    mData.add(new ConcreteData(id, viewType, text, swipeReaction, simple));
                }
            }
        } else {
            final int[] swipeReactionTable = {
                    RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_LEFT | RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_RIGHT,
                    RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_LEFT | RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT,
                    RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_LEFT | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_RIGHT,
                    RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT | RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_RIGHT,
                    RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT | RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT,
                    RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_RIGHT,
                    RecyclerViewSwipeManager.REACTION_CAN_SWIPE_LEFT | RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_RIGHT,
                    RecyclerViewSwipeManager.REACTION_CAN_SWIPE_LEFT | RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT,
                    RecyclerViewSwipeManager.REACTION_CAN_SWIPE_LEFT | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_RIGHT,
            };

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < atoz.length(); j++) {
                    final long id = mData.size();
                    final int viewType = j % 2;
                    final String text = Character.toString(atoz.charAt(j));
                    final int swipeReaction = swipeReactionTable[j % swipeReactionTable.length];
                    mData.add(new ConcreteData(id, viewType, text, swipeReaction, simple));
                }
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
            int insertedPosition = mLastRemovedPosition;
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
        final ConcreteData removedItem = mData.remove(position);

        mLastRemovedData = removedItem;
        mLastRemovedPosition = position;
    }

    public static final class ConcreteData extends Data {

        private final long mId;
        private final String mText;
        private final int mViewType;
        private final int mSwipeReaction;
        private boolean mPinnedToSwipeLeft;

        ConcreteData(long id, int viewType, String text, int swipeReaction, boolean simplified) {
            mId = id;
            mViewType = viewType;
            mText = makeText(id, text, swipeReaction, simplified);
            mSwipeReaction = swipeReaction;
        }

        private static String makeText(long id, String text, int swipeReaction, boolean simplified) {
            final StringBuilder sb = new StringBuilder();

            sb.append(id + " - ");
            sb.append(text);

            if (!simplified) {
                sb.append("\n");

                sb.append("(LEFT: ");
                switch (swipeReaction & 0x03) {
                    case RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_LEFT:
                        sb.append("disabled");
                        break;
                    case RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT:
                        sb.append("rubber band effect");
                        break;
                    case RecyclerViewSwipeManager.REACTION_CAN_SWIPE_LEFT:
                        sb.append("enabled");
                        break;
                }
                sb.append(", RIGHT: ");
                switch (swipeReaction & (0x03 << 16)) {
                    case RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_RIGHT:
                        sb.append("disabled");
                        break;
                    case RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT:
                        sb.append("rubber band effect");
                        break;
                    case RecyclerViewSwipeManager.REACTION_CAN_SWIPE_RIGHT:
                        sb.append("enabled");
                        break;
                }
                sb.append(")");
            }

            return sb.toString();
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
