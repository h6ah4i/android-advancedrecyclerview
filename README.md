Advanced RecyclerView
===============

This RecyclerView extension library provides Google's Inbox app like swiping, Play Music app like drag-and-drop sorting and expandable item features. Works on API level 9 or later.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Advanced%20RecyclerView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1432)

---

### Download the example app

<a href="https://play.google.com/store/apps/details?id=com.h6ah4i.android.example.advrecyclerview">
<img alt="Get it on Google Play"
src="https://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
</a>


### Demonstration video on YouTube

<a href="http://www.youtube.com/watch?feature=player_embedded&v=S7cSwMArjUQ" target="_blank">
<img src="http://img.youtube.com/vi/S7cSwMArjUQ/0.jpg" alt="Advanced" width="480" height="360" border="10" />
</a>

---

Target platforms
---

- API level 9 or later  (However, some animations are not supported on Gingerbread.)


Latest version
---

- Version 0.8.3  (Oct. 27, 2015)   ([RELEASE NOTES](./RELEASE-NOTES.md))

*If you are using support library v23.0.x, please use v0.8.1 instead.*


Getting started
---

This library is published on jCenter. Just add these lines to `build.gradle`.

```groovy
dependencies {
    compile ('com.h6ah4i.android.widget.advrecyclerview:advrecyclerview:0.8.3@aar'){
        transitive=true
    }
}
```

Migration Guide
---

The version 0.8.0 has fixed a lot of issues and introduced some new features. Some interface has changed and some methods/values/classes are marked as deprecated. Here is a quick guide for migrating from v0.7.x.

### Swipe

The `SwipeableItemAdapter` interface has been changed drastically.
Also, some methods and some constants are marked as deprecated because vertical swipe feature is added.

#### Recommended way
Implement the new `SwipeableItemAdapter.onItemSwipe()` method which returns `SwipeResultAction` objects.
The `SwipeResultAction` class has these three overridable methods;

- `void onPerformAction()`
  - => This method is called immediately after returning from the onItemSwipe() method. Can modify data set an
- `void onSlideAnimationEnd()`
  - => This method is called when the item slide animation is completed.
- `void onCleanUp()`
  - => This method is called after the onSlideAnimationEnd() method. Clear fields to avoid memory leak.

#### Easy way

Just change the interfaces which your adapter implementes as follows;
- `SwipeableItemAdapter` -> `LegacySwipeableItemAdapter`
- `ExpandableSwipeableItemAdapter` -> `LegacyExpandableSwipeableItemAdapter`

### Drag & Drop

:point_right: No special changes are required.

### Expand

:point_right: No special changes are required.


Usage
---

Please check the implementation of the simple examples.

- [Drag & Drop example](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_d/)
- [Swipe example](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_s/)
- [Expandable item example](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_e/)


Primary classes/interfaces
---

### Drag & Drop related classes/interfaces

| Class/Interface name                  | Description                                              |
|---------------------------------------|----------------------------------------------------------|
| [`RecyclerViewDragDropManager`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/draggable/RecyclerViewDragDropManager.java)         | Provides Drag & Drop sort operation                      |
| [`DraggableItemAdapter<T>`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/draggable/DraggableItemAdapter.java)             | Implement this interface on your RecyclerView.Adapter    |
| [`DraggableItemViewHolder`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/draggable/DraggableItemViewHolder.java)             | Implement this interface on your RecyclerView.ViewHolder |
| [`DraggableItemConstants`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/draggable/DraggableItemConstants.java)               | Constant values area decleared in this interface |


### Swiping related classes/interfaces

| Class/Interface name                  | Description                                              |
|---------------------------------------|----------------------------------------------------------|
| [`RecyclerViewSwipeManager`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/swipeable/RecyclerViewSwipeManager.java)            | Provides Swipe operation                             　  |
| [`SwipeableItemAdapter<T>`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/swipeable/SwipeableItemAdapter.java)             | Implement this interface on your RecyclerView.Adapter    |
| [`SwipeableItemViewHolder`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/swipeable/SwipeableItemViewHolder.java)             | Implement this interface on your RecyclerView.ViewHolder |
| [`SwipeableItemConstants`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/swipeable/SwipeableItemConstants.java)               | Constant values area decleared in this interface |


### Expandable item related classes/interfaces

| Class/Interface name                  | Description                                              |
|---------------------------------------|----------------------------------------------------------|
| [`RecyclerViewExpandableItemManager`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/expandable/RecyclerViewExpandableItemManager.java)   | Provides Expandable item function                    　  |
| [`ExpandableItemViewHolder`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/expandable/ExpandableItemViewHolder.java)            | Implement this interface on your RecyclerView.ViewHolder |
| [`ExpandableItemAdapter<GVH, CVH>`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/expandable/ExpandableItemAdapter.java)     | Implement this interface on your RecyclerView.Adapter    |
| [`ExpandableDraggableItemAdapter<GVH, CVH>`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/expandable/ExpandableDraggableItemAdapter.java) | (optional) Implement this interface on your RecyclerView.Adapter to support Drag & Drop sort operation |
| [`ExpandableSwipeableItemAdapter<GVH, CVH>`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/expandable/ExpandableSwipeableItemAdapter.java) | (optional) Implement this interface on your RecyclerView.Adapter to support Swipe operation |
| [`ExpandableItemConstants`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/expandable/ExpandableItemConstants.java)               | Constant values area decleared in this interface |



### RecyclerView decorations

| Class/Interface name                  | Description                                              |
|---------------------------------------|----------------------------------------------------------|
| [`ItemShadowDecorator`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/decoration/ItemShadowDecorator.java)                 | Drop shadow decoration for pre-Lollipop devices          |
| [`SimpleListDividerDecorator`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/decoration/SimpleListDividerDecorator.java)          | Simple list divider decoration                           |


### Misc.

| Class name                                 | Description                                              |
|--------------------------------------------|----------------------------------------------------------|
| [`RecyclerViewTouchActionGuardManager`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/touchguard/RecyclerViewTouchActionGuardManager.java)      | Suppress scrolling while item animations are running     |
| [`AbstractDraggableItemViewHolder`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/utils/AbstractDraggableItemViewHolder.java)          | ViewHolder class which implements boilerplate code of the  `DraggableItemViewHolder` interface      |
| [`AbstractSwipeableItemViewHolder`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/utils/AbstractSwipeableItemViewHolder.java)          | ViewHolder class which implements boilerplate code of the  `SwipeableItemViewHolder` interface      |
| [`AbstractExpandableItemViewHolder`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/utils/AbstractExpandableItemViewHolder.java)            | ViewHolder class which implements boilerplate code of the  `ExpandableItemViewHolder` interface      |
| [`AbstractDraggableSwipeableItemViewHolder`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/utils/AbstractDraggableSwipeableItemViewHolder.java) | ViewHolder class which implements boilerplate code of the `DraggableItemViewHolder` and the `SwipeableItemViewHolder` interfaces      |
| [`AbstractExpandableItemAdapter<GVH, CVH>`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/utils/AbstractExpandableItemAdapter.java)  | Adapter class which implements boilerplate code of the `ExpandableItemAdapter` interface |


License
---

This library is licensed under the [Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

See [`LICENSE`](LICENSE) for full of the license text.

    Copyright (C) 2015 Haruki Hasegawa

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
