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

NOTE: This library does not contain [Snackbar](http://www.google.com/design/spec/components/snackbars-toasts.html). The above example app uses third-party library. (thx. [Snackbar developed by William Mora ](https://github.com/nispok/snackbar)).

---

Target platforms
---

- API level 9 or later  (However, some animations are not supported on Gingerbread.)


Latest version
---

- Version 0.7.2  (June 18, 2015)

Getting started
---

This library is published on jCenter. Just add these lines to `build.gradle`.

```groovy
dependencies {
    compile 'com.h6ah4i.android.widget.advrecyclerview:advrecyclerview:0.7.2'
}
```

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


### Swiping related classes/interfaces

| Class/Interface name                  | Description                                              |
|---------------------------------------|----------------------------------------------------------|
| [`RecyclerViewSwipeManager`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/swipeable/RecyclerViewSwipeManager.java)            | Provides Swipe operation                             　  |
| [`SwipeableItemAdapter<T>`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/swipeable/SwipeableItemAdapter.java)             | Implement this interface on your RecyclerView.Adapter    |
| [`SwipeableItemViewHolder`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/swipeable/SwipeableItemViewHolder.java)             | Implement this interface on your RecyclerView.ViewHolder |


### Expandable item related classes/interfaces

| Class/Interface name                  | Description                                              |
|---------------------------------------|----------------------------------------------------------|
| [`RecyclerViewExpandableItemManager`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/expandable/RecyclerViewExpandableItemManager.java)   | Provides Expandable item function                    　  |
| [`ExpandableItemViewHolder`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/expandable/ExpandableItemViewHolder.java)            | Implement this interface on your RecyclerView.ViewHolder |
| [`ExpandableItemAdapter<GVH, CVH>`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/expandable/ExpandableItemAdapter.java)     | Implement this interface on your RecyclerView.Adapter    |
| [`ExpandableDraggableItemAdapter<GVH, CVH>`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/expandable/ExpandableDraggableItemAdapter.java) | (optional) Implement this interface on your RecyclerView.Adapter to support Drag & Drop sort operation |
| [`ExpandableSwipeableItemAdapter<GVH, CVH>`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/expandable/ExpandableSwipeableItemAdapter.java) | (optional) Implement this interface on your RecyclerView.Adapter to support Swipe operation |



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
