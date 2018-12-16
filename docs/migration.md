## **v1.0.0** (from v0.11.0)

!!! attention ""
    The v1.0.0 has been migrated to AndroidX. Please use **v0.11.0** instead if your project uses support libraries.

### `BaseWrapperAdapter` has been removed

```diff
- class MyAdapter extends BaseWrapperAdapter { ... }
+ class MyAdapter extends SimpleWrapperAdapter { ... }
```

### Some constants in `SwipeableItemConstants` has been removed

```diff
- SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH
+ SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H
```

```diff
- SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH
+ SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_H
```

```diff
- SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_WITH_RUBBER_BAND_EFFECT
+ SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_H_WITH_RUBBER_BAND_EFFECT
```

### New `getDragState()` method added to the `DraggableItemViewHolder` interface

```diff
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
         // set background resource (target view ID: container)
-        final int dragState = holder.getDragStateFlags();
+        final DraggableItemState dragState = holder.getDragState();

-        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0)) {
+        if (dragState.isUpdated()) {
             int bgResId;

-            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
+            if (dragState.isActive()) {
                 bgResId = R.drawable.bg_item_dragging_active_state;

                 // need to clear drawable state here to get correct appearance of the dragging item.
                 DrawableUtils.clearState(holder.mContainer.getForeground());
-            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
+            } else if (dragState.isDragging()) {
                 bgResId = R.drawable.bg_item_dragging_state;
             } else {
                 bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }
    }
```

### New `getSwipeState()` method added to the `SwipeableItemViewHolder` interface

```diff
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // set background resource (target view ID: container)
-        final int swipeState = holder.getSwipeStateFlags();
+        final SwipeableItemState swipeState = holder.getSwipeState();

-        if ((swipeState & Swipeable.STATE_FLAG_IS_UPDATED) != 0) {
+        if (swipeState.isUpdated()) {
             int bgResId;

-            if ((swipeState & Swipeable.STATE_FLAG_IS_ACTIVE) != 0) {
+            if (swipeState.isActive()) {
                 bgResId = R.drawable.bg_item_swiping_active_state;
-            } else if ((swipeState & Swipeable.STATE_FLAG_SWIPING) != 0) {
+            } else if (swipeState.isSwiping()) {
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }

        // set swiping properties
        holder.setSwipeItemHorizontalSlideAmount(
                item.isPinned() ? Swipeable.OUTSIDE_OF_THE_WINDOW_LEFT : 0);
    }
```

### New `getExpandState()` method added to the `ExpandableItemViewHolder` interface

```diff
    @Override
    public void onBindGroupViewHolder(@NonNull MyGroupViewHolder holder, int groupPosition, int viewType) {
         // set background resource (target view ID: container)
-        final int expandState = holder.getExpandStateFlags();
+        final ExpandableItemState expandState = holder.getExpandState();

-        if ((expandState & ExpandableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
+        if (expandState.isUpdated()) {
             int bgResId;
-            boolean isExpanded;
-            boolean animateIndicator = ((expandState & Expandable.STATE_FLAG_HAS_EXPANDED_STATE_CHANGED) != 0);
+            boolean animateIndicator = expandState.hasExpandedStateChanged();

-            if ((expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0) {
+            if (expandState.isExpanded()) {
                 bgResId = R.drawable.bg_group_item_expanded_state;
-                isExpanded = true;
             } else {
                 bgResId = R.drawable.bg_group_item_normal_state;
-                isExpanded = false;
             }

             holder.mContainer.setBackgroundResource(bgResId);
-            holder.mIndicator.setExpandedState(isExpanded, animateIndicator);
+            holder.mIndicator.setExpandedState(expandState.isExpanded(), animateIndicator);
         }
     }

```

