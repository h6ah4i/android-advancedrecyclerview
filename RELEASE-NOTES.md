## 0.7.2
- [Important] Updated support-v7-recyclerview v22.2.0
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
