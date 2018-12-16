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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ItemViewTypeComposerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void totalBitWidth() {
        assertThat(
                ItemViewTypeComposer.BIT_WIDTH_EXPANDABLE_FLAG +
                        ItemViewTypeComposer.BIT_WIDTH_SEGMENT +
                        ItemViewTypeComposer.BIT_WIDTH_WRAPPED_VIEW_TYPE,
                is(32));
    }

    @Test
    public void bitOffsets() {
        assertThat(ItemViewTypeComposer.BIT_OFFSET_WRAPPED_VIEW_TYPE, is(0));
        assertThat(ItemViewTypeComposer.BIT_OFFSET_SEGMENT, is(ItemViewTypeComposer.BIT_WIDTH_WRAPPED_VIEW_TYPE + ItemViewTypeComposer.BIT_OFFSET_WRAPPED_VIEW_TYPE));
        assertThat(ItemViewTypeComposer.BIT_OFFSET_EXPANDABLE_FLAG, is(ItemViewTypeComposer.BIT_WIDTH_SEGMENT + ItemViewTypeComposer.BIT_OFFSET_SEGMENT));
        assertThat(ItemViewTypeComposer.BIT_OFFSET_EXPANDABLE_FLAG + ItemViewTypeComposer.BIT_WIDTH_EXPANDABLE_FLAG, is(32));
    }

    @Test
    public void bitMasks() {
        assertThat(
                ItemViewTypeComposer.BIT_MASK_EXPANDABLE_FLAG,
                is(genBitMask(ItemViewTypeComposer.BIT_WIDTH_EXPANDABLE_FLAG, ItemViewTypeComposer.BIT_OFFSET_EXPANDABLE_FLAG)));

        assertThat(
                ItemViewTypeComposer.BIT_MASK_SEGMENT,
                is(genBitMask(ItemViewTypeComposer.BIT_WIDTH_SEGMENT, ItemViewTypeComposer.BIT_OFFSET_SEGMENT)));

        assertThat(
                ItemViewTypeComposer.BIT_MASK_WRAPPED_VIEW_TYPE,
                is(genBitMask(ItemViewTypeComposer.BIT_WIDTH_WRAPPED_VIEW_TYPE, ItemViewTypeComposer.BIT_OFFSET_WRAPPED_VIEW_TYPE)));
    }

    @Test
    public void bitWidthSegment() {
        assertThat(ItemViewTypeComposer.BIT_WIDTH_SEGMENT, is(ItemIdComposer.BIT_WIDTH_SEGMENT));
    }

    @Test
    public void minMaxSegment() {
        assertThat(ItemViewTypeComposer.MIN_SEGMENT, is(0));
        assertThat(ItemViewTypeComposer.MAX_SEGMENT, is(unsignedIntMax(ItemViewTypeComposer.BIT_WIDTH_SEGMENT)));
    }

    @Test
    public void minMaxWrappedViewType() {
        assertThat(ItemViewTypeComposer.MIN_WRAPPED_VIEW_TYPE, is(signedIntMin(ItemViewTypeComposer.BIT_WIDTH_WRAPPED_VIEW_TYPE)));
        assertThat(ItemViewTypeComposer.MAX_WRAPPED_VIEW_TYPE, is(signedIntMax(ItemViewTypeComposer.BIT_WIDTH_WRAPPED_VIEW_TYPE)));
    }

    @Test
    public void extractSegmentPart() throws Exception {
        // zero
        assertThat(ItemViewTypeComposer.extractSegmentPart(0), is(0));

        // one
        assertThat(ItemViewTypeComposer.extractSegmentPart(1 << ItemViewTypeComposer.BIT_OFFSET_SEGMENT), is(1));

        // min
        assertThat(ItemViewTypeComposer.extractSegmentPart(ItemViewTypeComposer.MIN_SEGMENT << ItemViewTypeComposer.BIT_OFFSET_SEGMENT), is(ItemViewTypeComposer.MIN_SEGMENT));

        // max
        assertThat(ItemViewTypeComposer.extractSegmentPart(ItemViewTypeComposer.MAX_SEGMENT << ItemViewTypeComposer.BIT_OFFSET_SEGMENT), is(ItemViewTypeComposer.MAX_SEGMENT));

        // etc - 1
        assertThat(ItemViewTypeComposer.extractSegmentPart(ItemViewTypeComposer.BIT_MASK_EXPANDABLE_FLAG | ItemViewTypeComposer.BIT_MASK_WRAPPED_VIEW_TYPE), is(0));

        // etc - 2
        assertThat(ItemViewTypeComposer.extractSegmentPart(ItemViewTypeComposer.BIT_MASK_SEGMENT), is(ItemViewTypeComposer.MAX_SEGMENT));
    }

    @Test
    public void extractWrappedViewTypePart() throws Exception {
        // zero
        assertThat(ItemViewTypeComposer.extractWrappedViewTypePart(0), is(0));

        // one
        assertThat(ItemViewTypeComposer.extractWrappedViewTypePart(1 << ItemViewTypeComposer.BIT_OFFSET_WRAPPED_VIEW_TYPE), is(1));

        // full bits
        assertThat(ItemViewTypeComposer.extractWrappedViewTypePart(ItemViewTypeComposer.BIT_MASK_WRAPPED_VIEW_TYPE), is(-1));

        // min
        assertThat(ItemViewTypeComposer.extractWrappedViewTypePart(ItemViewTypeComposer.MIN_WRAPPED_VIEW_TYPE << ItemViewTypeComposer.BIT_OFFSET_WRAPPED_VIEW_TYPE), is(ItemViewTypeComposer.MIN_WRAPPED_VIEW_TYPE));

        // max
        assertThat(ItemViewTypeComposer.extractWrappedViewTypePart(ItemViewTypeComposer.MAX_WRAPPED_VIEW_TYPE << ItemViewTypeComposer.BIT_OFFSET_WRAPPED_VIEW_TYPE), is(ItemViewTypeComposer.MAX_WRAPPED_VIEW_TYPE));

        // etc - 1
        assertThat(ItemViewTypeComposer.extractWrappedViewTypePart(ItemViewTypeComposer.BIT_MASK_EXPANDABLE_FLAG), is(0));

        // etc - 2
        assertThat(ItemViewTypeComposer.extractWrappedViewTypePart(ItemViewTypeComposer.BIT_MASK_SEGMENT), is(0));
    }

    @Test
    public void isExpandableGroup() throws Exception {
        // zero
        assertThat(ItemViewTypeComposer.isExpandableGroup(0), is(false));

        // not group - 1
        assertThat(ItemViewTypeComposer.isExpandableGroup(ItemViewTypeComposer.BIT_MASK_SEGMENT), is(false));

        // not group - 2
        assertThat(ItemViewTypeComposer.isExpandableGroup(ItemViewTypeComposer.BIT_MASK_WRAPPED_VIEW_TYPE), is(false));

        // is group - 1
        assertThat(ItemViewTypeComposer.isExpandableGroup(ItemViewTypeComposer.BIT_MASK_EXPANDABLE_FLAG), is(true));

        // is group - 2
        assertThat(ItemViewTypeComposer.isExpandableGroup(ItemViewTypeComposer.BIT_MASK_EXPANDABLE_FLAG | ItemViewTypeComposer.BIT_MASK_SEGMENT | ItemViewTypeComposer.BIT_MASK_WRAPPED_VIEW_TYPE), is(true));
    }

    @Test
    public void composeSegment() throws Exception {
        // zero
        assertThat(ItemViewTypeComposer.composeSegment(0, 0), is(0));

        // one
        assertThat(ItemViewTypeComposer.composeSegment(1, 0), is(1 << ItemViewTypeComposer.BIT_OFFSET_SEGMENT));

        // min
        assertThat(ItemViewTypeComposer.composeSegment(ItemViewTypeComposer.MIN_SEGMENT, 0), is(ItemViewTypeComposer.MIN_SEGMENT << ItemViewTypeComposer.BIT_OFFSET_SEGMENT));

        // max
        assertThat(ItemViewTypeComposer.composeSegment(ItemViewTypeComposer.MAX_SEGMENT, 0), is(ItemViewTypeComposer.MAX_SEGMENT << ItemViewTypeComposer.BIT_OFFSET_SEGMENT));
    }

    @Test
    public void composeSegment_MinOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        //noinspection Range
        ItemViewTypeComposer.composeSegment(ItemViewTypeComposer.MIN_SEGMENT - 1, 0);
    }

    @Test
    public void composeSegment_MaxOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        //noinspection Range
        ItemViewTypeComposer.composeSegment(ItemViewTypeComposer.MAX_SEGMENT + 1, 0);
    }

    private static int genBitMask(int width, int offset) {
        return ((1 << width) - 1) << offset;
    }

    private static int unsignedIntMax(int bitWidth) {
        return (1 << bitWidth) - 1;
    }

    private static int signedIntMax(int bitWidth) {
        return (1 << (bitWidth - 1)) - 1;
    }

    private static int signedIntMin(int bitWidth) {
        return (-1 & ~signedIntMax(bitWidth));
    }
}
