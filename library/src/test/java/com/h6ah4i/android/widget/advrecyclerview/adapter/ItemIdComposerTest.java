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

package com.h6ah4i.android.widget.advrecyclerview.adapter;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ItemIdComposerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void totalBitWidth() {
        assertThat(
                ItemIdComposer.BIT_WIDTH_RESERVED_SIGN_FLAG +
                        ItemIdComposer.BIT_WIDTH_SEGMENT +
                        ItemIdComposer.BIT_WIDTH_GROUP_ID +
                        ItemIdComposer.BIT_WIDTH_CHILD_ID,
                is(64));
    }

    @Test
    public void bitOffsets() {
        assertThat(ItemIdComposer.BIT_OFFSET_CHILD_ID, is(0));
        assertThat(ItemIdComposer.BIT_OFFSET_GROUP_ID, is(ItemIdComposer.BIT_WIDTH_CHILD_ID + ItemIdComposer.BIT_OFFSET_CHILD_ID));
        assertThat(ItemIdComposer.BIT_OFFSET_SEGMENT, is(ItemIdComposer.BIT_WIDTH_GROUP_ID + ItemIdComposer.BIT_OFFSET_GROUP_ID));
        assertThat(ItemIdComposer.BIT_OFFSET_RESERVED_SIGN_FLAG, is(ItemIdComposer.BIT_WIDTH_SEGMENT + ItemIdComposer.BIT_OFFSET_SEGMENT));
        assertThat(ItemIdComposer.BIT_OFFSET_RESERVED_SIGN_FLAG + ItemIdComposer.BIT_WIDTH_RESERVED_SIGN_FLAG, is(64));
    }

    @Test
    public void bitMasks() {
        assertThat(
                ItemIdComposer.BIT_MASK_RESERVED_SIGN_FLAG,
                is(genBitMask(ItemIdComposer.BIT_WIDTH_RESERVED_SIGN_FLAG, ItemIdComposer.BIT_OFFSET_RESERVED_SIGN_FLAG)));

        assertThat(
                ItemIdComposer.BIT_MASK_SEGMENT,
                is(genBitMask(ItemIdComposer.BIT_WIDTH_SEGMENT, ItemIdComposer.BIT_OFFSET_SEGMENT)));

        assertThat(
                ItemIdComposer.BIT_MASK_GROUP_ID,
                is(genBitMask(ItemIdComposer.BIT_WIDTH_GROUP_ID, ItemIdComposer.BIT_OFFSET_GROUP_ID)));

        assertThat(
                ItemIdComposer.BIT_MASK_CHILD_ID,
                is(genBitMask(ItemIdComposer.BIT_WIDTH_CHILD_ID, ItemIdComposer.BIT_OFFSET_CHILD_ID)));
    }

    @Test
    public void bitWidthSegment() {
        assertThat(ItemIdComposer.BIT_WIDTH_SEGMENT, is(ItemViewTypeComposer.BIT_WIDTH_SEGMENT));
    }

    @Test
    public void minMaxSegment() {
        assertThat(ItemIdComposer.MIN_SEGMENT, is(0));
        assertThat(ItemIdComposer.MAX_SEGMENT, is(unsignedIntMax(ItemIdComposer.BIT_WIDTH_SEGMENT)));
    }

    @Test
    public void minMaxGroupId() {
        assertThat(ItemIdComposer.MIN_GROUP_ID, is(signedLongMin(ItemIdComposer.BIT_WIDTH_GROUP_ID)));
        assertThat(ItemIdComposer.MAX_GROUP_ID, is(signedLongMax(ItemIdComposer.BIT_WIDTH_GROUP_ID)));
    }

    @Test
    public void minMaxChildId() {
        assertThat(ItemIdComposer.MIN_CHILD_ID, is(signedLongMin(ItemIdComposer.BIT_WIDTH_CHILD_ID)));
        assertThat(ItemIdComposer.MAX_CHILD_ID, is(signedLongMax(ItemIdComposer.BIT_WIDTH_CHILD_ID)));
    }

    @Test
    public void minMaxWrappedId() {
        assertThat(ItemIdComposer.MIN_WRAPPED_ID, is(signedLongMin(ItemIdComposer.BIT_WIDTH_GROUP_ID + ItemIdComposer.BIT_WIDTH_CHILD_ID)));
        assertThat(ItemIdComposer.MAX_WRAPPED_ID, is(signedLongMax(ItemIdComposer.BIT_WIDTH_GROUP_ID + ItemIdComposer.BIT_WIDTH_CHILD_ID)));
    }

    @Test
    public void composeExpandableChildId() throws Exception {
        // zero
        assertThat(ItemIdComposer.composeExpandableChildId(0L, 0L), is(0L));

        // one
        assertThat(ItemIdComposer.composeExpandableChildId(1L, 0L), is(1L << ItemIdComposer.BIT_OFFSET_GROUP_ID));
        assertThat(ItemIdComposer.composeExpandableChildId(0L, 1L), is(1L << ItemIdComposer.BIT_OFFSET_CHILD_ID));

        // minus one
        assertThat(ItemIdComposer.composeExpandableChildId(-1L, 0L), is(ItemIdComposer.BIT_MASK_GROUP_ID));
        assertThat(ItemIdComposer.composeExpandableChildId(0L, -1L), is(ItemIdComposer.BIT_MASK_CHILD_ID));

        // min
        assertThat(ItemIdComposer.composeExpandableChildId(ItemIdComposer.MIN_GROUP_ID, 0L), is(genBitMask(1, (ItemIdComposer.BIT_WIDTH_GROUP_ID - 1) + ItemIdComposer.BIT_OFFSET_GROUP_ID)));
        assertThat(ItemIdComposer.composeExpandableChildId(0L, ItemIdComposer.MIN_CHILD_ID), is(genBitMask(1, (ItemIdComposer.BIT_WIDTH_CHILD_ID - 1) + ItemIdComposer.BIT_OFFSET_CHILD_ID)));

        // max
        assertThat(ItemIdComposer.composeExpandableChildId(ItemIdComposer.MAX_GROUP_ID, 0L), is(genBitMask(ItemIdComposer.BIT_WIDTH_GROUP_ID - 1, ItemIdComposer.BIT_OFFSET_GROUP_ID)));
        assertThat(ItemIdComposer.composeExpandableChildId(0L, ItemIdComposer.MAX_CHILD_ID), is(genBitMask(ItemIdComposer.BIT_WIDTH_CHILD_ID - 1, ItemIdComposer.BIT_OFFSET_CHILD_ID)));
    }

    @Test
    public void composeExpandableChildId_GroupIdMaxOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        ItemIdComposer.composeExpandableChildId(ItemIdComposer.MAX_GROUP_ID + 1, 0);
    }

    @Test
    public void composeExpandableChildId_GroupIdMinOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        ItemIdComposer.composeExpandableChildId(ItemIdComposer.MIN_GROUP_ID - 1, 0);
    }

    @Test
    public void composeExpandableChildId_ChildIdMaxOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        ItemIdComposer.composeExpandableChildId(0, ItemIdComposer.MAX_CHILD_ID + 1);
    }

    @Test
    public void composeExpandableChildId_ChildIdMinOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        ItemIdComposer.composeExpandableChildId(0, ItemIdComposer.MIN_CHILD_ID - 1);
    }

    @Test
    public void composeExpandableGroupId() throws Exception {
        // zero
        //noinspection PointlessBitwiseExpression
        assertThat(ItemIdComposer.composeExpandableGroupId(0L), is((0L << ItemIdComposer.BIT_OFFSET_GROUP_ID) | ItemIdComposer.BIT_MASK_CHILD_ID));

        // one
        assertThat(ItemIdComposer.composeExpandableGroupId(1L), is((1L << ItemIdComposer.BIT_OFFSET_GROUP_ID) | ItemIdComposer.BIT_MASK_CHILD_ID));

        // minus one
        assertThat(ItemIdComposer.composeExpandableGroupId(-1L), is(ItemIdComposer.BIT_MASK_GROUP_ID | ItemIdComposer.BIT_MASK_CHILD_ID));

        // min
        assertThat(
                ItemIdComposer.composeExpandableGroupId(ItemIdComposer.MIN_GROUP_ID),
                is(genBitMask(1, (ItemIdComposer.BIT_WIDTH_GROUP_ID - 1) + ItemIdComposer.BIT_OFFSET_GROUP_ID) | ItemIdComposer.BIT_MASK_CHILD_ID));

        // max
        assertThat(
                ItemIdComposer.composeExpandableGroupId(ItemIdComposer.MAX_GROUP_ID),
                is(genBitMask(ItemIdComposer.BIT_WIDTH_GROUP_ID - 1, ItemIdComposer.BIT_OFFSET_GROUP_ID) | ItemIdComposer.BIT_MASK_CHILD_ID));
    }

    @Test
    public void isExpandableGroup() throws Exception {
        // zero
        assertThat(ItemIdComposer.isExpandableGroup(0L), is(false));

        // NO_ID
        assertThat(ItemIdComposer.isExpandableGroup(RecyclerView.NO_ID), is(false));

        // not group - 1
        assertThat(ItemIdComposer.isExpandableGroup(ItemIdComposer.BIT_MASK_CHILD_ID >> 1), is(false));

        // not group - 2
        assertThat(ItemIdComposer.isExpandableGroup(ItemIdComposer.BIT_MASK_CHILD_ID & ~1), is(false));

        // is group - 1
        assertThat(ItemIdComposer.isExpandableGroup(ItemIdComposer.BIT_MASK_CHILD_ID), is(true));

        // is group - 2
        assertThat(ItemIdComposer.isExpandableGroup(ItemIdComposer.BIT_MASK_GROUP_ID | ItemIdComposer.BIT_MASK_CHILD_ID), is(true));
    }

    @Test
    public void extractSegmentPart() throws Exception {
        // zero
        assertThat(ItemIdComposer.extractSegmentPart(0L), is(0));

        // one
        assertThat(ItemIdComposer.extractSegmentPart(1L << ItemIdComposer.BIT_OFFSET_SEGMENT), is(1));

        // min
        assertThat(ItemIdComposer.extractSegmentPart(((long) ItemIdComposer.MIN_SEGMENT) << ItemIdComposer.BIT_OFFSET_SEGMENT), is(ItemIdComposer.MIN_SEGMENT));

        // max
        assertThat(ItemIdComposer.extractSegmentPart(((long) ItemIdComposer.MAX_SEGMENT) << ItemIdComposer.BIT_OFFSET_SEGMENT), is(ItemIdComposer.MAX_SEGMENT));

        // etc - 1
        assertThat(ItemIdComposer.extractSegmentPart(ItemIdComposer.BIT_MASK_RESERVED_SIGN_FLAG | ItemIdComposer.BIT_MASK_GROUP_ID | ItemIdComposer.BIT_MASK_CHILD_ID), is(0));

        // etc - 2
        assertThat(ItemIdComposer.extractSegmentPart(ItemIdComposer.BIT_MASK_SEGMENT), is(ItemIdComposer.MAX_SEGMENT));
    }

    @Test
    public void extractExpandableGroupIdPart() throws Exception {
        // invalid - 1
        assertThat(ItemIdComposer.extractExpandableGroupIdPart(0L), is(RecyclerView.NO_ID));

        // invalid - 2
        assertThat(ItemIdComposer.extractExpandableGroupIdPart(1), is(RecyclerView.NO_ID));

        // invalid - 3
        assertThat(ItemIdComposer.extractExpandableGroupIdPart(RecyclerView.NO_ID), is(RecyclerView.NO_ID));

        // zero
        assertThat(ItemIdComposer.extractExpandableGroupIdPart(ItemIdComposer.composeExpandableGroupId(0L)), is(0L));

        // one
        assertThat(ItemIdComposer.extractExpandableGroupIdPart(ItemIdComposer.composeExpandableGroupId(1L)), is(1L));

        // minus one
        assertThat(ItemIdComposer.extractExpandableGroupIdPart(ItemIdComposer.composeExpandableGroupId(-1L)), is(-1L));

        // min
        assertThat(ItemIdComposer.extractExpandableGroupIdPart(ItemIdComposer.composeExpandableGroupId(ItemIdComposer.MIN_GROUP_ID)), is(ItemIdComposer.MIN_GROUP_ID));

        // max
        assertThat(ItemIdComposer.extractExpandableGroupIdPart(ItemIdComposer.composeExpandableGroupId(ItemIdComposer.MAX_GROUP_ID)), is(ItemIdComposer.MAX_GROUP_ID));

        // etc - 1
        assertThat(ItemIdComposer.extractExpandableGroupIdPart(ItemIdComposer.BIT_MASK_RESERVED_SIGN_FLAG | ItemIdComposer.BIT_MASK_CHILD_ID), is(0L));

        // etc - 2
        assertThat(ItemIdComposer.extractExpandableGroupIdPart(ItemIdComposer.BIT_MASK_SEGMENT | ItemIdComposer.BIT_MASK_CHILD_ID), is(0L));
    }

    @Test
    public void extractExpandableChildIdPart() throws Exception {
        // invalid - 1
        assertThat(ItemIdComposer.extractExpandableChildIdPart(ItemIdComposer.composeExpandableGroupId(0L)), is(RecyclerView.NO_ID));

        // invalid - 2
        assertThat(ItemIdComposer.extractExpandableChildIdPart(RecyclerView.NO_ID), is(RecyclerView.NO_ID));

        // zero
        assertThat(ItemIdComposer.extractExpandableChildIdPart(ItemIdComposer.composeExpandableChildId(0L, 0L)), is(0L));

        // one
        assertThat(ItemIdComposer.extractExpandableChildIdPart(ItemIdComposer.composeExpandableChildId(0L, 1L)), is(1L));

        // minus one
        assertThat(ItemIdComposer.extractExpandableChildIdPart(ItemIdComposer.composeExpandableChildId(0L, -1L)), is(-1L));

        // min
        assertThat(ItemIdComposer.extractExpandableChildIdPart(ItemIdComposer.composeExpandableChildId(0L, ItemIdComposer.MIN_CHILD_ID)), is(ItemIdComposer.MIN_CHILD_ID));

        // max
        assertThat(ItemIdComposer.extractExpandableChildIdPart(ItemIdComposer.composeExpandableChildId(0L, ItemIdComposer.MAX_CHILD_ID)), is(ItemIdComposer.MAX_CHILD_ID));

        // etc - 1
        assertThat(ItemIdComposer.extractExpandableChildIdPart(ItemIdComposer.BIT_MASK_RESERVED_SIGN_FLAG), is(0L));

        // etc - 2
        assertThat(ItemIdComposer.extractExpandableChildIdPart(ItemIdComposer.BIT_MASK_SEGMENT), is(0L));
    }

    @Test
    public void extractWrappedIdPart() throws Exception {
        // invalid - 1
        assertThat(ItemIdComposer.extractWrappedIdPart(RecyclerView.NO_ID), is(RecyclerView.NO_ID));

        // zero
        assertThat(ItemIdComposer.extractWrappedIdPart(0L), is(0L));

        // full bits
        assertThat(ItemIdComposer.extractWrappedIdPart(ItemIdComposer.BIT_MASK_GROUP_ID | ItemIdComposer.BIT_MASK_CHILD_ID), is(-1L));

        // etc - 1
        assertThat(ItemIdComposer.extractWrappedIdPart(ItemIdComposer.BIT_MASK_RESERVED_SIGN_FLAG), is(0L));

        // etc - 2
        assertThat(ItemIdComposer.extractWrappedIdPart(ItemIdComposer.BIT_MASK_SEGMENT), is(0L));
    }

    @Test
    public void composeSegment() throws Exception {
        // zero
        assertThat(ItemIdComposer.composeSegment(0, 0L), is(0L));

        // one
        assertThat(ItemIdComposer.composeSegment(1, 0L), is(1L << ItemIdComposer.BIT_OFFSET_SEGMENT));

        // min
        assertThat(ItemIdComposer.composeSegment(ItemIdComposer.MIN_SEGMENT, 0L), is(((long) ItemIdComposer.MIN_SEGMENT) << ItemIdComposer.BIT_OFFSET_SEGMENT));

        // max
        assertThat(ItemIdComposer.composeSegment(ItemIdComposer.MAX_SEGMENT, 0L), is(((long) ItemIdComposer.MAX_SEGMENT) << ItemIdComposer.BIT_OFFSET_SEGMENT));
    }

    @Test
    public void composeSegment_MinOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        //noinspection Range
        ItemIdComposer.composeSegment(ItemIdComposer.MIN_SEGMENT - 1, 0L);
    }

    @Test
    public void composeSegment_MaxOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        //noinspection Range
        ItemIdComposer.composeSegment(ItemIdComposer.MAX_SEGMENT + 1, 0L);
    }

    private static long genBitMask(int width, int offset) {
        return ((1L << width) - 1) << offset;
    }

    private static int unsignedIntMax(int bitWidth) {
        return (1 << bitWidth) - 1;
    }

    private static long signedLongMax(int bitWidth) {
        return (1L << (bitWidth - 1)) - 1;
    }

    private static long signedLongMin(int bitWidth) {
        return (-1L & ~signedLongMax(bitWidth));
    }
}
