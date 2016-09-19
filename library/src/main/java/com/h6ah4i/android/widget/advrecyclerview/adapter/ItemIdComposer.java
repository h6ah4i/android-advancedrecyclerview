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

import android.support.annotation.IntRange;
import android.support.v7.widget.RecyclerView;

/**
 * Utility class providing "Composed item ID" related definitions and methods.
 * <p>
 * Spec:
 * <table summary="Bit usages of composed item ID">
 * <tr><th>bit 63</th><td>Reserved</td></tr>
 * <tr><th>bit 62-56</th><td>View type segment</td></tr>
 * <tr><th>bit 55-28</th><td>Group ID</td></tr>
 * <tr><th>bit 27-0</th><td>Child ID</td></tr>
 * </table>
 * </p>
 */
public class ItemIdComposer {

    /**
     * Bit offset of the reserved sign flag part.
     */
    public static final int BIT_OFFSET_RESERVED_SIGN_FLAG = 63;

    /**
     * Bit offset of the segment part.
     */
    public static final int BIT_OFFSET_SEGMENT = 56;

    /**
     * Bit offset of the group ID part.
     */
    public static final int BIT_OFFSET_GROUP_ID = 28;

    /**
     * Bit offset of the child ID part.
     */
    public static final int BIT_OFFSET_CHILD_ID = 0;

    // ---

    /**
     * Bit width of the reserved sign flag part.
     */
    public static final int BIT_WIDTH_RESERVED_SIGN_FLAG = 1;

    /**
     * Bit width of the segment part.
     */
    public static final int BIT_WIDTH_SEGMENT = 7;

    /**
     * Bit width of the expandable group ID part.
     */
    public static final int BIT_WIDTH_GROUP_ID = 28;

    /**
     * Bit width of the expandable child ID part.
     */
    public static final int BIT_WIDTH_CHILD_ID = 28;

    // ---

    /**
     * Bit mask of the reserved sign flag part.
     */
    public static final long BIT_MASK_RESERVED_SIGN_FLAG = ((1L << BIT_WIDTH_RESERVED_SIGN_FLAG) - 1) << BIT_OFFSET_RESERVED_SIGN_FLAG;

    /**
     * Bit mask of the segment part.
     */
    public static final long BIT_MASK_SEGMENT = ((1L << BIT_WIDTH_SEGMENT) - 1) << BIT_OFFSET_SEGMENT;

    /**
     * Bit mask of the expandable group ID part.
     */
    public static final long BIT_MASK_GROUP_ID = ((1L << BIT_WIDTH_GROUP_ID) - 1) << BIT_OFFSET_GROUP_ID;

    /**
     * Bit mask of the expandable child ID part.
     */
    public static final long BIT_MASK_CHILD_ID = ((1L << BIT_WIDTH_CHILD_ID) - 1) << BIT_OFFSET_CHILD_ID;

    // ---

    /**
     * Minimum value of segment.
     */
    public static final int MIN_SEGMENT = 0;

    /**
     * Maximum value of segment.
     */
    public static final int MAX_SEGMENT = (1 << BIT_WIDTH_SEGMENT) - 1;

    /**
     * Minimum value of group ID.
     */
    public static final long MIN_GROUP_ID = -(1L << (BIT_WIDTH_GROUP_ID - 1));

    /**
     * Maximum value of group ID.
     */
    public static final long MAX_GROUP_ID = (1L << (BIT_WIDTH_GROUP_ID - 1)) - 1;

    /**
     * Minimum value of child ID.
     */
    public static final long MIN_CHILD_ID = -(1L << (BIT_WIDTH_CHILD_ID - 1));

    /**
     * Maximum value of child ID.
     */
    public static final long MAX_CHILD_ID = (1L << (BIT_WIDTH_CHILD_ID - 1)) - 1;

    /**
     * Minimum value of wrapped ID (= group + child) ID.
     */
    public static final long MIN_WRAPPED_ID = -(1L << (BIT_WIDTH_GROUP_ID + BIT_WIDTH_CHILD_ID - 1));

    /**
     * Minimum value of wrapped ID (= group + child) ID.
     */
    public static final long MAX_WRAPPED_ID = (1L << (BIT_WIDTH_GROUP_ID + BIT_WIDTH_CHILD_ID - 1)) - 1;


    private ItemIdComposer() {
    }

    /**
     * Makes a composed ID which represents a child item of an expandable group.
     *
     * @param groupId Group item ID
     * @param childId Child item ID
     * @return Composed expandable child ID
     */
    public static long composeExpandableChildId(@IntRange(from = MIN_GROUP_ID, to = MAX_GROUP_ID) long groupId, @IntRange(from = MIN_CHILD_ID, to = MAX_CHILD_ID) long childId) {
        if (groupId < MIN_GROUP_ID || groupId > MAX_GROUP_ID) {
            throw new IllegalArgumentException("Group ID value is out of range. (groupId = " + groupId + ")");
        }
        if (childId < MIN_CHILD_ID || childId > MAX_CHILD_ID) {
            throw new IllegalArgumentException("Child ID value is out of range. (childId = " + childId + ")");
        }

        //noinspection PointlessBitwiseExpression
        return ((groupId << BIT_OFFSET_GROUP_ID) & BIT_MASK_GROUP_ID) | ((childId << BIT_OFFSET_CHILD_ID) & BIT_MASK_CHILD_ID);
    }

    /**
     * Makes a composed ID which represents an expandable group item.
     *
     * @param groupId Group item ID
     * @return Composed expandable group ID
     */
    public static long composeExpandableGroupId(@IntRange(from = MIN_GROUP_ID, to = MAX_GROUP_ID) long groupId) {
        if (groupId < MIN_GROUP_ID || groupId > MAX_GROUP_ID) {
            throw new IllegalArgumentException("Group ID value is out of range. (groupId = " + groupId + ")");
        }

        //noinspection PointlessBitwiseExpression
        return ((groupId << BIT_OFFSET_GROUP_ID) & BIT_MASK_GROUP_ID) | ((RecyclerView.NO_ID << BIT_OFFSET_CHILD_ID) & BIT_MASK_CHILD_ID);
    }

    /**
     * Checks the composed ID is a expandable group or not.
     *
     * @param composedId Composed ID
     * @return True if the specified composed ID is an expandable group ID. Otherwise, false.
     */
    public static boolean isExpandableGroup(long composedId) {
        return (composedId != RecyclerView.NO_ID) && ((composedId & BIT_MASK_CHILD_ID) == BIT_MASK_CHILD_ID);
    }

    /**
     * Extracts "Segment" part from composed ID.
     *
     * @param composedId Composed ID
     * @return Segment part
     */
    @IntRange(from = MIN_SEGMENT, to = MAX_SEGMENT)
    public static int extractSegmentPart(long composedId) {
        return (int) ((composedId & BIT_MASK_SEGMENT) >>> BIT_OFFSET_SEGMENT);
    }

    /**
     * Extracts "Group ID" part from composed ID.
     *
     * @param composedId Composed ID
     * @return Group ID part. If the specified composed ID is not an expandable group, returns {@link RecyclerView#NO_ID}.
     */
    @IntRange(from = MIN_GROUP_ID, to = MAX_GROUP_ID)
    public static long extractExpandableGroupIdPart(long composedId) {
        if ((composedId == RecyclerView.NO_ID) || !isExpandableGroup(composedId)) {
            return RecyclerView.NO_ID;
        }
        return (composedId << (64 - BIT_WIDTH_GROUP_ID - BIT_OFFSET_GROUP_ID)) >> (64 - BIT_WIDTH_GROUP_ID);
    }

    /**
     * Extracts "Child ID" part from composed ID.
     *
     * @param composedId Composed ID
     * @return Child ID part. If the specified composed ID is not a child of an expandable group, returns {@link RecyclerView#NO_ID}.
     */
    @IntRange(from = MIN_CHILD_ID, to = MAX_CHILD_ID)
    public static long extractExpandableChildIdPart(long composedId) {
        if ((composedId == RecyclerView.NO_ID) || isExpandableGroup(composedId)) {
            return RecyclerView.NO_ID;
        }
        return (composedId << (64 - BIT_WIDTH_CHILD_ID - BIT_OFFSET_CHILD_ID)) >> (64 - BIT_WIDTH_CHILD_ID);
    }

    /**
     * Extracts "Wrapped ID" (group ID + child ID) part from composed ID.
     *
     * @param composedId Composed ID
     * @return Wrapped ID part.
     */
    public static long extractWrappedIdPart(long composedId) {
        if (composedId == RecyclerView.NO_ID) {
            return RecyclerView.NO_ID;
        }

        return (composedId << (64 - BIT_WIDTH_GROUP_ID - BIT_WIDTH_CHILD_ID - BIT_OFFSET_CHILD_ID)) >> (64 - (BIT_WIDTH_GROUP_ID + BIT_WIDTH_CHILD_ID));
    }

    /**
     * Makes a composed ID with specified segment and wrapped ID.
     *
     * @param segment   Segment
     * @param wrappedId Wrapped ID
     * @return Composed ID.
     */
    public static long composeSegment(@IntRange(from = MIN_SEGMENT, to = MAX_SEGMENT) int segment, long wrappedId) {
        if (segment < MIN_SEGMENT || segment > MAX_SEGMENT) {
            throw new IllegalArgumentException("Segment value is out of range. (segment = " + segment + ")");
        }

        return (((long) segment) << BIT_OFFSET_SEGMENT) | (wrappedId & (BIT_MASK_RESERVED_SIGN_FLAG | BIT_MASK_GROUP_ID | BIT_MASK_CHILD_ID));
    }
}
