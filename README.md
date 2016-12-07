Advanced RecyclerView
===============

This RecyclerView extension library provides Google's Inbox app like swiping, Play Music app like drag-and-drop sorting and expandable item features. Works on API level 9 or later.

[ ![Download](https://api.bintray.com/packages/h6ah4i/maven/android-advancedrecyclerview/images/download.svg) ](https://bintray.com/h6ah4i/maven/android-advancedrecyclerview/_latestVersion)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Advanced%20RecyclerView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1432)
[![Method Count](https://img.shields.io/badge/Methods and size-core: 1684 | deps: 19003 | 230 KB-e91e63.svg) ](http://www.methodscount.com/?lib=com.h6ah4i.android.widget.advrecyclerview%3Aadvrecyclerview%3A0.10.2)
---

### Download the example app

<a href="https://play.google.com/store/apps/details?id=com.h6ah4i.android.example.advrecyclerview">
<img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" width="165" height="64" />
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

- Version 0.10.2  (November 27, 2016)   ([RELEASE NOTES](./RELEASE-NOTES.md))


Getting started
---

This library is published on jCenter. Just add these lines to `build.gradle`.

```gradle
dependencies {
    compile ('com.h6ah4i.android.widget.advrecyclerview:advrecyclerview:0.10.2@aar'){
        transitive=true
    }
}
```

Usage
---

Please check the implementation of the simple examples.

### Drag & Drop related examples

- [Basic](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_d_basic/)
- [Minimal](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_d_minimal/)
- [Draggable grid](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_d_grid/)
- [Draggable staggered grid](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_d_staggered_grid/)
- [Draggable with section](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_d_with_section/)
- [Drag on Long press](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_d_on_longpress/)
- [Uses onCheckCanDrop()](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_d_check_can_drop/)

### Expandable item related examples

- [Basic](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_e_basic/)
- [Minimal](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_e_minimal/)
- [Add & Remove item](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_e_add_remove/)
- [Already expanded groups](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_e_already_expanded/)

### Swipeable related examples

- [Basic](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_s_basic/)
- [Minimal](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_s_minimal/)
- [Swipeable with button](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_s_button/)
- [Vertical](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_s_vertical/)
- [Viewpager](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_s_viewpager/)
- [Swipe on long press](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_s_longpress/)

### Headers and Footers examples
- [Minimal](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_hf_minimal)
- [Expandable with Header/Footer](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_hf_e)
- [Add & Remove Header/Footer items](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_hf_add_remove)

### WrapperAdapter examples
- [Composition of All Features](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_composition_all)
- [Insertion items](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_wa_insertion)
- [Filtering items](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_wa_filtering)

### Hybrid examples

- [Draggable & Swiping](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_ds/)
- [Draggable & Swiping with section](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_ed_with_section/)
- [Expandable & Draggable & Swiping](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_eds/)



Primary classes/interfaces
---

### WrapperAdapter related classes/interfaces

| Class/Interface name                  | Description                                              |
|---------------------------------------|----------------------------------------------------------|
| [`WrapperAdapter`<VH>](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/adapter/WrapperAdapter.java)         | Adds several methods to RecyclerView.Adapter to manage with wrapped adapters                       |
| [`ItemIdComposer`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemIdComposer.java)             | Utility class for handling packed item IDs |
| [`ItemViewTypeComposer`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemViewTypeComposer.java) | Utility class for handling packed item view types |
| [`SimpleWrapperAdapter<VH>`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/adapter/SimpleWrapperAdapter.java) | Base implementation of WrapperAdapter which wraps one adapter |

### Drag & Drop related classes/interfaces

| Class/Interface name                  | Description                                              |
|---------------------------------------|----------------------------------------------------------|
| [`RecyclerViewDragDropManager`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/draggable/RecyclerViewDragDropManager.java)         | Provides Drag & Drop sort operation                      |
| [`DraggableItemAdapter<T>`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/draggable/DraggableItemAdapter.java)             | Implement this interface on your RecyclerView.Adapter    |
| [`DraggableItemViewHolder`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/draggable/DraggableItemViewHolder.java)             | Implement this interface on your RecyclerView.ViewHolder |
| [`DraggableItemConstants`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/draggable/DraggableItemConstants.java)               | Constant values area decleared in this interface |
| [`DraggableItemAnimator`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/animator/DraggableItemAnimator.java)


### Swiping related classes/interfaces

| Class/Interface name                  | Description                                              |
|---------------------------------------|----------------------------------------------------------|
| [`RecyclerViewSwipeManager`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/swipeable/RecyclerViewSwipeManager.java)            | Provides Swipe operation                             　  |
| [`SwipeableItemAdapter<T>`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/swipeable/SwipeableItemAdapter.java)             | Implement this interface on your RecyclerView.Adapter    |
| [`SwipeableItemViewHolder`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/swipeable/SwipeableItemViewHolder.java)             | Implement this interface on your RecyclerView.ViewHolder |
| [`SwipeableItemConstants`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/swipeable/SwipeableItemConstants.java)               | Constant values area decleared in this interface |
| [`SwipeDismissItemAnimator`](library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/animator/SwipeDismissItemAnimator.java)


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

Other examples
---

| 
### iOS Mail app like swipe action

<img src="images/other_example_ios_mail.png" width="200" />

- [Repository](https://github.com/h6ah4i/RecyclerViewiOSMailAppLikeSwipe)


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
