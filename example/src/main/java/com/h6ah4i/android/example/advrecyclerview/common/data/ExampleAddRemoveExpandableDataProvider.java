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


import java.util.LinkedList;
import java.util.List;

public class ExampleAddRemoveExpandableDataProvider extends AbstractAddRemoveExpandableDataProvider {
    private static String GROUP_ITEM_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String CHILD_ITEM_CHARS = "abcdefghijklmnopqrstuvwxyz";


    private List<GroupSet> mData;
    private IdGenerator mGroupIdGenerator;

    public ExampleAddRemoveExpandableDataProvider() {
        mData = new LinkedList<>();
        mGroupIdGenerator = new IdGenerator();

        for (int i = 0; i < 1; i++) {
            addGroupItem(i);
            for (int j = 0; j < 3; j++) {
                addChildItem(i, j);
            }
        }
    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mData.get(groupPosition).mChildren.size();
    }

    @Override
    public GroupData getGroupItem(int groupPosition) {
        if (groupPosition < 0 || groupPosition >= getGroupCount()) {
            throw new IndexOutOfBoundsException("groupPosition = " + groupPosition);
        }

        return mData.get(groupPosition).mGroup;
    }

    @Override
    public ChildData getChildItem(int groupPosition, int childPosition) {
        if (groupPosition < 0 || groupPosition >= getGroupCount()) {
            throw new IndexOutOfBoundsException("groupPosition = " + groupPosition);
        }

        final List<ConcreteChildData> children = mData.get(groupPosition).mChildren;

        if (childPosition < 0 || childPosition >= children.size()) {
            throw new IndexOutOfBoundsException("childPosition = " + childPosition);
        }

        return children.get(childPosition);
    }

    @Override
    public void removeGroupItem(int groupPosition) {
        mData.remove(groupPosition);
    }

    @Override
    public void removeChildItem(int groupPosition, int childPosition) {
        mData.get(groupPosition).mChildren.remove(childPosition);
    }

    @Override
    public void addGroupItem(int groupPosition) {
        long id = mGroupIdGenerator.next();
        String text = getOneCharString(GROUP_ITEM_CHARS, id);
        ConcreteGroupData newItem = new ConcreteGroupData(id, text);

        mData.add(groupPosition, new GroupSet(newItem));
    }

    @Override
    public void addChildItem(int groupPosition, int childPosition) {
        mData.get(groupPosition).addNewChildData(childPosition);
    }

    @Override
    public void clear() {
        mData.clear();
    }

    @Override
    public void clearChildren(int groupPosition) {
        mData.get(groupPosition).mChildren.clear();
    }

    public static final class ConcreteGroupData extends GroupData {
        private final long mId;
        private final String mText;

        ConcreteGroupData(long id, String text) {
            mId = id;
            mText = text;
        }

        @Override
        public long getGroupId() {
            return mId;
        }

        @Override
        public String getText() {
            return mText;
        }
    }

    public static final class ConcreteChildData extends ChildData {
        private long mId;
        private final String mText;

        ConcreteChildData(long id, String text) {
            mId = id;
            mText = text;
        }

        @Override
        public long getChildId() {
            return mId;
        }

        @Override
        public String getText() {
            return mText;
        }
    }


    private static String getOneCharString(String str, long index) {
        return Character.toString(str.charAt((int) (index % str.length())));
    }

    private static class IdGenerator {
        long mId;

        public long next() {
            final long id = mId;
            mId += 1;
            return id;
        }
    }

    private static class GroupSet {
        private ConcreteGroupData mGroup;
        private List<ConcreteChildData> mChildren;
        private IdGenerator mChildIdGenerator;

        public GroupSet(ConcreteGroupData group) {
            mGroup = group;
            mChildren = new LinkedList<>();
            mChildIdGenerator = new IdGenerator();
        }

        public void addNewChildData(int position) {
            long id = mChildIdGenerator.next();
            String text = getOneCharString(CHILD_ITEM_CHARS, id);
            ConcreteChildData child = new ConcreteChildData(id, text);

            mChildren.add(position, child);
        }
    }
}
