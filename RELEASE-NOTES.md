## 0.11.0
[Breaking changes]
- Changed minimum SDK level to v14
- New callbacks are added to `SwipeableItemAdapter` and `DraggableItemAdapter`

[New features & improvements]
- Bumped Support libraries to v27.0.0
- A new callback `onItemSwipeStarted()` is added to `SwipeableItemAdapter` to reduce implicitly calls of the `notifyDataSetChanged()` method.

  Migration code:
  ```java
  @Override
  public void onSwipeItemStarted(MyViewHolder holder, int position) {
      notifyDataSetChanged(); // or you can implement better invalidation code here
  }
  ```

- New callbacks `onItemDragStarted()` and `onItemDragFinished()` are added to `DraggableItemAdapter` to reduce implicitly calls of the `notifyDataSetChanged()` method.

  Migration code:
  ```java
  @Override
  public void onItemDragStarted(int position) {
      notifyDataSetChanged(); // or you can implement better invalidation code here
  }

  @Override
  public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
      notifyDataSetChanged(); // or you can implement better invalidation code here
  }
  ```

## 0.10.6
[Bug fixes]
- Fixed some internal wrapper adapter's onViewRecycled() method is not invoked (issue #376)
- Fixed unexpected onClick() event fired after finished dragging item (issue #378)

[New features]
- Added RecyclerViewSwipeManager.performFakeSwiping (issue #372)

[Improvements]
- Changed to preserve item scaling when starts dragging (issue #384)


## 0.10.5
[Bug fixes]
- Fixed View.OVER_SCROLL_NEVER not work when dragging (issue #374)

[Improvements]
- Expose path segments for header footer adapter (PR #368, #373)
- Updated support library to v25.3.0


## 0.10.4
[BREAKING CHANGE]
- The `payload` parameter is added to `OnGroupExpandListener` and `OnGroupCollapseListener` (issue #350)
  (commit: 353406ea43657dead1ba65207b95e9067e457f6d)

[New features]
- Added fine control of initial state of group items by
  `ExpandableItemAdapter.getInitialGroupExpandedState()`  (issue #346)
- Added variants of `expandGroup()`/`collapseGroup()` methods which has a `payload` parameter (issue #350)

[Bug fixes]
- Fixed NullPointerException issue (issue #358 & PR #362, thanks @polyak01 !)
- Fix swipe amount not applied before laid out item views in proportional mode (issue #361)

[Improvements]
- Added scrolling support while dragging in NestedScrollView (issue #351)
- Updated support library to v25.2.0



## 0.10.3
[Bug fixes]
- Fixed IllegalStateException on touching a group item while RecyclerView is calculating layout (issue #339)
- Fixed onBindGroupViewHolder()/onBindChildViewHolder() method with palyloads parameter not used bug

[Improvements]
- Reduce overdraws of "Button under swipeable item" (PR #331, thx. @AnirudhaAgashe)
- Updated support library to v25.1.0


## 0.10.2
[Bug fixes]
- Fixed createDraggingItemImage() method regression (issue #325)


## 0.10.1
[Improvements]
- Updated support library to v25.0.1

[Bug fixes]
- Backport official DefaultItemAnimator fixes to RefactoredDefaultAnimator (issue #324)
- Improve dragging item image bitmap creation process (issue #319)


## 0.10.0
[New features]
- Introduced ComposedAdapter
- Introduced Headers and Footers support
- Added new demos for new adapter related features
- Made dragging item appearance controllable (issue #193, #292)
- Added AFTER_SWIPE_REACTION_DO_NOTHING (pull request #308)
- Added SwipeResultActionDoNothing and SwipeResultActionMoveToOrigin

[Improvements]
- Updated support library to v25.0.0

[Bug fixes]
- Small bug fixes


## 0.9.3
[New features]
- Added `RecyclerViewExpandableItemManager.setDefaultGroupsExpandedState(boolean expanded)` (issue #281)
- Added `SwipebleItemViewHolder.setProportionalSwipeAmountModeEnabled(boolean enabled)` (issue #286)
- Added `RecyclerViewExpandableItemManager.notifyGroupItemChanged(int groupPosition, Object payload)`

[Bug fixes]
- Fixed item sliding animation not working bug (issue #285)


## 0.9.2
[New features]
- Added `void RecyclerViewDragDropManager.setItemMoveMode(@ItemMoveMode int mode)` (issue #253, #269)
- Added `void RecyclerViewSwipeManager.setSwipeThresholdDistance(int distanceInPixels)` (issue #266)
- Added `notifyGroupItemMoved()` and `notifyChildItemMoved()` methods to `RecyclerViewExpandableItemManager` (issue #270)
- Added `RecyclerViewDragDropManager.setInitiateOnTouch(boolean initiateOnTouch)` (issue #273)

[Improvements]
- Better handling of `notifyItem*` method calls during swiping (issue #274)
- Improved span size change handling during dragging (issue #278)

[Bug fixes]
- IllegalStateException (issue #247)
- Item loses Remove Animation properties when removed + undo (issue #262)


## 0.9.1
[Bug fixes]
- Fix broken drag and drop behavior on API level 10


## 0.9.0
[New features]
- Introduced `DraggableItemAdapter.onCheckCanDrop()` callback (issue #233)

[Improvements]
- Improved Drag & Drop behavior for StaggeredGridLayout (issue #219)
- Updated support library to v23.3.0

[Breaking changes]
- Removed deprecated classes/methods/fields marked as @Deprecated in v0.8.x
- Removed Legacy* swipable feature related classes


## 0.8.7
[New features]
- Added onItemDragMoveDistanceUpdated() callback to OnItemDragEventListener (#191)
- Added "minimal" draggable/swipeable/expandable examples
- Updated support library to v23.2.1

[Bug fixes]
- Fixed drag & drop behavior of GridLayoutManager (related to #193)


## 0.8.6
[New features]
- Added these methods to RecyclerViewExpandableItemManager (issue #161)
  - getExpandedGroupsCount()
  - getCollapsedGroupsCount()
  - isAllGroupsExpanded()
  - isAllGroupsCollapsed()

[Bug fixes]
- Fixed issue #158, #173, #176 (NullPointerException bug)
- Fixed issue #173 (crash bugs bug)
- Fixed issue #163 (Dragging with only 2 items in list lags significantly more than if it had more than 2 items.. bug)
- Fixed issue #153 (Bug:happens as the user dragging,and the item root view's margin is set bug)
- Fixed issue #152 (bug:Samsung S3 4.1.1(genymotion) with swipe left bug)


## 0.8.5
- Updated support library to v23.1.1
- Fixed issue #145 (SwipeableItemWrapperAdapter.onViewRecycled crash bug)
- Re-implement the ExpandableItemIndicator without vector-compat (related issue: #149)
- Reduced overdraws on API level v20 or lower (related PR: #146)
- Small bug fixes


## 0.8.4
- Fixed issue #142 (Recycler View crash on destroy)


## 0.8.3
- Fixed issue #131 (Bottom padding of recycler view offsets the dragging item [Drag drop] bug)
- Fixed issue #133 (Wrong item position used when data set changes during swipe bug)
- Fixed issue #136 (Item swipe completes after holder recycled bug)


## 0.8.2
- Updated support library to v23.1.0
- Added notify*Changed methods which has payload parameter


## 0.8.1
- Fixed issue #124 (Long press should cancel when recycler view scrolls)


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
