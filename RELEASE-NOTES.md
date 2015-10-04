## 0.8.0
[Main new features and improvements]
- Support GridLayoutManager (drag & drop, swipe)   (issue #41, #67, #86)
- Support horizontal layout orientaiton (LinearLayoutManager and GridLayoutManager)   (issue #116)
- Organized demo launcher screen

[Changes of RecyclerViewExpandableItemManager]
- Added the expandAll()/collapseAll() methods   (issue #100)
- Added variant of the notifyGroupItemInseterd()/notifyGroupItemRangeInseterd() methods   (issue #100)
- Added the getGroupCount()/getChildCount() methods
- Added the scrollToGroup() method   (issue #60)
- Added the STATE_FLAG_HAS_EXPANDED_STATE_CHANGED state flag

[Changes of RecyclerViewSwipeManager]
- Added vertical swipe feature   (issue #116)
- Introduced the SwipeResultAction object   (issue #69)
  - This is a BREAKING CHANGE. Please refer to the Migration Guide section on README.

[Changes of RecyclerViewDragDropManager]
- Added the setDragEdgeScrollSpeed() and getDragEdgeScrollSpeed()   (issue #85)

[New demos]
- "Draggable (Grid Layout)"
- "Expandable (Groups already expanded)"
- "Swipeable (Vertical)"
- "Swipeable with ViewPager"
- "Swipeable (Legacy)"
- "Swipe on Long Press"

[Fixed issues]
- Fixed issue #41 (Swipe not working if RecyclerView is using a GridLayout)
- Fixed issue #60 (Scroll to the last child if it is offscreen)
- Fixed issue #67 (GridLayoutManager support)
- Fixed issue #69 (Dispatch events to parent when can't swipe to a direction)
- Fixed issue #75 (Error In -->Expandable(Add/Remove Items))
- Fixed issue #85 (Drag & drop edge scroll speed)
- Fixed issue #86 (Require help in implementing drag and drop for Grid View)
- Fixed issue #98 (Callback method when swipe animation is fully completed?)
- Fixed issue #100 (How to expand all group by default?)
- Fixed issue #111 (Drag is not working when a drag handle is small)
- Fixed issue #116 (can i build a horizontal recyclerview?)
- Fixed issue #118 (How to swipe on LongClick? enhancement)
- Fixed some other minor issues


## 0.7.4
- Fixed issue #75 (Error In -->Expandable(Add/Remove Items))

## 0.7.3
- Updated to support-v7-recyclerview v22.2.1
- Added a new demo "Expandable (Add/Remove items)"
- Added the RecyclerViewDragDropManager.OnItemDragEventListener
- Added the RecyclerViewSwipeManager.OnItemSwipeEventListener
- Added methods of notify*Inserted() and notify*Remove() families to RecyclerViewExpandableItemManager
- Renamed getInitiateOnLongPress()/getInitiateOnMove() methods to isInitiateOnLongPressEnabled()/isInitiateOnMoveEnabled()
- Fixed issue #54 (ClassCastException, MyPlatformChildViewHolder cannot be MyPlatformGroupViewHolder)
- Fixed issue #64 (After swipe/remove of a group crash in some cases)
- Fixed issue #65 (Is there any way to detect drag start and drag end)
- Fixed some other minor bugs

## 0.7.2
- [Important] Updated to support-v7-recyclerview v22.2.0
- Fixed issue #44 (Click effect on GroupView)
- Fixed issue #50 (CPU usage 30-40% while Idle)
- Merged pull request #55 (Makes ItemAnimators more easily extensible.)

## 0.7.1

- Removed TestActivity from manifest file
- Added expanded/collapsed status indicators to demo app

## 0.7.0

[Breaking changes]
- Changed method signatures of SwipeableItemAdapter (commit: `56e0084342f6e86ae26078f7c329368e2a59b92b`)
- Changed method signatures of DraggableItemAdapter (commit: `56e0084342f6e86ae26078f7c329368e2a59b92b`)

[Improvements]
- Upgraded support library to v22.1.1
- Fixed issue #3 (ClassCastException bug)
- Fixed issue #13 (Can't set child item draggable range in children group)
- Fixed issue #23 (overscrolling effect)
- Fixed issue #29 (Begin drag and drop using OnLongClick
- Fixed issue #30 (Enhancement to Swiping)
- Merged pull request from @jcamp1 (related to #29, #30)

## 0.6.2

- Added drag-sortable range configuration feature
- Added new two examples which has sections headers in the list and using new APIs
- Added RecyclerView's event distributor mechanism (no examples yet.)
- Improved JavaDoc comments
- Small bug fixes & improvements

## 0.6.1

- Added "settle back into place" animation
- Fix wrong destination position bug when moving an child item on certain condition (05c5f0a0db423698599ae2e85436c595f8b7a5c4)
- Small bug fixes
- Fix Android Studio Lint warnings

## 0.6.0

- Introduced "Expandable item" feature
- Minor bug fixes

## 0.5.0

- Initial release
